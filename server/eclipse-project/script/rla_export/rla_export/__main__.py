#!/usr/bin/env python
"""rla_export: Export data from ColoradoRLA to allow public verification of the audit
~~~~~~~~

Abbreviated usage:

This will run queries using all the standard .sql queries:

 rla_export [-e export_directory]

Export a query on selected sql files:

 rla_export [-e export_directory] file.sql ...

Full command line usage synopsis:

 rla_export -h

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
import pkg_resources
import ConfigParser
import psycopg2
import psycopg2.extras
import requests

__author__ = "Neal McBurnett <nealmcb@freeandfair.us>"
__date__ = "2017-10-03"
__copyright__ = "Copyright (c) 2017 Colorado Department of State"
__license__ = "AGPLv3"

MYPACKAGE = 'rla_export'
SQL_PATH = pkg_resources.resource_filename(MYPACKAGE, 'sql')
PROPERTIES_FILE = pkg_resources.resource_filename(MYPACKAGE, 'default.properties')

parser = argparse.ArgumentParser(description='Export ColoradoRLA data for publication on Audit Center web site')

parser.add_argument('queryfiles', metavar="QUERYFILE", nargs='*',
                    help='name of file with an SQL query to execute, relative to $SQL_DIR')

parser.add_argument('-p, --properties', dest='properties',
                    default=PROPERTIES_FILE,
                    help='Properties file from which to obtain database connection information. '
                    'Default: ' + PROPERTIES_FILE)

parser.add_argument('-e, --export-dir', dest='export_dir',
                    default=".",
                    help='Directory in which to put the resulting .json files. Default: . (current directory)')
parser.add_argument('-c, --cvr-download', dest='cvr_download', action='store_true',
                    help='Download cvrs also - only needed once per audit, before dice are rolled')
parser.add_argument('-u, --url', dest='url',
                    default=None,
                    help='base url of corla server. Defaults to the port from the properties file '
                    'on localhost. '
                    'Use something like http://example.gov/api when running '
                    'against a normal remote installation.')

parser.add_argument('-v, --version', dest='version', action='store_true',
                    help='Print version number')
parser.add_argument("-d, --debuglevel", type=int, default=logging.WARNING, dest="debuglevel",
  help="Set logging level to debuglevel: DEBUG=10, INFO=20,\n WARNING=30 (the default), ERROR=40, CRITICAL=50")

parser.add_argument('--test',
  action="store_true", default=False,
  help="Run tests")


# incorporate ArgParser usage documentation in our docstring
__doc__ = __doc__.replace("%InsertOptionParserUsage%\n", parser.format_help())


class Dotable(dict):
    """Make nested python dictionaries (json-like objects) accessable using dot notation.

    MIT License. Copyright 2013 Andy Hayden <http://hayd.github.io/2013/dotable-dictionaries> 
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


