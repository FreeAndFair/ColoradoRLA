#!/usr/bin/env python
"""corla_test: drive testing of ColoradoRLA

Examples. Note that 'crtest' and/or corla_test should be defined
as an alias for main.py.

# Smoketest:

./main.py

# Simple loading of some data, for audit by the web client:

crtest reset
crtest dos_init
crtest county_setup

# Test 2-vote overstatement
crtest -l "Distant Loser"

# 1-vote understatement
crtest -s "22345123451234512345" -p "1 17"

# 2-vote understatement
crtest -l "Clear Winner" -s "22345123451234512345" -p "1 17"

# Test ballot-not-found
crtest -C 1 -n "8 15"

# Simple quick retrievals
crtest -E /audit-board-asm-state
crtest -e /dos-asm-state

# Display a county or DOS dashboard
crtest -E '/county-dashboard'

crtest -e '/dos-dashboard'

# Get csv file with ballot cards to be audited, random sequence numbers
crtest -E '/cvr-to-audit-download?start=0&ballot_count=9&include_duplicates'

# Check whether the /cvr-to-audit responses match the other lists, and also remove audited ballots

crtest reset dos_init county_setup dos_start
crtest -E "/cvr-to-audit-list?start=0&ballot_count=9&include_duplicates=true"
crtest county_audit
crtest -E "/cvr-to-audit-list?start=0&ballot_count=1&include_duplicates=true"
crtest -E "/cvr-to-audit-list?start=0&ballot_count=9&include_duplicates=true"

# Request as state user, specifying a county
crtest -e '/cvr-to-audit-download?round=1&county=3&include_audited&include_duplicates'

crtest -e "/cvr-to-audit-download?county=3&start=0&ballot_count=9&include_audited&include_duplicates"

# Test two county audits in parallel
(
./main.py reset dos_init
./main.py -c 3 -v 0 county_setup &
./main.py -c 5 -v 2 county_setup &
wait
./main.py dos_start -C 3
./main.py -c 5 county_audit &
./main.py -c 3 county_audit &
wait
./main.py dos_wrapup
) > multi.out

# Test two counties, one of which doesn't have a contest to audit
(
date -Is
./main.py reset dos_init
for c in 1 2; do
 ./main.py -c $c county_setup &
done
wait
./main.py dos_start
for c in 1 2; do ./main.py -c $c county_audit &
wait
done
date -Is
) 2>&1 | tee 2-counties-one-contest.out

# Parallel auditing
(
./main.py -C 1 -c 1 -c 2 -c 3 -c 4 -c 5 -c 6 -c 7 -c 8 -c 9 -c 10 -f d0-n1000-fix1.csv reset dos_init county_setup dos_start
wait
for c in 1 2 3 4 5 6 7 8 9 10; do ./main.py -c $c county_audit &  done
wait
) | tee parallelaudit.out

# Parallel uploads of bigger CVRs

(
date -Is
./main.py reset dos_init
for c in 1 2 3 4 5 6 7 8 9 10; do
 ./main.py -c $c -f neal_ignore/d0-n50000.csv county_setup &
done
wait
./main.py dos_start -C 1 
for c in 1 2 3 4 5 6 7 8 9 10; do ./main.py -c $c county_audit &
wait
done
date -Is
) 2>&1 | tee parallelcvr50000x10.out

# Miscellaneous of specific server endpoint paths

crtest -e /acvr
crtest -e /contest
crtest -e /contest/id/52253
crtest -e /contest/county?3
crtest -E /contest/county?3 -c 3

# Not working - minor missing feature:

crtest -e /acvr/county/3

TODO later:

/hand-count  IndicateHandCount

 GET /ballot-manifest (BallotManifestDownload)
 GET /acvr/county (ACVRDownloadByCounty)
 GET /cvr (CVRDownload)
 GET /cvr/id/:id (CVRDownloadByID)
 GET /ballot-manifest/county (BallotManifestDownloadByCounty)
 GET /cvr/county (CVRDownloadByCounty)
 GET /acvr (ACVRDownload)
 GET /contest (ContestDownload)
 GET /contest/id/:id (ContestDownloadByID)
 GET /contest/county (ContestDownloadByCounty)

Note: zerotest doesn't support POST operations (yet?)
See "File upload via POST request not working: Issue #12"
https://github.com/jjyr/zerotest/issues/12

TODO: to help human testers using web client, display CVRs corresponding to selected ACVRs for a given county
"""

from __future__ import (print_function, division,
                        absolute_import, unicode_literals)
import sys
import codecs
import os 
import operator
import argparse
from argparse import Namespace
import json
import time
import random
import logging
import hashlib

import requests


__author__ = "Neal McBurnett <http://neal.mcburnett.org/>"
__license__ = "GPLv3+"


parser = argparse.ArgumentParser(description='Drive testing for ColoradoRLA auditing.')

parser.add_argument('-c, --county', dest='counties', metavar='COUNTY', action='append',
                    type=int,
                    help='numeric county_id for the given command, e.g. 1 '
                    'for Adams. May be specified multiple times.')
parser.add_argument('-v, --cvr', dest='cvr', type=int,
                    default=0,
                    help='predefined cvr filename and hash: integer index to pre-defined array, default 0')
parser.add_argument('-f, --cvrfile', dest='cvrfile',
                    help='cvr filename, for arbitrary cvrs. Takes precedence over -v. '
                    'Proper hash will be computed.')
parser.add_argument('-F, --manifestfile', dest='manifestfile',
                    help='manifest filename, for arbitrary manifests. '
                    'Proper hash will be computed.')

