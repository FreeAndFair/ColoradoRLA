#!/usr/bin/env python
"""Smoketest the RLA server
TODO: get server_sequence working and displaying errors
nicely, rather than having the user run pytest server_test.py
"""

from __future__ import print_function
import sys
import requests
from server_test import *


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


def upload_cvrs(baseurl, s, filename, sha256):
    "Upload cvrs"

    with open(filename, 'rb') as f:
        path = "/upload-cvr-export"
        payload = {'county': '3', 'hash': sha256}
        r = s.post(baseurl + path,
                          files={'cvr_file': f}, data=payload)
    print(r, path)


def upload_manifest(baseurl, s, filename, sha256):
    "Upload manifest"

    with open(filename, 'rb') as f:
        path = "/upload-ballot-manifest"
        payload = {'county': 'Arapahoe', 'hash': sha256}
        r = s.post(baseurl + path,
                          files={'bmi_file': f}, data=payload)
    print(r, path)


def upload_acvr(baseurl, s, filename):
    "Upload audit CVR (acvr)"

    with open(filename, 'rb') as f:
        path = "/upload-audit-cvr"
        r = s.post(baseurl + path,
                          files={'audit_cvr': f})
    print(r, path)


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
    # When we're updating the tests themselves, the server is running
    # on port 8887.

    if len(sys.argv) > 1  and  sys.argv[1] == "--update":
        base = "http://localhost:8887"
    else:
        base = "http://localhost:8888"

    s = requests.Session()

    state_login(base, s)

    county_login(base, s, 3)

    upload_files(base, s)

    upload_acvr(base, s, "acvr.json")

    # server_sequence()
