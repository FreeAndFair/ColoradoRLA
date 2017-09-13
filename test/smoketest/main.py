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

# Test ballot-not-found

crtest -C 1 -n "8 15"

# Check whether the /cvr-to-audit responses match the other lists, and also remove audited ballots

crtest reset dos_init county_setup dos_start
crtest -E "/cvr-to-audit-list?start=0&ballot_count=9&include_duplicates=true"
crtest county_audit
crtest -E "/cvr-to-audit-list?start=0&ballot_count=1&include_duplicates=true"
crtest -E "/cvr-to-audit-list?start=0&ballot_count=9&include_duplicates=true"

# Test two county audits in parallel
(
./main.py reset
./main.py dos_init
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

from __future__ import print_function
import sys
import os 
import operator
import argparse
from argparse import Namespace
import json
import logging
import requests
import hashlib

import sampler


__author__ = "Neal McBurnett <http://neal.mcburnett.org/>"
__license__ = "TODO GPL"


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
                    help='numeric contest_index for the given command, e.g. 0 '
                    'for first one from the CVRs. May be specified multiple times.')
parser.add_argument('-l, --loser', dest='loser', default="Distant Loser",
                    help='Loser to use for -p, default "Distant Loser"')
parser.add_argument('-p, --discrepancy-plan', dest='plan', default="2 17",
                    help='Planned discrepancies. Default is "2 17", i.e. '
                    'Every 17 ACVR upload, upload a possible discrepancy once, '
                    'when the remainder of dividing the upload index is 2. '
                    'Discrepancies thus come with the 3rd of every 17 ACVR uploads.')
parser.add_argument('-P, --discrepancy-end', dest='plan_limit', type=int, default=sys.maxint,
                    help='Last upload with possible discrepancy is # PLAN_LIMIT')

parser.add_argument('-n, --notfound-plan', dest='notfound_plan', default="-1 1",
                    help='Planned rate of ballot-not-found discrepancies. Default is "-1 1", i.e. never')

parser.add_argument('-R, --rounds', type=int, default=-1, dest='rounds',
  help='Set maximum number of rounds. Default is all rounds.')

parser.add_argument('-r, --risk-limit', type=float, dest='risk_limit', default=0.1,
                    help='risk limit, e.g. 0.1')
parser.add_argument('-s, --seed', dest='seed',
                    default='01234567890123456789',
                    help='numeric contest_index for the given command, e.g. 0 '
                    'for first one from the CVRs. May be specified multiple times.')
parser.add_argument('-u, --url', dest='url',
                    default='http://localhost:8888',
                    help='base url of corla server. Defaults to http://localhost:8888')
parser.add_argument('-e, --endpoint', dest='endpoint',
                    help='do an HTTP GET from the given endpoint, authenticated as state admin.')
parser.add_argument('-E, --county-endpoint', dest='county_endpoint',
                    help='do an HTTP GET from the given endpoint, authenticated as a county.')

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
                    'Possibilities: reset dos_init, county_setup, dos_start, county_audit, dos_wrapup')


def state_login(ac, s):
    "Login as state admin in given requests session"

    path = "/auth-state-admin"
    r = s.post(ac.base + path,
               data={'username': 'stateadmin1', 'password': '', 'second_factor': ''})
    r = s.post(ac.base + path,
               data={'username': 'stateadmin1', 'password': '', 'second_factor': ''})
    print(r, "POST", path)


def county_login(ac, s, county_id):
    "Login as county admin in given requests session"

    path = "/auth-county-admin"
    r = s.post(ac.base + path,
               data={'username': 'countyadmin%d' % county_id, 'password': '', 'second_factor': ''})
    r = s.post(ac.base + path,
               data={'username': 'countyadmin%d' % county_id, 'password': '', 'second_factor': ''})
    print(r, "POST", path)