# TODO: allow a way to specify CVRs and contests per county.
parser.add_argument('-C, --contest', dest='contests', metavar='CONTEST', action='append',
                    type=int,
                    help='numeric contest_index of contest to use for the given audit commands '
                    'E.g. 0 for first one from the CVRs. May be specified multiple times. '
                    '-1 means "audit all contests')
parser.add_argument('-l, --loser', dest='loser', default="UNDERVOTE",
                    help='Loser to use for -p, default "UNDERVOTE"')
parser.add_argument('-p, --discrepancy-plan', dest='plan', default="2 17",
                    help='Planned discrepancies. Default is "2 17", i.e. '
                    'Every 17 ACVR uploads, upload a possible discrepancy once, '
                    'when the remainder of dividing the upload index is 2. '
                    'Discrepancies thus come with the 3rd of every 17 ACVR uploads.')
parser.add_argument('-P, --discrepancy-end', dest='plan_limit', type=int, default=sys.maxint,
                    help='Last upload with possible discrepancy is # PLAN_LIMIT')

parser.add_argument('-n, --notfound-plan', dest='notfound_plan', default="-1 1",
                    help='Planned rate of ballot-not-found discrepancies. Default is "-1 1", i.e. never')

parser.add_argument('-R, --rounds', type=int, dest='rounds', default=-1,
                    help='Set maximum number of rounds. Default is all rounds.')

parser.add_argument('-r, --risk-limit', type=float, dest='risk_limit', default=0.1,
                    help='risk limit, e.g. 0.1')
parser.add_argument('-s, --seed', dest='seed',
                    default='01234567890123456789',
                    help='random seed to use: 20 or more digts')
parser.add_argument('-u, --url', dest='url',
                    default='http://localhost:8888',
                    help='base url of corla server. Defaults to http://localhost:8888. '
                    'Use something like http://example.gov/api when running '
                    'against a full installation.')
parser.add_argument('-e, --dos-endpoint', dest='dos_endpoint',
                    help='do an HTTP GET from the given endpoint, authenticated as state admin.')
parser.add_argument('-E, --county-endpoint', dest='county_endpoint',
                    help='do an HTTP GET from the given endpoint, authenticated as a county.')
parser.add_argument('-i, --state-imported', dest='state_imported', action='store_true',
                    help='show CVRs imported for all counties')
parser.add_argument('-I, --county-imported', dest='county_imported', action='store_true',
                    help='show CVRs imported for given counties')

parser.add_argument('--hand-count', dest='hand_counts', type=int, metavar='CONTEST', action='append',
                    help='Declare a hand-count for the given numeric contest_index')
parser.add_argument('--download-file', dest='download_file', type=int, metavar='FILE_ID',
                    help='Just download file with given FILE_ID')
                    # help='Just list files and download selected ones')
parser.add_argument('-S, --check-audit-size', type=bool, dest='check_audit_size',
                    help='Check calculations of audit size. Requires rlacalc, psycopg2')

parser.add_argument('-T, --time-delay', type=float, dest='time_delay', default=0.0,
                    help='Maximum time to pause before network requests. Default 0.0. '
                    'Actual pauses will be uniformly distributed between 0 and the maximum')
parser.add_argument('-L, --lower-time-delay', type=float, dest='lower_time_delay', default=0.0,
                    help='Minimum time to pause before network requests. Default 0.0. '
                    'Actual pauses will be uniformly distributed between this and the maximum')

# TODO: get rid of this and associated old code when /upload-cvr-export and /upload-cvr-export go away
parser.add_argument('-Y, --ye-olde-upload', type=bool, dest='ye_olde_upload',
                    help='use old file upload protocol')

parser.add_argument('-t, --trackstates', type=bool, dest='trackstates',
                    default=False,
                    help='Show state after most requests')

parser.add_argument('-d, --debuglevel', type=int, default=logging.WARNING, dest='debuglevel',
  help='Set logging level to debuglevel: DEBUG=10, INFO=20,\n WARNING=30 (the default), ERROR=40, CRITICAL=50')

parser.add_argument('commands', metavar="COMMAND", nargs='*',
                    help='audit commands to run. May be specified multiple times. '
                    'Possibilities: reset dos_init county_setup dos_start county_audit dos_wrapup')


class Pause(object):
    """Provide a configurable sleep delay.
    Just set Pause.max_pause directly when you want the default to change"
    """

    min_pause = 0.0
    max_pause = 0.0

    @classmethod
    def pause_hook(self, r, *args, **kwargs):
        """A hook for a Requests response, which pauses a random amount of time,
        between 0.0 and the maximum pause configured for the class
        """

        time.sleep(random.uniform(self.min_pause, self.max_pause))


def requests_retry_session(retries=3, backoff_factor=2, method_whitelist=False,
                           status_forcelist=(429, 502, 503), session=None):
    """Return a Requests session that retries for the given status codes, and
    for connection timeouts.
    The default of method_whitelist=False means that it retries all HTTP
    methods, even POST, which should be OK given the default status_forcelist.

    Inspired by Peter Bengtsson https://www.peterbe.com/plog/best-practice-with-retries-with-requests

    To test 503 error and connection timeout:
     crtest -e '' -d 10 -u http://httpbin.org/status/503?
     crtest -e '' -d 10 -u http://10.255.255.1/?
    """

    session = session or requests.Session()
    session.hooks = dict(response=Pause.pause_hook)  # Why isn't this an argument to the constructor?
    retry = requests.packages.urllib3.util.retry.Retry(
        total=retries,
        connect=retries,
        read=retries,
        status=retries,
        method_whitelist=method_whitelist,
        status_forcelist=status_forcelist,
        backoff_factor=backoff_factor,
        raise_on_status=False
    )
    adapter = requests.adapters.HTTPAdapter(max_retries=retry)
    session.mount('http://', adapter)
    session.mount('https://', adapter)
    return session


