#!/usr/bin/env python
"""
Audit_Center: Export ColoradoRLA data for publication on Audit Center web site
~~~~~~~~

TODO:
  Export ballot manifest files
  Export it all as csv also
  Export zip of all csv files

%InsertOptionParserUsage%

Usage:

 ./audit_center.py

Exports for the public dashboard are produced in these files:

seed.json:
   Seed for randomization

__:
   Random ballot order

ballots_to_audit_per_county.json:
   Number of ballots to be audited overall in each audited contest in each county

d. List of Audit Rounds (number of ballots, status by County, download links). Links shouldl be to all the finalized ballot-level interpretations and comparison details, in sufficient detail to independently verify the calculated risk levels. [as allowable by CORA]

e. Status (audit required, audit in progress, audit complete, hand count required,, hand count complete) by audited contest (i.e., contest "selected for audit" by SoS

f. Link to Final Audit Report

g. Audit Board names and political parties by County

manifest_hash.json:
  County Ballot Manifests and Hashes

Environmental variables:

$SQL_DIR: directory with .sql files

"""

import string
import os
import sys
import logging
import argparse
from argparse import Namespace
import json
from datetime import datetime
import psycopg2
import psycopg2.extras

__author__ = "Neal McBurnett <nealmcb@freeandfair.us>"
__version__ = "0.1.0"
__date__ = "2017-10-03"
__copyright__ = "Copyright (c) 2017 Free & Fair"
__license__ = "AGPLv3"

parser = argparse.ArgumentParser(description='Gather ColoradoRLA data for publication on Audit Center')

parser.add_argument('queryfiles', metavar="QUERYFILE", nargs='+',
                    help='name of file with an SQL query to execute, relative to $SQL_DIR')

parser.add_argument("-d, --debuglevel", type=int, default=logging.WARNING, dest="debuglevel",
  help="Set logging level to debuglevel: DEBUG=10, INFO=20,\n WARNING=30 (the default), ERROR=40, CRITICAL=50")

parser.add_argument('-e, --export-dir', dest='export_dir',
                    default=".",
                    help='Directory in which to put the resulting .json files. Default: . (current directory)')

parser.add_argument('--test',
  action="store_true", default=False,
  help="Run tests")

# incorporate ArgParser usage documentation in our docstring
__doc__ = __doc__.replace("%InsertOptionParserUsage%\n", parser.format_help())


def totest(n):
    """
    >>> totest(2)
    2
    """
    return n


def _test():
    import doctest
    return doctest.testmod()


json_query_wrapper = """
SELECT row_to_json(r) FROM (
%s
) r
"""

def query_to_json(ac, queryfile):
    """Get output in JSON format based on given query, as an array of rows.
    Removes a trailing semicolon from query if there.
    """
    
    with open(queryfile, "r") as query_f:
        pure_query = query_f.read().rstrip(';' + string.whitespace)

        full_query = json_query_wrapper % pure_query
        ac.cur.execute(full_query)
        rows = ac.cur.fetchall()
        return "[" + ','.join(json.dumps(r['row_to_json'], indent=2) for r in rows) + "]"
        

def sqlfile(filename):
    "Return full path for the named sql file"

    return os.path.join(os.environ['SQL_DIR'], filename)


def query_to_csv(ac, queryfile):

    # FIXME: fill in arguments etc
    with open("acvr_report.csv", "w") as f:
        cur.copy_expert("COPY ({}) TO STDOUT WITH CSV HEADER".format(acvrs_query), f)


def main(parser):
    "Run audit_center with given OptionParser arguments"

    args = parser.parse_args()

    logging.basicConfig(level=args.debuglevel)

    logging.debug("args: %s", args)

    if args.test:
        _test()
        sys.exit(0)

    ac = Namespace()

    ac.con = psycopg2.connect("dbname='corla'")

    acvrs_query = """
    -- Show information about all ACVR entries. In particular:
    --  For all random selections, compare ACVRs with their CVRs, by contest
    -- Note that a "discrepancy" column entry appears if ANY of the contests on the ACVR have a discrepancy
    -- Note that some CVRs may be selected multiple times, and each selection shows up here.
    --  The RLA algorithm takes matches and discrepancies into account for each selection.

    SELECT cai.index as selection, dashboard_id AS county, imprinted_id, record_type, timestamp, counted, disagreement,
       discrepancy, cci_a.comment, cci_a.consensus, cci_s.contest_id, cai.cvr_id, cci_s.choices, acvr_id, cci_a.choices
     FROM cvr_audit_info AS cai
     LEFT JOIN cast_vote_record AS cvr
       ON cai.acvr_id = cvr.id
     LEFT JOIN cvr_contest_info AS cci_s
       ON cai.cvr_id = cci_s.cvr_id
     LEFT JOIN cvr_contest_info AS cci_a
       ON cai.acvr_id = cci_a.cvr_id
         AND cci_a.contest_id = cci_s.contest_id
     ORDER BY cai.index, cci_s.contest_id 
    """

    ac.cur = ac.con.cursor(cursor_factory=psycopg2.extras.DictCursor)

    # print("seed result: %s" % query_to_json(ac, sqlfile("seed.sql")))

    for queryfile in args.queryfiles:
        resultfile = os.path.join(args.export_dir, os.path.splitext(queryfile)[0] + '.json')
        with open(resultfile, 'w') as f:
            f.write(query_to_json(ac, sqlfile(queryfile)))

if __name__ == "__main__":
    main(parser)
