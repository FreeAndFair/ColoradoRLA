#!/usr/bin/env python
"""corla_test: drive testing of ColoradoRLA

TODO: enable examples like these.

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
corla_test contest_county

corla_test upload-audit-cvr
corla_test ballot-not-found {'id': cvr['id']}

Perhaps mix multiple actions in a single command line
Perhaps mix multiple roles in a single command line

Note: zerotest doesn't support POST operations (yet?)
See "File upload via POST request not working: Issue #12"
https://github.com/jjyr/zerotest/issues/12
"""

from __future__ import print_function
import sys
import json
import logging
import requests

import sampler


__author__ = "Neal McBurnett <http://neal.mcburnett.org/>"
__license__ = "TODO GPL"


"""
parser = argparse.ArgumentParser(description='Drive testing for ColoradoRLA auditing.')
parser.add_argument('cvr_file', nargs='+',
                    help='zip archive containing Cast Vote Record files in json format')
parser.add_argument('--name', default="test",
                    help='short name to use as a prefix for the generated files')

parser.add_argument('--sum', dest='accumulate', action='store_const',
                    const=sum, default=max,
                    help='sum the integers (default: find the max)')
"""

# Table of county names: server/eclipse-project/src/main/resources/us/freeandfair/corla/county_ids.properties

def state_login(baseurl, s):
    "Login as state admin in given requests session"

    path = "/auth-state-admin"
    r = s.post(baseurl + path,
               data={'username': 'stateadmin1', 'password': '', 'second_factor': ''})
    print(r, path)


def county_login(baseurl, s, county_id):
    "Login as county admin in given requests session"

    path = "/auth-county-admin"
    r = s.post(baseurl + path,
               data={'username': 'countyadmin%d' % county_id, 'password': '', 'second_factor': ''})
    print(r, path)


def test_endpoint_json(baseurl, s, path, data):
    "Do a generic test of an endpoint that posts the given data to the given path"

    r = s.post(baseurl + path, json=data)
    if r.status_code != 200:
         print(r, path, r.text)
    return r


def test_endpoint_get(baseurl, s, path, show=True):
    "Do a generic test of an endpoint that gets the given path"

    r = s.get(baseurl + path)
    if r.status_code != 200:
        if show:
            print(r, path, r.text)
        else:
            print(r, path)
    return r


def test_endpoint_bytes(baseurl, s, path, data):
    "Do a generic test of an endpoint that posts the given data to the given path"

    r = s.post(baseurl + path, data)
    print(r, path, r.text)
    return r


def test_endpoint_post(baseurl, s, path, data):
    "Do a generic test of an endpoint that posts the given data to the given path"

    r = s.post(baseurl + path, data)
    print(r, path, r.text)
    return r


def upload_cvrs(baseurl, s, filename, sha256):
    "Upload cvrs"

    with open(filename, 'rb') as f:
        path = "/upload-cvr-export"
        # TODO: make this generic for any county
        payload = {'county': '3', 'hash': sha256}
        r = s.post(baseurl + path,
                          files={'cvr_file': f}, data=payload)
    print(r, path, r.text)


def upload_manifest(baseurl, s, filename, sha256):
    "Upload manifest"

    with open(filename, 'rb') as f:
        path = "/upload-ballot-manifest"
        payload = {'county': 'Arapahoe', 'hash': sha256}
        r = s.post(baseurl + path,
                          files={'bmi_file': f}, data=payload)
    print(r, path)


def get_cvrs(baseurl, s):
    "Return all cvrs uploaded by any county"

    r = s.get("%s/cvr" % baseurl)
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
                print("Selected cvr %d: id: %d RecordID: %s" % (i, cvr['id'], cvr['imprinted_id']))

        ballots_to_audit.append([county_id, selected])

    return ballots_to_audit


def upload_files(baseurl, s):
    """Directly upload files, which zerotest doesn't support.
    See "File upload via POST request not working: Issue #12"
     https://github.com/jjyr/zerotest/issues/12
    """

    upload_manifest(baseurl, s, "../e-1/arapahoe-manifest.csv",
                "42d409d3394243046cf92e3ce569b7078cba0815d626602d15d0da3e5e844a94")

    v = 1
    if v == 1:
        upload_cvrs(baseurl, s, "../e-1/arapahoe-regent-3-clear-CVR_Export.csv",
                    "413befb5bc3e577e637bd789a92da425d0309310c51cfe796e57c01a1987f4bf")
    if v == 2:
        upload_cvrs(baseurl, s, "../dominion-2017-CVR_Export_20170310104116.csv",
                    "4e3844b0dabfcea64a499d65bc1cdc00d139cd5cdcaf502f20dd2beaa3d518d2")

    if v == 3:
        upload_cvrs(baseurl, s, "../Denver2016Test/CVR_Export_20170804111144.csv",
                    "1def4aa4c0e1421b4e5adcd4cc18a8d275f709bc07820a37e76e11a038195d02")



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