def state_login(ac, s):
    "Login as state admin in given requests session"

    path = "/auth-state-admin"
    r = s.post(ac.base + path,
               data={'username': 'stateadmin1', 'password': '', 'second_factor': ''})
    r = s.post(ac.base + path,
               data={'username': 'stateadmin1', 'password': '', 'second_factor': ''})
    ac.logconsole.info("%s %s %s", r, "POST", path)


def county_login(ac, s, county_id):
    "Login as county admin in given requests session"

    path = "/auth-county-admin"
    r = s.post(ac.base + path,
               data={'username': 'countyadmin%d' % county_id, 'password': '', 'second_factor': ''})
    r = s.post(ac.base + path,
               data={'username': 'countyadmin%d' % county_id, 'password': '', 'second_factor': ''})
    ac.logconsole.info("%s %s %s", r, "POST", path)


def test_endpoint_json(ac, s, path, data, show=True):
    "Do a generic test of an endpoint that posts the given data to the given path"

    if ac.args.trackstates:
        show=True  # Make sure we show the action before showing the resulting state

    r = s.post(ac.base + path, json=data)
    if r.status_code == 200:
        if show:
            ac.logconsole.info("%s %s %s", r, "POST", path)
    else:
        if show:
            ac.logconsole.info("%s %s %s %s", r, "POST", path, r.text)
        else:
            ac.logconsole.info("%s %s %s", r, "POST", path)

    if ac.args.trackstates:
        r = test_endpoint_get(ac, ac.state_s, "/dos-asm-state", show=False)

        if 'current_state' in r.json():
            print("DOS: %s" % r.json()['current_state'])
        else:
            print("smoketest sees no current state", r.text)

        if s != ac.state_s:
            print("County: %s" % test_endpoint_get(ac, s, "/audit-board-asm-state", show=False).json()['current_state'])
            
    return r


def test_endpoint_get(ac, s, path, show=True):
    "Do a generic test of an endpoint that gets the given path"

    r = s.get(ac.base + path)
    if r.status_code == 200:
        if show:
            ac.logconsole.info("%s %s %s", r, "GET", path)
    else:
        if show:
            ac.logconsole.info("%s %s %s %s", r, "GET", path, r.text)
        else:
            ac.logconsole.info("%s %s %s", r, "GET", path)
    return r


def get_imported_count(dashboard):
    """Pull counts out of county-dashboard
    Return (ASM_state, summary)
    """

    imported_count = dashboard.get('cvr_export_count', None)
    if 'cvr_export_file' in dashboard:
        approximate_record_count = dashboard['cvr_export_file']['approximate_record_count']
    else:
        approximate_record_count = 0

    return (dashboard['asm_state'],
            "Parsed about %d of about %d CVRs" %
                (imported_count, approximate_record_count))


def upload_file(ac, s, import_path, filename, sha256):
    """Upload the named file, specifying the given sha256 hash.
    import_path is either '/import-cvr-export' or '/import-ballot-manifest'
    """

    with open(filename, 'rb') as f:
        path = "/upload-file"
        payload = {'hash': sha256}
        r = s.post(ac.base + path,
                          files={'file': f}, data=payload)

    if r.status_code != 200:
        print(r, "POST", path, r.text)

    logging.debug("%s %s %s" % (r, path, r.text))

    import_handle = r.json()

    r = test_endpoint_json(ac, s, import_path, import_handle)
    if r.status_code != 200:
        print(r, "POST", import_path, r.text)

    logging.debug("%s %s %s" % (r, import_path, r.text))

    if import_path == "/import-cvr-export":
        while True:
            # wait for the verdict on the CVR export
            r = test_endpoint_get(ac, s, "/county-dashboard")
            dashboard = r.json()
            (state, summary) = get_imported_count(dashboard)

            logging.info(summary)

            if state not in ["CVRS_IMPORTING", "BALLOT_MANIFEST_OK_AND_CVRS_IMPORTING"]:
                print("CVR import complete, state: %s" % state)
                break

            time.sleep(30)


def download_file(ac, s, file_id, filename):
    "Download the previously-uploaded file with the given file_id to the given filename"

    with open(filename, 'wb') as f:
        path = "/download-file"
        r = s.get(ac.base + path, params={'file_info': json.dumps({'file_id': "%d" % file_id})})

    if r.status_code != 200:
        print(r, "GET", path, r.text)

    logging.debug("%s %s" % (r, path))

    with open(filename, "wb") as f:
        f.write(r.content)
        print("file_id %d saved as %s" % (file_id, filename))

def upload_cvrs(ac, s, filename, sha256):
    "Upload cvrs"

    with open(filename, 'rb') as f:
        path = "/upload-cvr-export"
        # TODO: make this generic for any county
        payload = {'county': '3', 'hash': sha256}
        r = s.post(ac.base + path,
                          files={'cvr_file': f}, data=payload)
    print(r, "POST", path, r.text)


def upload_manifest(ac, s, filename, sha256):
    "Upload manifest"

    with open(filename, 'rb') as f:
        path = "/upload-ballot-manifest"
        payload = {'county': 'Arapahoe', 'hash': sha256}
        r = s.post(ac.base + path,
                          files={'bmi_file': f}, data=payload)
    print(r, "POST", path)


def get_county_cvrs(ac, county_id, s):
    "Return all cvrs uploaded by a given county"

    path = x
    r = s.get("%s/cvr/%d" % (ac.base, county_id))
    if r.status_code != 200:
         print(r, "GET", path, r.text)
    cvrs = r.json()

    return cvrs


def get_cvrs(ac, s):
    "Return all cvrs uploaded by any county"

    r = s.get("%s/cvr" % ac.base)
    cvrs = r.json()

    return cvrs


