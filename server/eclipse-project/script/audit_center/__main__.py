#!/usr/bin/env python
"""
Audit_Center: Export ColoradoRLA data for publication on Audit Center web site
~~~~~~~~

TODO:
  Pass in default.properties file, Get it working with production environment for db access
  Export ballot manifest files, CVR files
    outcomes, vote counts and margins

  Export it all as csv also
  Export zip of all csv files

%InsertOptionParserUsage%

Abbreviated usage:

 audit_center [-e export_directory] [file.sql]...

Exports for the public dashboard are produced in these files:

seed.json:
   Seed for randomization

__:
   Random ballot order

ballots_to_audit_per_county.json:
   Number of ballots to be audited overall in each county

prefix_length.json:
   The number of ballots 

discrepancies.json:
   Details on each audited contest including ballot cards to audit, discrepancies

acvrs.json:
   Details about each ACVR entry

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
import re
import urlparse # import urllib.parse for python 3+
import psycopg2
import psycopg2.extras

__author__ = "Neal McBurnett <nealmcb@freeandfair.us>"
__version__ = "0.1.0"
__date__ = "2017-10-03"
__copyright__ = "Copyright (c) 2017 Free & Fair"
__license__ = "AGPLv3"

parser = argparse.ArgumentParser(description='Export ColoradoRLA data for publication on Audit Center web site')

parser.add_argument('queryfiles', metavar="QUERYFILE", nargs='*',
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


# To get query result in json format, substitute the query in this string
# and execute the result.
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

    return os.path.join(os.environ.get('SQL_DIR', ''), filename)


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

    # Establish an audit_center context for passing state around
    ac = Namespace()

    # This works locally when logged in as special linux user:
    # ac.con = psycopg2.connect("dbname='corla'")

    # TODO: parse default.properties file
    user = 'corla'
    password = 'corla'
    # uri = 'jdbc:postgresql://localhost:5432/corla?reWriteBatchedInserts=true&disableColumnSantiser=true'
    uri = 'jdbc:postgresql://localhost:5432,192.168.24.76:5432,192.168.24.77:5432/corla?reWriteBatchedInserts=true&disableColumnSantiser=true'

    # Note: in ColoradoRLA, the uri property is used to specify the host and port, but not user and password

    # Remove hibernate-specific query strings and 'scheme' value
    pguri = re.sub(r'\?.*', '', uri.replace('jdbc:postgresql', 'postgresql'))

    logging.debug("pguri: %s\n uri: %s" % (pguri, uri))

    try:
        ac.con = psycopg2.connect(pguri, user=user, password=password)
    except psycopg2.Error as e:
        logging.error(e)
        sys.exit(1)

    ac.cur = ac.con.cursor(cursor_factory=psycopg2.extras.DictCursor)

    for queryfile in args.queryfiles:
        resultfile = os.path.join(args.export_dir, os.path.splitext(queryfile)[0] + '.json')
        with open(resultfile, 'w') as f:
            f.write(query_to_json(ac, sqlfile(queryfile)))


if __name__ == "__main__":
    main(parser)
