#!/usr/bin/env python
"""Smoketest the RLA server
Run thru a simple server_sequence.

TODO: display errors nicely.
"""

from __future__ import print_function
import sys
import json
import logging
import requests

import sampler


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
    print(r, path, r.text)
    return r


def test_endpoint_get(baseurl, s, path, show=True):
    "Do a generic test of an endpoint that gets the given path"

    r = s.get(baseurl + path)
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
    upload_cvrs(baseurl, s, "../e-1/arapahoe-regent-3-clear-CVR_Export.csv",
               "413befb5bc3e577e637bd789a92da425d0309310c51cfe796e57c01a1987f4bf")


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
    # logging.basicConfig(level=logging.DEBUG)

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

    # Alternatives that work, FWIW
    # r = test_endpoint_post(base, state_s, "/risk-limit-comp-audits", '{"risk_limit": "0.1"}')
    # r = test_endpoint_bytes(base, state_s, "/risk-limit-comp-audits", '{"risk_limit": "0.1"}')

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
    print(contests)
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

    if False:
        # TODO: sometimes hit ballot-not-found test
        pass

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

    print("Received table of %d CVRs" % len(cvrtable))
    # logging.debug(json.dumps(cvrs))

    # Print this tool's notion of what should be audited, based on seed etc.
    # for auditing the audit.
    # TODO or FIXME - doesn't yet match "ballots_to_audit" from the dashboard
    print(json.dumps(publish_ballots_to_audit(seed, cvrs), indent=2))

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

    for i in range(len(selected) * 2):
        if i % 10 == 0:
            r = test_endpoint_get(base, state_s, "/dos-dashboard")

        acvr = cvrtable[selected[i]].copy()
        print("Original CVR: %s" % json.dumps(acvr))
        acvr['record_type'] = 'AUDITOR_ENTERED'

        # Modify the aCVR sometimes.
        # TODO: provide command-line parameters for discrepancy rates?
        if False:
            print('Possible discrepancy: blindly setting choices for first contest to ["Distant Loser"]')
            acvr['contest_info'][0]['choices'] = ["Distant Loser"]

        if False:
            # Test: make uploaded cvr not match
            # TODO: Decide what API should be for mismatch in contests between CVR and paper
            del acvr['contest_info'][0]

        print("Submitting aCVR: %s" % json.dumps(acvr))
        test_endpoint_json(base, county_s1, "/upload-audit-cvr",
                           {'cvr_id': selected[i], 'audit_cvr': acvr})

        r = test_endpoint_get(base, county_s1, "/county-dashboard", show=False)
        resp = r.json()
        # The list of ballots_to_audit takes up way too much space in the output....
        resp['ballots_to_audit'] = "SUPPRESSED"
        print(resp)

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