if __name__ == "__main__":
    # Uncomment for debugging output
    # logging.basicConfig(level=logging.DEBUG)   # or level=5 for everything

    # When we're updating the tests themselves, the server is running
    # on port 8887.

    if len(sys.argv) > 1  and  sys.argv[1] == "--update":
        base = "http://localhost:8887"
    else:
        base = "http://localhost:8888"

    state_s = requests.Session()
    state_login(base, state_s)

    county_s1 = requests.Session()
    county_login(base, county_s1, 3)

    r = test_endpoint_json(base, state_s, "/reset-database", {})

    r = test_endpoint_json(base, state_s, "/risk-limit-comp-audits", {"risk_limit": 0.1})

    r = test_endpoint_json(base, county_s1, "/audit-board",
                           [{"first_name": "Mary",
                             "last_name": "Doe",
                             "political_party": "Democrat"},
                            {"first_name": "John",
                             "last_name": "Doe",
                             "political_party": "Republican"}])

    upload_files(base, county_s1)

    # Replace that with this later - or make test_endpoint_file method?
    # r = test_endpoint_post(base, county_s1, "/upload-ballot-manifest", ...)
    # r = test_endpoint_post(base, county_s1, "/upload-cvr-export", ...)

    # We need a valid contest to audit. Pick the first one.
    r = test_endpoint_get(base, county_s1, "/contest")
    contests = r.json()
    if len(contests) > 0:
        logging.log(5, "Contests: %s" % contests)
        contest_to_audit = contests[0]['id']

        r = test_endpoint_json(base, state_s, "/select-contests",
                               [{"contest": contest_to_audit,
                                 "reason": "COUNTY_WIDE_CONTEST",
                                 "audit": "COMPARISON"}])
        # Each contest is selected separately, despite the endpoint name
        if True:  # TODO: add a command-line option for contest selection
            r = test_endpoint_json(base, state_s, "/select-contests",
                                   [{"contest": contests[1]['id'],
                                     "reason": "COUNTY_WIDE_CONTEST",
                                     "audit": "COMPARISON"}])

    else:
        print("No contests to audit, status_code = %d" % r.status_code)

    # TODO shouldn't this be a POST ala this?
    # r = test_endpoint_post(base, state_s, "/publish-data-to-audit", {})
    r = test_endpoint_get(base, state_s, "/publish-data-to-audit")

    seed = "01234567890123456789"

    r = test_endpoint_json(base, state_s, "/random-seed",
                           {'seed': seed})
    r = test_endpoint_post(base, state_s, "/ballots-to-audit/publish", {})

    cvrs = get_cvrs(base, county_s1)
    cvrtable = {}
    for cvr in cvrs:
        cvrtable[cvr['id']] = cvr

    print("Received table of %d CVRs with %d contests" % (len(cvrtable), len(contests)))
    # logging.debug(json.dumps(cvrs))

    # Print this tool's notion of what should be audited, based on seed etc.
    # for auditing the audit.
    # TODO or FIXME - doesn't yet match "ballots_to_audit" from the dashboard
    logging.log(5, json.dumps(publish_ballots_to_audit(seed, cvrs), indent=2))

    r = test_endpoint_get(base, state_s, "/dos-dashboard")

    r = test_endpoint_get(base, county_s1, "/county-dashboard")
    # r = test_endpoint_get(base, county_s1, "/audit-board-asm-state")
    # r = test_endpoint_json(base, county_s1, "/audit-board-dashboard", {})

    selected = r.json()["ballots_to_audit"]

    # For each of a a bunch of selected cvrs,
    #   make it into a matching acvr and upload it, watching progress
    # TODO: upload the right number of them....

    if len(selected) < 1:
        print("No ballots_to_audit")

    for i in range(len(selected)):
        if i % 10 == 0:
            r = test_endpoint_get(base, state_s, "/dos-dashboard")

        acvr = cvrtable[selected[i]].copy()
        logging.debug("Original CVR: %s" % json.dumps(acvr))
        acvr['record_type'] = 'AUDITOR_ENTERED'

        if False:
            r = test_endpoint_json(base, county_s1, "/ballot-not-found", {'id': acvr['id']})

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
        test_endpoint_json(base, county_s1, "/upload-audit-cvr",
                           {'cvr_id': selected[i], 'audit_cvr': acvr})

        r = test_endpoint_get(base, county_s1, "/county-dashboard", show=False)
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

    r = test_endpoint_get(base, state_s, "/dos-dashboard")

    r = test_endpoint_json(base, county_s1, "/intermediate-audit-report", {})
    r = test_endpoint_json(base, county_s1, "/audit-report", {})

    r = test_endpoint_get(base, state_s, "/dos-dashboard")

    r = test_endpoint_json(base, state_s, "/publish-report", {})

    # server_sequence()

    sys.exit(0)
