#!/usr/bin/env python
import requests
from server_test import *

baseurl = "http://localhost:8888"

def upload_cvr(filename):
    with open(filename, 'rb') as f:
        payload = {'county': 'Arapahoe', 'hash': 'xyzzy'}
        r = requests.post(baseurl + "/upload-cvr-export", files={'cvr_file': f}, data=payload)
    print(r)

def upload_manifest(filename):
    with open(filename, 'rb') as f:
        payload = {'county': 'Arapahoe', 'hash': 'xyzzy'}
        r = requests.post(baseurl + "/upload-ballot-manifest", files={'bmi_file': f}, data=payload)
    print(r)

def server_sequence():
    test_get_cvr()
    test_get_ballot_manifest()
    test_get_contest()
    test_get_cvr_county_Arapahoe()
    test_get_ballot_manifest_county_Arapahoe()
    test_get_contest_id_2()
    test_get_contest_county_Arapahoe()

if __name__ == "__main__":
    upload_cvr("../e-1/arapahoe-regent-3-clear-CVR_Export.csv")
    upload_manifest("../e-1/arapahoe-manifest.csv")
    # server_sequence()
