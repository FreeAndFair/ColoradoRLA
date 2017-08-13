#!/usr/bin/env python
"""
Utility functions for testing ColoradoRLA.

For testing, convert CVRs for ballots to be audited into aCVRs for automatic entry.

Implement publish_ballots_to_audit, or at least a stub.

get_cvrs(): Get all cvrs

"""

from __future__ import print_function
import sys
import logging
import json
import requests
import sampler

#class CVR(object):   # TODO possibility. Why would we do this?
    # access fields as attributes
    # define key
    # print it
    # filter by county
    # various sort orders

def get_cvrs(baseurl="http://localhost:8888"):
    "Return all cvrs uploaded by any county"

    r = requests.get("%s/cvr" % baseurl)
    cvrs = r.json()

    return cvrs


def publish_ballots_to_audit(seed, n, N, cvrs, manifest):
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
                logging.debug("selected cvr %d: %s" % (i, cvr['imprinted_id']))

        ballots_to_audit.append([county_id, selected])

    return ballots_to_audit

if __name__ == "__main__":
    logging.basicConfig(level=logging.DEBUG)

    seed = n = N = manifest = None

    cvrs = get_cvrs()
    print(json.dumps(publish_ballots_to_audit(seed, n, N, cvrs, manifest), indent=2))
