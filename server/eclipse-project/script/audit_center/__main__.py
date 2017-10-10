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
import requests

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


class Dotable(dict):
    """Make nested python dictionaries (json-like objects) accessable using dot notation.
    FIXME: Check licensing
    From Andy Hayden, http://hayd.github.io/2013/dotable-dictionaries
    """

    __getattr__= dict.__getitem__

    def __init__(self, d):
        self.update(**dict((k, self.parse(v))
                           for k, v in d.iteritems()))

    @classmethod
    def parse(cls, v):
        if isinstance(v, dict):
            return cls(v)
        elif isinstance(v, list):
            return [cls.parse(i) for i in v]
        else:
            return v


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


def stripped_query(queryfile):
    "Return the query in the given file sans trailing semicolon or whitespace"

    with open(queryfile, "r") as query_f:
        return query_f.read().rstrip(';' + string.whitespace)


def query_to_json(ac, queryfile):
    """Get output in JSON format based on given query, as an array of rows.
    """

    # To get query result in json format, substitute the query into this string
    # and execute the result.
    json_query_wrapper = """
    SELECT row_to_json(r) FROM (
    {}
    ) r
    """

    full_query = json_query_wrapper.format(stripped_query(queryfile))

    try:
        ac.cur.execute(full_query)
    except psycopg2.Error as e:
        message = ("audit_center json query error on %s:\n %s\nQuery: \n%s" %
                      (queryfile, e, full_query))
        logging.error(message)
        return message

    rows = ac.cur.fetchall()
    return "[" + ','.join(json.dumps(r['row_to_json'], indent=2) for r in rows) + "]"


def query_to_csvfile(ac, queryfile, csvfile):
    "Export query result, writing to given csv filename."

    # To get query result in csv format, substitute the query in this string
    # and execute the result.
    csv_query_wrapper = "COPY ({}) TO STDOUT WITH CSV HEADER"

    full_query = csv_query_wrapper.format(stripped_query(queryfile), csvfile)

    try:
        with open(csvfile, "w") as f:
            ac.cur.copy_expert(full_query, f)
    except (psycopg2.Error, IOError) as e:
        message = ("audit_center csv query error on %s, writing to %s:\n %s\nQuery: \n%s" %
                      (queryfile, csvfile, e, full_query))
        logging.error(message)
        return message


def state_login(uri, username, password):
    "Login as state admin in given requests session with given credentials"

    PATH = "/auth-state-admin"
    data={'username': username, 'password': password, 'second_factor': ''}

    session = None
    try:
        session = requests.Session()
        r = session.post(uri + PATH, data)
        if r.status_code != 200:
            logging.error("Login failed for %s as %s: status_code %d" % (uri, username, r.status_code))
        r = session.post(uri + PATH, data)
        if r.status_code != 200:
            logging.error("Login phase 2 failed for %s as %s: status_code %d" % (uri, username, r.status_code))
    except requests.ConnectionError as e:
        logging.error("state_login: %s" % e)

    return session


def get_endpoint(session, baseuri, path):
    "Retrieve an endpoint via the given Requests session"

    r = session.get(baseuri + path)
    if r.status_code != 200:
        logging.error('get_endpoint: Status code %d for %s' % (r.status_code, baseuri + path))

    return r


def download_content(session, baseuri, path, filename):
    "Download and save the content from the given endpoint to filename"

    r = get_endpoint(session, baseuri, "/%s" % path)
    if r.status_code == 200:
        with open(filename, "wb") as f:
            try:
                f.write(r.content)
                logging.debug("/%s report saved as %s" % (path, filename))
            except IOError as e:
                logging.error('download_content %s: %s' % (baseuri + path, e))


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

    # TODO: parse default.properties file
    user = 'corla'
    password = 'corla'
    # uri = 'jdbc:postgresql://localhost:5432/corla?reWriteBatchedInserts=true&disableColumnSantiser=true'
    uri = 'jdbc:postgresql://localhost:5432,192.168.24.76:5432,192.168.24.77:5432/corla?reWriteBatchedInserts=true&disableColumnSantiser=true'
    # FIXME: temporarily use test database until config file parsing is done
    uri = 'jdbc:postgresql://localhost:5432,192.168.24.76:5432,192.168.24.77:5432/corla_auditall?reWriteBatchedInserts=true&disableColumnSantiser=true'

    # Note: in ColoradoRLA, the uri property is used to specify the host and port,
    # but not user and password

    # Remove hibernate-specific query strings and nonstandard 'scheme' value
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
        resultfilebase = os.path.join(args.export_dir,
                                  os.path.basename(
                                      os.path.splitext(queryfile)[0]))
        try:
            with open(resultfilebase + '.json', 'w') as f:
                logging.debug("Export query from %s as json in %s" % (queryfile, resultfilebase + '.json'))
                f.write(query_to_json(ac, queryfile))
        except IOError as e:
            logging.error(e)
            break

        query_to_csvfile(ac, queryfile, resultfilebase + '.csv')

    baseuri = 'http://localhost:8888'
    ac.corla_auth = {'uri': baseuri, 'username': 'stateadmin1', 'password': ''}

    with state_login(**ac.corla_auth) as session:
        r = get_endpoint(session, baseuri, "/dos-dashboard")
        if r.status_code != 200:
            logging.error("Can't get dos-dashboard: status_code %d" % (r.status_code))
            sys.exit(2)

        dos_dashboard = Dotable(r.json())

        r = download_content(session, baseuri, 'state-report',
                             os.path.join(args.export_dir, 'state_report.xlsx'))

        for county_status in dos_dashboard.county_status.values():
            # FIXME: no 'current_round' at the end of the audit
            if not county_status.has_key('current_round'):
                continue
            ballot_count = (county_status.current_round.expected_audited_prefix_length +
                            county_status.current_round.previous_ballots_audited)
            if ballot_count > 0:
                county_id = county_status.id
                filename = os.path.join(args.export_dir, 'ballot_list_%d.csv' % county_id)
                query = ("cvr-to-audit-download?county=%d&start=0&ballot_count=%d"
                        "&include_audited&include_duplicates" % (county_id, ballot_count))
                r = download_content(session, baseuri, query, filename)

                filename = os.path.join(args.export_dir, 'county_report_%d.csv' % county_id)
                # FIXME: specify county id. can we get county reports via state login?
                download_content(session, baseuri, "county-report", filename)

if __name__ == "__main__":
    main(parser)