def test_endpoint_json(ac, s, path, data, show=True):
    "Do a generic test of an endpoint that posts the given data to the given path"

    if ac.args.trackstates:
        show=True  # Make sure we show the action before showing the resulting state

    r = s.post(ac.base + path, json=data)
    if r.status_code == 200:
        if show:
            print(r, "POST", path)
    else:
        if show:
            print(r, "POST", path, r.text)
        else:
            print(r, "POST", path)

    r = test_endpoint_get(ac, ac.state_s, "/dos-asm-state", show=False)

    if ac.args.trackstates:
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
            print(r, "GET", path)
    else:
        if show:
            print(r, "GET", path, r.text)
        else:
            print(r, "GET", path)
    return r


def test_endpoint_bytes(ac, s, path, data):
    "Do a generic test of an endpoint that posts the given data to the given path"

    r = s.post(ac.base + path, data)
    print(r, "POST", path, r.text)
    return r


def test_endpoint_post(ac, s, path, data):
    "Do a generic test of an endpoint that posts the given data to the given path"

    r = s.post(ac.base + path, data)
    print(r, "POST", path, r.text)
    return r


def upload_file(ac, s, import_path, filename, sha256):
    "Upload a file and confirm its sha256 hash"

    # Obtain test directory, i.e. where this script is.
    # Filenames can be absolute or relative to this directory.
    dir_path = os.path.dirname(os.path.realpath(__file__))

    with open(os.path.join(dir_path, filename), 'rb') as f:
        path = "/upload-file"
        payload = {'hash': sha256}
        r = s.post(ac.base + path,
                          files={'file': f}, data=payload)

    if r.status_code != 200:
        print(r, "POST", path, r.text)

    logging.debug("%s %s %s" % (r, path, r.text))

    import_handle = r.json()

    # This could be done later, after importing another file.
    r = test_endpoint_json(ac, s, import_path, import_handle)
    if r.status_code != 200:
        print(r, "POST", import_path, r.text)
    logging.debug("%s %s %s" % (r, import_path, r.text))

"""
TODO: clean this out when ready.

Alternate approaches that have worked:
    r = test_endpoint_bytes(ac, s, import_path, r.text)
    r = test_endpoint_json(ac, s, import_path, { "file_id": import_handle['file_id']})

    print("import_handle: %s" % import_handle)
    print("response text: %s" % r.text)
"""

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