def publish_ballots_to_audit(seed, cvrs):
    """Return lists by county of ballots to audit.
    """

    import sampler

    county_ids = set(cvr['county_id'] for cvr in cvrs)

    ballots_to_audit = []
    for county_id in county_ids:
        county_cvrs = sorted( (cvr for cvr in cvrs if cvr['county_id'] == county_id),
                              key=lambda cvr: "%s-%s-%s" % (cvr['scanner_id'], cvr['batch_id'], cvr['record_id']))
        N = len(county_cvrs)
        # n is based on auditing Regent contest.
        # TODO: perhaps calculate from margin etc
        n = 11
        seed = "01234567890123456789"

        _, new_list = sampler.generate_outputs(n, True, 0, N, seed, False)

        logging.debug("Random selections, N=%d, n=%d, seed=%s: %s" %
                      (N, n, seed, new_list))

        selected = []
        for i, cvr in enumerate(county_cvrs):
            if i in new_list:
                cvr['record_type'] = 'AUDITOR_ENTERED'
                selected.append(cvr)
                logging.info("Selected cvr %d: id: %d RecordID: %s" % (i, cvr['id'], cvr['imprinted_id']))

        ballots_to_audit.append([county_id, selected])

    return ballots_to_audit


def compute_hash(filename):
    "Compute and return the sha-256 hash of a file"

    BUF_SIZE = 2 ** 18

    sha256 = hashlib.sha256()

    with open(filename, 'rb') as f:
        while True:
            data = f.read(BUF_SIZE)
            if not data:
                break
            sha256.update(data)

    return sha256.hexdigest()


def upload_files(ac, s):
    """Directly upload files, which zerotest doesn't support.
    See "File upload via POST request not working: Issue #12"
     https://github.com/jjyr/zerotest/issues/12
    """

    if ac.args.manifestfile is None:
        manifestfile = "../e-1/arapahoe-manifest.csv"
        hash = "42d409d3394243046cf92e3ce569b7078cba0815d626602d15d0da3e5e844a94"
    else:
        manifestfile = ac.args.manifestfile
        hash = compute_hash(manifestfile)

    if ac.args.ye_olde_upload:
        dir_path = os.path.dirname(os.path.realpath(__file__))
        upload_manifest(ac, s, os.path.join(dir_path, manifestfile), hash)
    else:
        upload_file(ac, s, '/import-ballot-manifest', manifestfile, hash)

    predefined_cvrs = (
        ("../e-1/arapahoe-regent-3-clear-CVR_Export.csv",
                    "49bd5d56e6107ff6b7381a6f563121e3b1d5d967bba1c29e6ffe31583d646e6d"),
        ("../dominion-2017-CVR_Export_20170310104116.csv",
                    "4e3844b0dabfcea64a499d65bc1cdc00d139cd5cdcaf502f20dd2beaa3d518d2"),
        ("../Denver2016Test/CVR_Export_20170804111144.csv",
                    "1def4aa4c0e1421b4e5adcd4cc18a8d275f709bc07820a37e76e11a038195d02"),
        ("../e-1/arapahoe-regent-3-clear-CVR_Export.csv",
                    "invalid hash"),
        ("../e-1/arapahoe-regent-3-clear-CVR_Export.csv",
                    "00000111111111122222222234444444444456789999999abbbbbbbbbcccddef"),
    )

    if ac.args.cvrfile is None:
        cvrfile, hash = predefined_cvrs[ac.args.cvr]
    else:
        cvrfile = ac.args.cvrfile
        hash = compute_hash(cvrfile)

    if ac.args.ye_olde_upload:
        upload_cvrs(ac, s, os.path.join(dir_path, cvrfile), hash)
    else:
        upload_file(ac, s, '/import-cvr-export', cvrfile, hash)

def get_county_dashboard(ac, county_s, county_id, i=0, acvr={'id': -1}, show=True):
    "Get and show useful info about /county-dashboard"

    r = test_endpoint_get(ac, county_s, "/county-dashboard", show=False)
    county_dashboard = r.json()

    total_audited = 1 + county_dashboard['audited_ballot_count']

    if show:
        logging.debug("county-dashboard: %s" % r.text)
        print("Round %d, county %d, upload %d, prefix %d: aCVR %d; ballots_remaining_in_round: %d, optimistic_ballots_to_audit: %s est %s" %
              (ac.round, county_id, total_audited, county_dashboard.get('audited_prefix_length', -1), acvr['id'],  # FIXME
               county_dashboard['ballots_remaining_in_round'], county_dashboard['optimistic_ballots_to_audit'], county_dashboard['estimated_ballots_to_audit']))


        """ Put this back in when estimated_ballots_to_audit makes sense again
        print("Round %d, county %d, upload %d, prefix %d: aCVR %d; ballots_remaining_in_round: %d, estimated_ballots_to_audit: %s" %
              (ac.round, county_id, total_audited, county_dashboard.get('audited_prefix_length', -1), acvr['id'],  # FIXME
               county_dashboard['ballots_remaining_in_round'], county_dashboard['estimated_ballots_to_audit']))
        """

    return county_dashboard

def server_sequence():
    '''Run thru a given test sequence to explore server ASM transitions.
    TODO: needs lots of work to easily handle a full Eulerian traversal
    of all transitions in the state graphs.
    '''

    test_get_cvr()
    test_get_ballot_manifest()
    test_get_contest()
    test_get_cvr_county_Arapahoe()
    test_get_ballot_manifest_county_Arapahoe()
    test_get_contest_id_2()
    test_get_contest_county_Arapahoe()


def reset(ac):
    'Reset the database, leaving only authentication info'

    r = test_endpoint_json(ac, ac.state_s, "/reset-database", {})