class FakeSecHead(object):
    """Read Java .properties files, a form of .INI file without a 'section header'.
    From https://stackoverflow.com/a/2819788/507544
    By [Alex Martelli](https://stackoverflow.com/users/95810/alex-martelli).
    """

    def __init__(self, fp):
        self.fp = fp
        self.sechead = '[p]\n'

    def readline(self):
        if self.sechead:
            try: 
                return self.sechead
            finally: 
                self.sechead = None
        else: 
            return self.fp.readline()


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
        message = ("rla_export json query error on %s:\n %s\nQuery: \n%s" %
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
        message = ("rla_export csv query error on %s, writing to %s:\n %s\nQuery: \n%s" %
                      (queryfile, csvfile, e, full_query))
        logging.error(message)
        return message


def state_login(url, username, password):
    "Login as state admin in given requests session with given credentials"

    PATH = "/auth-state-admin"
    data={'username': username, 'password': password, 'second_factor': ''}

    session = None
    try:
        session = requests.Session()
        r = session.post(url + PATH, data)
        if r.status_code != 200:
            logging.error("Login failed for %s as %s: status_code %d" % (url, username, r.status_code))
        r = session.post(url + PATH, data)
        if r.status_code != 200:
            logging.error("Login phase 2 failed for %s as %s: status_code %d" % (url, username, r.status_code))
    except requests.ConnectionError as e:
        logging.error("state_login: %s" % e)

    return session


def get_endpoint(session, baseurl, path):
    "Retrieve an endpoint via the given Requests session"

    r = session.get(baseurl + path)
    if r.status_code != 200:
        logging.error('get_endpoint: Status code %d for %s' % (r.status_code, baseurl + path))

    return r


def download_content(session, baseurl, path, filename):
    "Download and save the content from the given endpoint to filename"

    r = get_endpoint(session, baseurl, "/%s" % path)
    if r.status_code == 200:
        with open(filename, "wb") as f:
            try:
                f.write(r.content)
                logging.debug("/%s report saved as %s" % (path, filename))
            except IOError as e:
                logging.error('download_content %s: %s' % (baseurl + path, e))


def download_file(session, baseurl, file_id, filename):
    "Download the previously-uploaded file with the given file_id to the given filename"

    try:
        with open(filename, 'wb') as f:
            path = "/download-file"
            r = session.get(baseurl + path, params={'file_info': json.dumps({'file_id': "%d" % file_id})})

        if r.status_code != 200:
            logging.error('download_file: status_code %d for %s file, file_id %d' % (r.status_code, filename, file_id))

        with open(filename, "wb") as f:
            f.write(r.content)
    except (IOError, requests.RequestException) as e:
        logging.error("download_file for file_id %d: %s" % (file_id, e))


def main():
    "Run rla_export with given OptionParser arguments"

    args = parser.parse_args()

    logging.basicConfig(level=args.debuglevel)

    logging.debug("args: %s", args)

    if args.version:
        print(pkg_resources.get_distribution(MYPACKAGE))
        sys.exit(0)

    if args.test:
        _test()
        sys.exit(0)

    # Establish a context for passing state around
    ac = Namespace()

    # Parse properties file
    cp = ConfigParser.SafeConfigParser()
    cp.readfp(FakeSecHead(open(args.properties)))

    # Note: in ColoradoRLA, the url property is used to specify the host and port,
    # but not user and password

    url = cp.get('p', 'hibernate.url')
    user = cp.get('p', 'hibernate.user')
    password = cp.get('p', 'hibernate.pass')

    # Remove hibernate-specific query strings and nonstandard 'scheme' value
    pgurl = re.sub(r'\?.*', '', url.replace('jdbc:postgresql', 'postgresql'))

    logging.debug("pgurl: %s\n url: %s" % (pgurl, url))

    try:
        ac.con = psycopg2.connect(pgurl, user=user, password=password)
    except psycopg2.Error as e:
        logging.error(e)
        sys.exit(1)

    ac.cur = ac.con.cursor(cursor_factory=psycopg2.extras.DictCursor)

    queryfiles = args.queryfiles
    if not queryfiles:
        sql_dir = os.environ.get('SQL_DIR', SQL_PATH)
        logging.debug('sql_dir = %s, path=%s' % (sql_dir, SQL_PATH))
        queryfiles = [filename for filename in glob.glob(sql_dir + "/*.sql")]

    for queryfile in queryfiles:
        logging.info("Exporting json and csv for query in %s" % queryfile)

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

    # TODO: pick this up from a config file
    baseurl = args.url or 'http://localhost:' + cp.get('p', 'http_port', '8888')
    ac.corla_auth = {'url': baseurl, 'username': 'stateadmin1', 'password': ''}

    with state_login(**ac.corla_auth) as session:
        r = get_endpoint(session, baseurl, "/dos-dashboard")
        if r.status_code != 200:
            logging.error("Can't get dos-dashboard: status_code %d" % (r.status_code))
            sys.exit(2)

        dos_dashboard = Dotable(r.json())

        r = download_content(session, baseurl, 'state-report',
                             os.path.join(args.export_dir, 'state_report.xlsx'))

        for county_status in dos_dashboard.county_status.values():
            county_id = county_status.id

            if county_status.has_key('ballot_manifest_file'):
                filename = os.path.join(args.export_dir, 'county_manifest_%d.csv' % county_id)
                download_file(session, baseurl, county_status.ballot_manifest_file.file_id, filename)

            if county_status.has_key('cvr_export_file') and args.cvr_download:
                filename = os.path.join(args.export_dir, 'county_cvr_%d.csv' % county_id)
                download_file(session, baseurl, county_status.cvr_export_file.file_id, filename)

            if not county_status.has_key('rounds') or len(county_status.rounds) <= 0:
                continue

            ballot_count = county_status.rounds[-1].expected_audited_prefix_length
            if ballot_count > 0:
                filename = os.path.join(args.export_dir, 'ballot_list_%d.csv' % county_id)
                query = ("cvr-to-audit-download?county=%d&start=0&ballot_count=%d"
                        "&include_audited&include_duplicates" % (county_id, ballot_count))
                r = download_content(session, baseurl, query, filename)

                filename = os.path.join(args.export_dir, 'county_report_%d.xlsx' % county_id)
                download_content(session, baseurl, "county-report?county=%d" % county_id, filename)


if __name__ == "__main__":
    main()
