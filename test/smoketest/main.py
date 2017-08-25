#!/usr/bin/env python
"""corla_test: drive testing of ColoradoRLA

Example:

./main.py -R dos_init
./main.py -c 3 -v 1 county_setup
./main.py -c 5 -v 2 county_setup
./main.py dos_start
./main.py -c 5 county_audit &
./main.py -c 3 county_audit &
./main.py dos_wrapup

TODO later:

enable examples like these.

Examples:

corla_test reset_database

corla_test reset-database {}
corla_test risk-limit-comp-audits 0.1
corla_test audit-board
corla_test -c 2 -m arapahoe-manifest.csv upload-ballot-manifest
corla_test -c 2 -v arapahoe-regent-3-clear-CVR_Export.csv upload-cvr-export
corla_test contest
corla_test -l "1 14" select-contests
corla_test publish-data-to-audit
corla_test -s 01234567890123456789 random-seed
corla_test publish_ballots-to-audit

-p 3r # pause for about 3 seconds after each aCVR
-D debug level

corla_test dos-dashboard
corla_test county-dashboard

looping:
corla_test -d "2 17" run_audit   # discrepancy for 2nd of every set of 17 ballots
corla_test -d "n 1 17" run_audit # ballot not found for 1st of every set of 17 ballots

corla_test intermediate-audit-report
corla_test audit-report
corla_test publish-report

TODO:
/hand-count  IndicateHandCount

corla_test contest_county

corla_test upload-audit-cvr
corla_test ballot-not-found {'id': cvr['id']}

Perhaps mix multiple actions in a single command line
Perhaps mix multiple roles in a single command line

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

parser.add_argument('-R, --reset', dest='reset', action='store_true',
                    default=False,
                    help='reset the database before other actions')
parser.add_argument('-c, --county', dest='counties', metavar='COUNTY', action='append',
                    type=int,
                    help='numeric county_id for the given command, e.g. 1 '
                    'for Adams. May be specified multiple times.')
parser.add_argument('-m, --manifest', dest='manifest',
                    default=1,
                    help='manifest filename and hash: integer index to pre-defined array, default 0')
parser.add_argument('-v, --cvr', dest='cvr', type=int,
                    default=0,
                    help='predefined cvr filename and hash: integer index to pre-defined array, default 0')
parser.add_argument('-f, --cvrfile', dest='cvrfile',
                    help='cvr filename, for arbitrary cvrs. Takes precedence over -v. '
                    'Proper hash will be computed.')

# TODO: allow a way to specify CVRs and contests per county.
parser.add_argument('-C, --contest', dest='contests', metavar='CONTEST', action='append',
                    type=int,
                    help='numeric contest_index for the given command, e.g. 0 '
                    'for first one from the CVRs. May be specified multiple times.')
parser.add_argument('-r, --risk-limit', type=float, dest='risk_limit', default=0.1,
                    help='risk limit, e.g. 0.1')
parser.add_argument('-s, --seed', dest='seed',
                    default='01234567890123456789',
                    help='numeric contest_index for the given command, e.g. 0 '
                    'for first one from the CVRs. May be specified multiple times.')
parser.add_argument('-u, --url', dest='url',
                    default='http://localhost:8888',
                    help='base url of corla server. Defaults to http://localhost:8888')
# TODO: get rid of this and associated old code when /upload-cvr-export and /upload-cvr-export go away
parser.add_argument('-Y, --ye-olde-upload', type=bool, dest='ye_olde_upload',
                    help='use old file upload protocol')

parser.add_argument('-d, --debuglevel', type=int, default=logging.WARNING, dest='debuglevel',
  help='Set logging level to debuglevel: DEBUG=10, INFO=20,\n WARNING=30 (the default), ERROR=40, CRITICAL=50')

parser.add_argument('commands', metavar="COMMAND", nargs='*',
                    help='audit commands to run. May be specified multiple times. '
                    'Possibilities: dos_init, county_setup, dos_start, county_audit, dos_wrapup')


def state_login(ac, s):
    "Login as state admin in given requests session"

    path = "/auth-state-admin"
    r = s.post(ac.base + path,
               data={'username': 'stateadmin1', 'password': '', 'second_factor': ''})
    print(r, path)


def county_login(ac, s, county_id):
    "Login as county admin in given requests session"

    path = "/auth-county-admin"
    r = s.post(ac.base + path,
               data={'username': 'countyadmin%d' % county_id, 'password': '', 'second_factor': ''})
    print(r, path)


def test_endpoint_json(ac, s, path, data, show=True):
    "Do a generic test of an endpoint that posts the given data to the given path"

    r = s.post(ac.base + path, json=data)
    if r.status_code == 200:
        if show:
            print(r, path)
    else:
        if show:
            print(r, path, r.text)
        else:
            print(r, path)
    return r


def test_endpoint_get(ac, s, path, show=True):
    "Do a generic test of an endpoint that gets the given path"

    r = s.get(ac.base + path)
    if r.status_code == 200:
        if show:
            print(r, path)
    else:
        if show:
            print(r, path, r.text)
        else:
            print(r, path)
    return r


def test_endpoint_bytes(ac, s, path, data):
    "Do a generic test of an endpoint that posts the given data to the given path"

    r = s.post(ac.base + path, data)
    print(r, path, r.text)
    return r


def test_endpoint_post(ac, s, path, data):
    "Do a generic test of an endpoint that posts the given data to the given path"

    r = s.post(ac.base + path, data)
    print(r, path, r.text)
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
    print(r, path, r.text)

    import_handle = r.json()

    # This could be done later, after importing another file.
    r = test_endpoint_json(ac, s, import_path, import_handle)

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
    print(r, path, r.text)


def upload_manifest(ac, s, filename, sha256):
    "Upload manifest"

    with open(filename, 'rb') as f:
        path = "/upload-ballot-manifest"
        payload = {'county': 'Arapahoe', 'hash': sha256}
        r = s.post(ac.base + path,
                          files={'bmi_file': f}, data=payload)
    print(r, path)


def get_county_cvrs(ac, county_id, s):
    "Return all cvrs uploaded by a given county"

    path = x
    r = s.get("%s/cvr/%d" % (ac.base, county_id))
    if r.status_code != 200:
         print(r, path, r.text)
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


def upload_files(ac, s):
    """Directly upload files, which zerotest doesn't support.
    See "File upload via POST request not working: Issue #12"
     https://github.com/jjyr/zerotest/issues/12
    """

    # TODO: predefine more manifests
    manifestfile = "../e-1/arapahoe-manifest.csv"
    hash = "42d409d3394243046cf92e3ce569b7078cba0815d626602d15d0da3e5e844a94"

    manifest = ac.args.manifest # TODO finish when we have more manifests and they make a difference

    if ac.args.ye_olde_upload:
        dir_path = os.path.dirname(os.path.realpath(__file__))
        upload_manifest(ac, s, os.path.join(dir_path, manifestfile), hash)
    else:
        upload_file(ac, s, '/import-ballot-manifest', manifestfile, hash)

    predefined_cvrs = (
        ("../e-1/arapahoe-regent-3-clear-CVR_Export.csv",
                    "413befb5bc3e577e637bd789a92da425d0309310c51cfe796e57c01a1987f4bf"),
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
        BUF_SIZE = 2 ** 18

        sha256 = hashlib.sha256()

        with open(cvrfile, 'rb') as f:
            while True:
                data = f.read(BUF_SIZE)
                if not data:
                    break
                sha256.update(data)

        hash = sha256.hexdigest()

    if ac.args.ye_olde_upload:
        upload_cvrs(ac, s, os.path.join(dir_path, cvrfile), hash)
    else:
        upload_file(ac, s, '/import-cvr-export', cvrfile, hash)

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


def dos_init(ac):
    'Run initial Dept of State steps: reset, risk_limit etc.'

    if ac.args.reset:
        r = test_endpoint_json(ac, ac.state_s, "/reset-database", {})

    r = test_endpoint_json(ac, ac.state_s, "/risk-limit-comp-audits",
                           {"risk_limit": ac.args.risk_limit})


def county_setup(ac, county_id):

    logging.debug("county setup for county_id %d" % county_id)

    county_s = requests.Session()
    county_login(ac, county_s, county_id)

    r = test_endpoint_json(ac, county_s, "/audit-board",
                           [{"first_name": "Mary",
                             "last_name": "Doe",
                             "political_party": "Democrat"},
                            {"first_name": "John",
                             "last_name": "Doe",
                             "political_party": "Republican"}])

    upload_files(ac, county_s)
    # Replace that with this later - or make test_endpoint_file method?
    # r = test_endpoint_post(base, county_s1, "/upload-ballot-manifest", ...)
    # r = test_endpoint_post(base, county_s1, "/upload-cvr-export", ...)

    #FIXME  r = test_endpoint_get("/cvr/%d" % county_id)
    cvrs = get_cvrs(ac, county_s)
    cvrtable = {}
    for cvr in cvrs:
        cvrtable[cvr['id']] = cvr

    r = test_endpoint_get(ac, county_s, "/contest")
    contests = r.json()

    print("Uploaded table of %d CVRs with %d contests" % (len(cvrtable), len(contests)))
    # logging.debug(json.dumps(cvrs))

    # TODO perhaps cleanup - but is this realistic usage? county_s.close()


def dos_start(ac):
    'Run DOS steps to start the audit, enabling county auditing to begin: contest selection, seed, etc.'

    r = test_endpoint_get(ac, ac.state_s, "/contest")
    contests = r.json()
    if len(contests) <= 0:
        print("No contests to audit, status_code = %d" % r.status_code)
        return

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
    r = test_endpoint_post(ac, ac.state_s, "/ballots-to-audit/publish", {})

    r = test_endpoint_get(ac, ac.state_s, "/dos-dashboard")
    if r.status_code == 200:
        dos_dashboard = r.json()
        for contest_id, reason in dos_dashboard['audited_contests'].items():
            r = test_endpoint_get(ac, ac.state_s, "/contest/id/%s" % contest_id)
            print("Audited contest: vote for {votes_allowed} in {name}".format(**r.json()))
        for county_id, status in dos_dashboard['county_status'].items():
            if status['estimated_ballots_to_audit'] != 0:
                print("County %s has initial sample size of %s ballot cards" % 
                      (county_id, status['estimated_ballots_to_audit']))
    logging.debug("dos-dashboard: %s" % r.text)

def county_audit(ac, county_id):
    'Audit board uploads ACVRs from a county'

    county_s = requests.Session()
    county_login(ac, county_s, county_id)

    cvrs = get_cvrs(ac, county_s)
    cvrtable = {}
    for cvr in cvrs:
        cvrtable[cvr['id']] = cvr

    # Print this tool's notion of what should be audited, based on seed etc.
    # for auditing the audit.
    # TODO or FIXME - doesn't yet match "ballots_to_audit" from the dashboard
    logging.log(5, json.dumps(publish_ballots_to_audit(ac.args.seed, cvrs), indent=2))

    r = test_endpoint_get(ac, ac.state_s, "/dos-dashboard")

    r = test_endpoint_get(ac, county_s, "/county-dashboard")
    # r = test_endpoint_get(ac, county_s, "/audit-board-asm-state")
    # r = test_endpoint_json(ac, county_s, "/audit-board-dashboard", {})

    selected = r.json()["ballots_to_audit"]

    # For each of a a bunch of selected cvrs,
    #   make it into a matching acvr and upload it, watching progress
    # TODO: upload the right number of them....

    if len(selected) < 1:
        print("No ballots_to_audit")

    for i in range(len(selected)):
        if i % 10 == 0:
            r = test_endpoint_get(ac, ac.state_s, "/dos-dashboard", show=False)

        acvr = cvrtable[selected[i]].copy()
        logging.debug("Original CVR: %s" % json.dumps(acvr))
        acvr['record_type'] = 'AUDITOR_ENTERED'

        if False:
            r = test_endpoint_json(ac, county_s, "/ballot-not-found", {'id': acvr['id']})

        # Modify the aCVR sometimes.
        # TODO: provide command-line parameters for discrepancy rates?
        if False:
            print('Possible discrepancy: blindly setting choices for first contest to ["Distant Loser"]')
            acvr['contest_info'][0]['choices'] = ["Distant Loser"]
            # acvr['contest_info'][0]['choices'] = ["No/Against"]  # for Denver election contest 0

        if False:
            # Test: make uploaded cvr not match
            # TODO: Decide what API should be for mismatch in contests between CVR and paper
            if len(acvr['contest_info']) > 0:
                del acvr['contest_info'][0]

        logging.debug("Submitting aCVR: %s" % json.dumps(acvr))
        test_endpoint_json(ac, county_s, "/upload-audit-cvr",
                           {'cvr_id': selected[i], 'audit_cvr': acvr}, show=False)

        r = test_endpoint_get(ac, county_s, "/county-dashboard", show=False)
        resp = r.json()
        # The list of ballots_to_audit takes up way too much space in the output....
        if False:
            if 'ballots_to_audit' in resp:
                resp['ballots_to_audit'] = "SUPPRESSED"
            print(resp)
        else:
            # TODO test getting just contests from current county
            # TODO print other interesting info
            print("Uploaded aCVR %d; estimated_ballots_to_audit: %s" % (acvr['id'], resp['estimated_ballots_to_audit']))

        if resp['estimated_ballots_to_audit'] <= 0:
            print("\nAudit completed after %d ballots" % (i + 1))
            break

    r = test_endpoint_get(ac, ac.state_s, "/dos-dashboard")

    r = test_endpoint_json(ac, county_s, "/intermediate-audit-report", {})
    r = test_endpoint_json(ac, county_s, "/audit-report", {})

def dos_wrapup(ac):

    r = test_endpoint_get(ac, ac.state_s, "/dos-dashboard")

    r = test_endpoint_json(ac, ac.state_s, "/publish-report", {})

    # server_sequence()

    sys.exit(0)


if __name__ == "__main__":
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
        ac.args.commands = ["dos_init", "county_setup", "dos_start", "county_audit", "dos_wrapup"]

    logging.debug("Arguments: %s" % ac.args)

    ac.base = ac.args.url

    ac.state_s = requests.Session()
    state_login(ac, ac.state_s)

    if "dos_init" in ac.args.commands:
        dos_init(ac)

    if "county_setup" in ac.args.commands:
        for county_id in ac.args.counties:
            county_setup(ac, county_id)

    if "dos_start" in ac.args.commands:
        dos_start(ac)

    if "county_audit" in ac.args.commands:
        for county_id in ac.args.counties:
            county_audit(ac, county_id)

    if "dos_wrapup" in ac.args.commands:
        dos_wrapup(ac)