def dos_init(ac):
    'Run initial Dept of State steps: audit definition, risk_limit etc.'

    r = test_endpoint_json(ac, ac.state_s, "/update-audit-info",
                           { "election_type": "coordinated",
                             "election_date": "2017-11-09T02:00:00Z",
                             "public_meeting_date": "2017-11-19T02:00:00Z",
                             "risk_limit": ac.args.risk_limit } )

def county_setup(ac, county_id):

    logging.debug("county setup for county_id %d" % county_id)

    county_s = requests_retry_session()
    county_login(ac, county_s, county_id)

    upload_files(ac, county_s)

    r = test_endpoint_get(ac, county_s, "/contest/county?%d" % county_id)
    contests = r.json()

    # TODO: get count again: print("Uploaded table of %d CVRs with %d contests" % (, len(contests)))
    print("Uploaded CVR table with %d contests" % (len(contests),))

    # TODO perhaps cleanup - but is it more realistic to just leave sessions open?
    # county_s.close()


def dos_start(ac):
    'Run DOS steps to start the audit, enabling county auditing to begin: contest selection, seed, etc.'

    if len(ac.audited_contests) <= 0:
        print("No contests to audit, status_code = %d" % r.status_code)
        return

    for contest_id in ac.audited_contests:
        r = test_endpoint_json(ac, ac.state_s, "/select-contests",
                               [{"contest": contest_id,
                                 "reason": "COUNTY_WIDE_CONTEST",
                                 "audit": "COMPARISON"}])

    r = test_endpoint_json(ac, ac.state_s, "/random-seed",
                           {'seed': ac.args.seed})

    r = test_endpoint_json(ac, ac.state_s, "/start-audit-round",
                           { "multiplier": 1.0, "use_estimates": True})
    # print(r.text)
    # with use_estimates: False:    "county_ballots": { "ID": number, "ID": number, ... }
    # only the counties listed in it have rounds started

    r = test_endpoint_get(ac, ac.state_s, "/dos-dashboard")
    if r.status_code == 200:
        dos_dashboard = r.json()
        for contest_id, reason in dos_dashboard['audited_contests'].items():
            r = test_endpoint_get(ac, ac.state_s, "/contest/id/%s" % contest_id)
            contest = r.json()
            print("Audit driver in county {county_id}, contest {id}: vote for {votes_allowed} in {name}".format(**contest))
            for choice in contest['choices']:
                print("  %s" % choice['name'])

        for county_id, status in dos_dashboard['county_status'].items():
            if status['estimated_ballots_to_audit'] != 0:
                print("County %s has initial sample size of %s sample interpretations, including duplicates" % 
                      (county_id, status['estimated_ballots_to_audit']))
                print("ballots_remaining_in_round %d: %d" %
                      (ac.round, status['ballots_remaining_in_round']))
    logging.debug("dos-dashboard: %s" % r.text)