def get_county_dashboard(ac, county_s, i=0, acvr={'id': -1}, show=True):
    "Get and show useful info about /county-dashboard"

    r = test_endpoint_get(ac, county_s, "/county-dashboard", show=False)
    county_dashboard = r.json()

    total_audited = 1 + county_dashboard['audited_ballot_count']

    if show:
        logging.debug("county-dashboard: %s" % r.text)
        print("Round %d, county %d, upload %d, prefix %d: aCVR %d; ballots_remaining_in_round: %d, estimated_ballots_to_audit: %s" %
              (ac.round, county_id, total_audited, county_dashboard.get('audited_prefix_length', -1), acvr['id'],  # FIXME
               county_dashboard['ballots_remaining_in_round'], county_dashboard['estimated_ballots_to_audit']))

        """ TODO: drop this assuming it isn't helpful any more.
        print("Round %d, county %d, upload %d: aCVR %d; ballots_remaining_in_round: %d, audited_ballot_count: %d, estimated_ballots_to_audit: %s" %
              (ac.round, county_id, total_audited, acvr['id'], county_dashboard['ballots_remaining_in_round'], county_dashboard['audited_ballot_count'], county_dashboard['estimated_ballots_to_audit']))
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
    'Run initial Dept of State steps: reset, risk_limit etc.'

    r = test_endpoint_json(ac, ac.state_s, "/update-audit-info",
                           { "election_type": "coordinated",
                             "election_date": "2017-11-09T02:00:00Z",
                             "public_meeting_date": "2017-11-19T02:00:00Z",
                             "risk_limit": ac.args.risk_limit } )

def county_setup(ac, county_id):

    logging.debug("county setup for county_id %d" % county_id)

    county_s = requests.Session()
    county_login(ac, county_s, county_id)

    upload_files(ac, county_s)
    # Replace that with this later - or make test_endpoint_file method?
    # r = test_endpoint_post(base, county_s1, "/upload-ballot-manifest", ...)
    # r = test_endpoint_post(base, county_s1, "/upload-cvr-export", ...)

    r = test_endpoint_get(ac, county_s, "/contest/county?%d" % county_id)
    contests = r.json()

    # TODO: get count again: print("Uploaded table of %d CVRs with %d contests" % (, len(contests)))
    print("Uploaded CVR table with %d contests" % (len(contests),))

    # TODO perhaps cleanup - but is it more realistic to just leave sessions open?
    # county_s.close()


def dos_start(ac):
    'Run DOS steps to start the audit, enabling county auditing to begin: contest selection, seed, etc.'

    r = test_endpoint_get(ac, ac.state_s, "/contest")
    contests = sorted(r.json(), key=operator.itemgetter("id"))
    if len(contests) <= 0:
        print("No contests to audit, status_code = %d" % r.status_code)
        return

    for i, contest in enumerate(contests):
        print("Contest {}: vote for {votes_allowed} in {name}".format(i, **contest))

    logging.log(5, "Contests: %s" % contests)

    for contest_index in ac.args.contests:
        if contest_index > len(contests):
            logging.error("Contest_index %d out of range: only %d contests in election" %
                          (contest_index, len(contests)))

        r = test_endpoint_json(ac, ac.state_s, "/select-contests",
                               [{"contest": contests[contest_index]['id'],
                                 "reason": "COUNTY_WIDE_CONTEST",
                                 "audit": "COMPARISON"}])

    # TODO shouldn't this be a POST ala this?
    # r = test_endpoint_post(ac, ac.state_s, "/publish-data-to-audit", {})
    r = test_endpoint_get(ac, ac.state_s, "/publish-data-to-audit")

    r = test_endpoint_json(ac, ac.state_s, "/random-seed",
                           {'seed': ac.args.seed})
    # r = test_endpoint_post(ac, ac.state_s, "/ballots-to-audit/publish", {})
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
            print("Audited contest {id}: vote for {votes_allowed} in {name}".format(**r.json()))
        for county_id, status in dos_dashboard['county_status'].items():
            if status['estimated_ballots_to_audit'] != 0:
                print("County %s has initial sample size of %s sample interpretations" % 
                      (county_id, status['estimated_ballots_to_audit']))
                print("ballots_remaining_in_round %d: %d" %
                      (ac.round, status['ballots_remaining_in_round']))
    logging.debug("dos-dashboard: %s" % r.text)

def county_audit(ac, county_id):
    'Audit board uploads ACVRs from a county. Return estimated remaining ballots to audit'

    county_s = requests.Session()
    county_login(ac, county_s, county_id)

    # Note: we take advantage of a side effect of this also: print where we're at....
    county_dashboard = get_county_dashboard(ac, county_s, -1)

    # audit over detection:
    #  the DOS dashboard can infer this fact, so we can stop showing the "start next round" button
    #  by the conjunction of estimated_ballots_to_audit <= 0 for all counties
    #  After all counties submit their audit report, the DOS ASM _should_ change to DOS_AUDIT_COMPLETE.

    if county_dashboard['estimated_ballots_to_audit'] <= 0:
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

    r = test_endpoint_get(ac, ac.state_s, "/dos-dashboard")

    # r = test_endpoint_get(ac, county_s, "/audit-board-asm-state")

    round = len(county_dashboard['rounds'])
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
        if i % 10 == 0:
            r = test_endpoint_get(ac, ac.state_s, "/dos-dashboard", show=False)

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

            loser=ac.args.loser
            print('Possible discrepancy: blindly setting choices for first contest to [%s]' % loser)
            # TODO: use contest info to look for the contests and add votes for losers 
            acvr['contest_info'][0]['choices'] = [loser]
            # acvr['contest_info'][0]['choices'] = ["No/Against"]  # for Denver election contest 0

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

        county_dashboard = get_county_dashboard(ac, county_s, i, acvr)
        if county_dashboard['estimated_ballots_to_audit'] <= 0:
            break

    r = test_endpoint_json(ac, county_s, "/sign-off-audit-round", audit_board_set)

    r = test_endpoint_get(ac, ac.state_s, "/dos-dashboard")

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

    county_s = requests.Session()
    county_login(ac, county_s, county_id)

    county_dashboard = get_county_dashboard(ac, county_s)
    logging.info("county-dashboard: %s" % county_dashboard)

    rounds = len(county_dashboard['rounds'])

    print("Rounds: %s " % json.dumps(county_dashboard['rounds'], indent=2))

    if (rounds > 1)  and  county_dashboard['rounds'][rounds - 1]['actual_count'] == 0:
        # we didn't actually start the last round
        rounds -= 1

    to_go = county_dashboard['estimated_ballots_to_audit']
    audited = county_dashboard['audited_ballot_count']
    cvr_count = county_dashboard['cvr_export_count']

    if to_go > 0:
        print("\nCounty %d audit incomplete, ended after %d ballots (of %d exported) and %d rounds, %d to go" %
              (county_id, audited, cvr_count, rounds, to_go))
    else:
        print("\nCounty %d audit complete, ended after %d ballots (of %d exported) and %d rounds" %
              (county_id, audited, cvr_count, rounds))

    # TODO: where/how to test this?  r = test_endpoint_json(ac, county_s, "/intermediate-audit-report", {})
    #  and avoid   "result": "/intermediate-audit-report attempted to apply illegal event SUBMIT_INTERMEDIATE_AUDIT_REPORT_EVENT from state AUDIT_COMPLETE"

    download_report(ac, county_s, "county-report", "xlsx")

def dos_wrapup(ac):

    r = test_endpoint_get(ac, ac.state_s, "/dos-dashboard")
    logging.info("dos-dashboard: %s" % r.text)

    r = test_endpoint_json(ac, ac.state_s, "/publish-report", {})

    download_report(ac, ac.state_s, "state-report", "xlsx")


if __name__ == "__main__":

    # Get unbuffered output
    sys.stdout = os.fdopen(sys.stdout.fileno(), 'w', 0)

    # Establist an "audit context", abbreviated ac, for passing state around.
    ac = Namespace()

    ac.args = parser.parse_args()
    logging.basicConfig(level=ac.args.debuglevel)   # or level=5 for everything

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

    print("Arguments: %s" % ac.args)

    ac.round = 1

    ac.base = ac.args.url

    ac.state_s = requests.Session()
    state_login(ac, ac.state_s)

    if not ac.args.endpoint is None:
        r = test_endpoint_get(ac, ac.state_s, ac.args.endpoint)
        print(r, "GET", ac.args.endpoint, r.text)
        sys.exit(0)

    if not ac.args.county_endpoint is None:
        for county_id in ac.args.counties:
            county_s = requests.Session()
            county_login(ac, county_s, county_id)

            r = test_endpoint_get(ac, county_s, ac.args.county_endpoint)
            print(r, "GET", ac.args.endpoint, r.text)

        sys.exit(0)

    if "reset" in ac.args.commands:
        reset(ac)

    if "dos_init" in ac.args.commands:
        dos_init(ac)

    if "county_setup" in ac.args.commands:
        for county_id in ac.args.counties:
            county_setup(ac, county_id)

    if "dos_start" in ac.args.commands:
        dos_start(ac)

    print()

    if "county_audit" in ac.args.commands:
        round = 0
        alldone = False

        while (ac.args.rounds == -1) or (round < ac.args.rounds):
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
