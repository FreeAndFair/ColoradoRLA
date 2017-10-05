#!/usr/bin/env python
"""
Audit_Center: Export ColoradoRLA data for publication on Audit Center web site
~~~~~~~~

Abbreviated usage:

This will run queries using all the .sql files in the $SQL_DIR
environmental variable, which is the current directory by default:

 audit_center [-e export_directory]

Export a query on selected sql files:

 audit_center [-e export_directory] file.sql ...

Full command line usage synopsis:

 audit_center -h

See README.md for documentation.
"""

import string
import os
import sys
import logging
import argparse
from argparse import Namespace
import json
import glob
import re
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
    "Run all doctests in this file"

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
        try:
            ac.cur.execute(full_query)
        except psycopg2.Error as e:
            message = ("audit_center query error on %s:\n %s\nQuery: \n%s" %
                          (queryfile, e, full_query))
            logging.error(message)
            return message

        rows = ac.cur.fetchall()
        return "[" + ','.join(json.dumps(r['row_to_json'], indent=2) for r in rows) + "]"


def query_to_csv(ac, queryfile):
    "Export query result as csv file. FIXME: fill in arguments etc"

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

    # Note: in ColoradoRLA, the uri property is used to specify the host and port,
    # but not user and password

    # Remove hibernate-specific query strings and 'scheme' value
    pguri = re.sub(r'\?.*', '', uri.replace('jdbc:postgresql', 'postgresql'))

    logging.debug("pguri: %s\n uri: %s" % (pguri, uri))

    try:
        ac.con = psycopg2.connect(pguri, user=user, password=password)
    except psycopg2.Error as e:
        logging.error(e)
        sys.exit(1)

    ac.cur = ac.con.cursor(cursor_factory=psycopg2.extras.DictCursor)

    queryfiles = args.queryfiles
    if not queryfiles:
        sql_dir = os.environ.get('SQL_DIR', '.')
        queryfiles = [filename for filename in glob.glob(sql_dir + "/*.sql")]

    for queryfile in queryfiles:
        resultfile = os.path.join(args.export_dir,
                                  os.path.basename(
                                      os.path.splitext(queryfile)[0] + '.json'))
        with open(resultfile, 'w') as f:
            logging.debug("Export query from %s as json in %s" % (queryfile, resultfile))
            f.write(query_to_json(ac, queryfile))


if __name__ == "__main__":
    main(parser)