def county_audit(ac, county_id):
    'Audit board uploads ACVRs from a county. Return estimated remaining ballots to audit'

    county_s = requests_retry_session()
    county_login(ac, county_s, county_id)

    # Note: we take advantage of a side effect of this also: print where we're at....
    county_dashboard = get_county_dashboard(ac, county_s, county_id, -1)

    if county_dashboard['asm_state'] == "COUNTY_AUDIT_COMPLETE":
        return(True)

    audit_board_set = [{"first_name": "Mary",
                        "last_name": "Doe",
                        "political_party": "Democrat"},
                       {"first_name": "John",
                        "last_name": "Doe",
                        "political_party": "Republican"}]

    r = test_endpoint_get(ac, county_s, "/audit-board-asm-state")
    if ((r.json()['current_state'] == "WAITING_FOR_ROUND_START_NO_AUDIT_BOARD") or
        (r.json()['current_state'] == "ROUND_IN_PROGRESS_NO_AUDIT_BOARD")):
        r = test_endpoint_json(ac, county_s, "/audit-board-sign-in", audit_board_set)

    # Print this tool's notion of what should be audited, based on seed etc.
    # for auditing the audit.
    # TODO or FIXME - doesn't yet match "ballots_to_audit" from the dashboard
    # logging.log(5, json.dumps(publish_ballots_to_audit(ac.args.seed, cvrs), indent=2))

    # r = test_endpoint_get(ac, county_s, "/audit-board-asm-state")

    round = len(county_dashboard['rounds'])
    r = test_endpoint_get(ac, county_s, "/cvr-to-audit-download?round=%d" % round)
    r = test_endpoint_get(ac, county_s, "/cvr-to-audit-list?round=%d" % round)
    selected = r.json()

    print("Retrieved ballots_to_audit, got %d" % len(selected))
    if len(selected) != county_dashboard['ballots_remaining_in_round']:
        print("ERROR: got %d CVR ids in ballots_to_audit, but ballots_remaining_in_round is %d in county-dashboard" %
              (len(selected), county_dashboard['ballots_remaining_in_round']))

    # For each of a a bunch of selected cvrs,
    #   make it into a matching acvr and upload it, watching progress
    # TODO: upload the right number of them....

    if len(selected) < 1:
        print("No ballots_to_audit")

    for i in range(len(selected)):
        if ac.args.debuglevel >= logging.INFO:
            r = test_endpoint_get(ac, ac.state_s, "/dos-dashboard", show=False)
            discrepancies = ""
            contest_discrepancies = r.json().get('discrepancy_count', {})
            for contest_id, d in contest_discrepancies.iteritems():
                discrepancies += "%s %2d %2d %2d %2d %2d  " % (contest_id, d["2"], d["1"], d["0"], d["-1"], d["-2"])
            print(discrepancies)

        if i % 50 == 5:
            r = test_endpoint_json(ac, county_s, "/audit-board-sign-out", {});
            r = test_endpoint_get(ac, county_s, "/audit-board-asm-state")
            # print(r.text)
            r = test_endpoint_json(ac, county_s, "/audit-board-sign-in", audit_board_set)
            r = test_endpoint_get(ac, county_s, "/audit-board-asm-state")
            # print(r.text)

        r = test_endpoint_get(ac, county_s, "/cvr/id/%d" % selected[i]['db_id'], show=False)
        acvr = r.json()
        logging.debug("Original CVR: %s" % json.dumps(acvr))
        acvr['record_type'] = 'AUDITOR_ENTERED'

        total_audited = county_dashboard['audited_ballot_count']
        # print("total_audited: %d" % total_audited)

        # Modify the aCVR sometimes.
        if (total_audited % ac.discrepancy_cycle == ac.discrepancy_remainder
              and  total_audited <= ac.args.plan_limit):

            # TODO: use contest info to look for the contests and add votes for losers 

            message = "No Discrepancy, contest %d not in this CVR" % ac.audited_contests[0]
            for ci in acvr['contest_info']:
                if ci['contest'] == ac.audited_contests[0]:
                    message = "Choice wouldn't change"
                    if ci['choices'] != ac.false_choices:
                        message = "Discrepancy: %s in %d, was %s" % (ac.false_choices, ac.audited_contests[0], ci['choices'])
                        ci['choices'] = ac.false_choices
                    break
            print(message)

        elif False:
            # Test: make uploaded cvr not match
            # TODO: Decide what API should be for mismatch in contests between CVR and paper
            if len(acvr['contest_info']) > 0:
                del acvr['contest_info'][0]

        # Either submit a not-found discrepancy, or submit the aCVR
        if (total_audited % ac.nf_discrepancy_cycle == ac.nf_discrepancy_remainder
            and  total_audited <= ac.args.plan_limit):
            print('ballot-not-found for %s' % acvr['contest_info'])
            r = test_endpoint_json(ac, county_s, "/ballot-not-found", {'id': acvr['id']})
        else:
            logging.debug("Submitting aCVR: %s" % json.dumps(acvr))
            test_endpoint_json(ac, county_s, "/upload-audit-cvr",
                               {'cvr_id': selected[i]['db_id'], 'audit_cvr': acvr}, show=False)

        county_dashboard = get_county_dashboard(ac, county_s, county_id, i, acvr)
        if county_dashboard['asm_state'] == "COUNTY_AUDIT_COMPLETE":
            break

    r = test_endpoint_json(ac, county_s, "/sign-off-audit-round", audit_board_set)

    remaining = county_dashboard['estimated_ballots_to_audit']
    if remaining <= 0:
        print("\nCounty %d Audit completed after %d ballots" % (county_id, total_audited + 1))

    return(remaining)


def download_report(ac, s, path, extension):
    "Download and save the given report, adding the given extension"

    r = test_endpoint_get(ac, s, "/%s" % path)
    name = "%s.%s" % (path, extension)
    with open(name, "wb") as f:
        f.write(r.content)
        print("/%s report saved as %s" % (path, name))


def county_wrapup(ac, county_id):
    'Audit board summary, wrapup, audit-report'

    county_s = requests_retry_session()
    county_login(ac, county_s, county_id)

    county_dashboard = get_county_dashboard(ac, county_s, county_id)
    logging.info("county-dashboard: %s" % county_dashboard)

    rounds = len(county_dashboard['rounds'])

    print("Rounds: %s " % json.dumps(county_dashboard['rounds'], indent=2))

    if (rounds > 1)  and  county_dashboard['rounds'][rounds - 1]['actual_count'] == 0:
        # we didn't actually start the last round
        rounds -= 1

    to_go = county_dashboard['estimated_ballots_to_audit']
    audited = county_dashboard['audited_ballot_count']
    cvr_count = county_dashboard['cvr_export_count']

    if county_dashboard['asm_state'] == "COUNTY_AUDIT_COMPLETE":
        print("\nCounty %d audit complete, ended after %d ballots (of %d exported) and %d rounds, %d to go" %
              (county_id, audited, cvr_count, rounds, to_go))
    else:
        print("\nCounty %d audit incomplete, ended after %d ballots (of %d exported) and %d rounds, %d to go, state %s" %
              (county_id, audited, cvr_count, rounds, to_go, county_dashboard['asm_state']))

    # TODO: Replaced by audit board sign out? Gone?
    # r = test_endpoint_json(ac, county_s, "/intermediate-audit-report", {})
    #  and avoid   "result": "/intermediate-audit-report attempted to apply illegal event SUBMIT_INTERMEDIATE_AUDIT_REPORT_EVENT from state AUDIT_COMPLETE"

    download_report(ac, county_s, "county-report", "xlsx")

def dos_wrapup(ac):

    r = test_endpoint_get(ac, ac.state_s, "/dos-dashboard")
    logging.info("dos-dashboard: %s" % r.text)

    # r = test_endpoint_json(ac, ac.state_s, "/publish-report", {})

    download_report(ac, ac.state_s, "state-report", "xlsx")


discrepancy_query = """
-- Retrieve counts of audited ballot cards and each type of discrepancy by contest
-- along with contest ballot counts, outcomes and margins for checking calculations.

SELECT
  contest.name,
  contest.id,
  contest.winners_allowed,
  county_contest_comparison_audit.one_vote_over_count,
  county_contest_comparison_audit.one_vote_under_count,
  county_contest_comparison_audit.two_vote_over_count,
  county_contest_comparison_audit.two_vote_under_count,
  county_contest_comparison_audit.id,
  county_contest_comparison_audit.audit_reason,
  county_contest_comparison_audit.audit_status,
  county_contest_comparison_audit.audited_sample_count,
  county_contest_comparison_audit.disagreement_count,
  county_contest_comparison_audit.estimated_samples_to_audit,
  county_contest_comparison_audit.estimated_recalculate_needed,
  county_contest_comparison_audit.gamma,
  county_contest_comparison_audit.optimistic_recalculate_needed,
  county_contest_comparison_audit.optimistic_samples_to_audit,
  county_contest_comparison_audit.risk_limit,
  contest.county_id,
  county_contest_result.min_margin,
  county_contest_result.winners,
  county_contest_result.losers,
  county_contest_result.county_ballot_count,
  county_contest_result.contest_ballot_count
FROM
  public.county_contest_comparison_audit,
  public.contest,
  public.county_contest_result
WHERE
  county_contest_comparison_audit.contest_id = contest.id AND
  county_contest_comparison_audit.contest_result_id = county_contest_result.id
ORDER BY contest.county_id
;
"""


def check_audit_size(ac):
    """Check the RLA calculations for each contest, i.e. that
    optimistic_samples_to_audit matches the rlacalc python implementation (nmin()),
    and confirming that audited_sample_count >= nmin()

    It also estimates the round size using the same math that is in
    ColoradoRLA 1.0 (called togo_1_0) and checks that against
    'estimated_samples_to_audit'.

    It prints out the calculated values, and also prints an ERROR message if
    the checks fail.

    This function acquires data directly from the database via an SQL query,
    and requires the psycopg2 and rlacalc python modules.

    The ``-C -1`` option should be used so that ColoradoRLA calculates parameters
    for each contest, or else values for 'estimated_samples_to_audit' will be 0
    for the unaudited contests.
    """

    import math

    import rlacalc
    import psycopg2
    import psycopg2.extras

    con = psycopg2.connect("dbname='corla'")

    cur = con.cursor(cursor_factory=psycopg2.extras.DictCursor)
    cur.execute(discrepancy_query)
    rows = cur.fetchall()

    for r in rows:
        logging.info("check_audit_size for %s" % r.items())

        params = {'alpha': float(r['risk_limit']),
                  'gamma': float(r['gamma']),
                  'margin': r['min_margin'] / r['county_ballot_count'],
                  'o1': r['one_vote_over_count'],
                  'o2': r['two_vote_over_count'],
                  'u1': r['one_vote_under_count'],
                  'u2': r['two_vote_under_count'] }

        nmin_size = rlacalc.nmin(**params)
        params['audited'] = r['audited_sample_count']

        if params['audited'] > 0:
            togo_size = rlacalc.nminToGo(**params)
            togo_1_0 = math.ceil(nmin_size * (1 + (params['o1'] + params['o2']) / params['audited']))
        else:
            togo_size = nmin_size
            togo_1_0 = nmin_size

        if r['estimated_samples_to_audit'] != togo_1_0:
                print("ERROR: r['estimated_samples_to_audit'] %d != togo_1_0 %d]" %
                      (r['estimated_samples_to_audit'], togo_1_0))

        print("County {} nmin={:.0f} nminToGo={:.0f} est={} alpha={alpha:.0%} gamma={gamma} margin={margin:.2%}, disc={o2} {o1} {u1} {u2} for contest {}".format(
            r['county_id'], nmin_size, togo_size, r['estimated_samples_to_audit'], r['name'], **params))

        if (r['audit_reason'] != 'OPPORTUNISTIC_BENEFITS'  and
            r['audit_status'] == 'RISK_LIMIT_ACHIEVED'):
            if r['optimistic_samples_to_audit'] != nmin_size:
                print("ERROR: r['optimistic_samples_to_audit' %d != nmin_size %d]" %
                      (r['audited_sample_count'], nmin_size))
            if r['audited_sample_count'] < nmin_size:
                print("ERROR: r['audited_sample_count' %d < nmin_size %d]" %
                      (r['audited_sample_count'], nmin_size))

def main():
    # Get unbuffered output
    sys.stdout = os.fdopen(sys.stdout.fileno(), 'w', 0)
    sys.stdout = codecs.getwriter('utf8')(sys.stdout)

    # Establist an "audit context", abbreviated ac, for passing state around.
    ac = Namespace()

    ac.args = parser.parse_args()
    FORMAT = '%(asctime)-15s %(levelname)s %(name)s %(message)s'
    logging.basicConfig(stream=sys.stdout, level=ac.args.debuglevel, format=FORMAT)

    # Define a standalone logger to get timestamped results sent to stdout
    # creating a nice "print" statement with additional context added in
    ac.logconsole = logging.getLogger('console')
    ac.logconsole.propagate = False
    ac.logconsole.setLevel(logging.INFO)
    console = logging.StreamHandler(stream=sys.stdout)
    formatter = logging.Formatter(fmt='%(asctime)s.%(msecs)03d %(message)s', datefmt='%Y-%m-%d %H:%M:%S')
    console.setFormatter(formatter)
    ac.logconsole.addHandler(console)

    # Add a default county here to work around https://bugs.python.org/issue16399
    if ac.args.counties is None:
        ac.args.counties = [3]

    # Add a default contest
    if ac.args.contests is None:
        ac.args.contests = [0]

    # If no commands are listed, enter all of them
    if len(ac.args.commands) == 0:
        ac.args.commands = ["reset", "dos_init", "county_setup",
                            "dos_start", "county_audit", "dos_wrapup"]

    fields = [int(f) for f in ac.args.plan.split()]
    ac.discrepancy_remainder, ac.discrepancy_cycle = fields

    fields = [int(f) for f in ac.args.notfound_plan.split()]
    ac.nf_discrepancy_remainder, ac.nf_discrepancy_cycle = fields

    ac.logconsole.info("Arguments: %s" % ac.args)

    Pause.max_pause = ac.args.time_delay
    Pause.min_pause = ac.args.lower_time_delay
    # Start off with a pause, since others are inserted at end of requests.
    Pause.pause_hook(None)

    ac.round = 1

    ac.base = ac.args.url

    loser=ac.args.loser
    if loser == "UNDERVOTE":
        ac.false_choices = []
    else:
        ac.false_choices = [loser]

    if ac.args.commands == ['county_setup'] or ac.args.county_endpoint is not None:
        # Assuming --trackstates is not on, don't need state session.
        # Avoid possible database locking problems with logging in as
        # state admin from many clients at once in performance testing.

        logging.info("Skipping state_login()")
        ac.state_s = None
    else:
        ac.state_s = requests_retry_session()
        state_login(ac, ac.state_s)

    # These options imply exit after running a single action
    if ac.args.county_endpoint is not None:
        for county_id in ac.args.counties:
            county_s = requests_retry_session()
            county_login(ac, county_s, county_id)

            r = test_endpoint_get(ac, county_s, ac.args.county_endpoint)
            ac.logconsole.info("%s %s %s %s", r, "GET", ac.args.county_endpoint, r.text)

        sys.exit(0)

    if ac.args.dos_endpoint is not None:
        r = test_endpoint_get(ac, ac.state_s, ac.args.dos_endpoint)
        print(r, "GET", ac.args.dos_endpoint, r.text)
        sys.exit(0)

    if ac.args.county_imported:
        for county_id in ac.args.counties:
            county_s = requests_retry_session()
            county_login(ac, county_s, county_id)

            r = test_endpoint_get(ac, county_s, "/county-dashboard")
            dashboard = r.json()
            (state, summary) = get_imported_count(dashboard)

            print("County %d: %s" % (county_id, summary))

        sys.exit(0)

    if ac.args.state_imported:
        r = test_endpoint_get(ac, ac.state_s, "/dos-dashboard")
        dos_dashboard = r.json()
        for county_id, status in sorted(dos_dashboard['county_status'].items(),
                                        key=lambda t: int(t[0])):
            county_id = int(county_id)
            (state, summary) = get_imported_count(status)
            print("County %d: %s" % (county_id, summary))

        sys.exit(0)

    if ac.args.hand_counts:
        r = test_endpoint_get(ac, ac.state_s, "/contest")
        contests = r.json()

        for contest_index in ac.args.hand_counts:
            if contest_index >= len(contests):
                logging.error("Contest_index %d out of range: only %d contests in election" %
                              (contest_index, len(contests)))
                sys.exit(1)
            r = test_endpoint_json(ac, ac.state_s, "/hand-count",
                       [{"contest": contests[contest_index]['id'],
                         "reason": "COUNTY_WIDE_CONTEST",
                         "audit": "HAND_COUNT"}])
        sys.exit(0)

    if ac.args.download_file:
        download_file(ac, ac.state_s, ac.args.download_file, "/tmp/testdownload.csv")
        sys.exit(0)


    if "reset" in ac.args.commands:
        reset(ac)

    if "dos_init" in ac.args.commands:
        dos_init(ac)

    if "county_setup" in ac.args.commands:
        for county_id in ac.args.counties:
            county_setup(ac, county_id)

    if ac.args.commands == ['county_setup']:
        # All done. Avoid inquiring about contests, etc.
        sys.exit(0)

    # Establish which contests are being audited
    # Note, this may be a separate run of crtest
    r = test_endpoint_get(ac, ac.state_s, "/contest")
    contests = r.json()

    for i, contest in enumerate(contests):
        print("Contest {}: vote for {votes_allowed} in {name}".format(i, **contest))

    logging.log(5, "Contests: %s" % contests)

    ac.audited_contests = []

    # -1 is a special value meaning "audit all contests"
    if ac.args.contests[0] == -1:
        ac.args.contests = range(len(contests))

    for contest_index in ac.args.contests:
        if contest_index >= len(contests):
            logging.debug("Warning: Contest_index %d out of range: only %d contests in election (but we may just be in an early phase)" %
                          (contest_index, len(contests)))
            break

        ac.audited_contests.append(contests[contest_index]['id'])

    if "dos_start" in ac.args.commands:
        dos_start(ac)

    print()

    if "county_audit" in ac.args.commands:
        round = 0
        alldone = False

        while (ac.args.rounds == -1) or (round < ac.args.rounds):
            if ac.args.check_audit_size:
                check_audit_size(ac)

            r = test_endpoint_get(ac, ac.state_s, "/dos-asm-state")
            current_state = r.json()['current_state']
            if current_state == "DOS_AUDIT_COMPLETE":
                alldone = True
                break
            elif current_state != "DOS_AUDIT_ONGOING":
                print("Not in DOS_AUDIT_ONGOING state, can't audit")
                break
            round += 1
            print("Start Round %d" % round)
            for county_id in ac.args.counties:
                # TODO: really needs to track each individual county for being done....
                remaining = county_audit(ac, county_id)

            print()
            ac.round += 1
            # Note, may get Illegal transition on ASM... (DOS_AUDIT_COMPLETE, DOS_START_ROUND_EVENT)
            r = test_endpoint_json(ac, ac.state_s, "/start-audit-round",
                                   { "multiplier": 1.0, "use_estimates": True})

        if alldone:
            print("State audit complete")

        for county_id in ac.args.counties:
            county_wrapup(ac, county_id)

    if "dos_wrapup" in ac.args.commands:
        dos_wrapup(ac)


if __name__ == "__main__":
    main()
