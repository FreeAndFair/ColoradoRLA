#!/usr/bin/env python3
"""Genelect
~~~~~~~~

Generate test files for RLAtool

%InsertOptionParserUsage%

For example, to generate 5000 CVRs for 3 contests (Prop 1, Prop 2, and Prop 3) with
margins of 900, -200, and 1500 votes for "Yes" vs "No", run:

./genelect.py 5000 900 -200 1500 > cvr-5000-900--200-1500.csv

Currently, only a single batch is generated, so a suitable manifest file might simply say:

    CountyID,ScannerID,BatchID,NumBallots,StorageLocation
    Arapahoe,1,1,10,

Undervotes may be specified with the -u option, terminating the list of undervote counts
per contest with a "--", e.g.

./genelect.py -u 100 200 300 -- 5000 900 -200 1500 > cvr-5000-900--200-1500_100-200-300.csv

A county specific contest can be added with --with-county. One of the contests
with have the county name prepended to it so that it will be unique to the
county. The other contests will be possibly shared with other counties.

./genelect.py 5000 900 -200 1500 --with-county Arapahoe > cvr-arapahoe.csv

    ,,,,,,Arapahoe Prop 1 (Vote For=1),

Todo:

 Automatically save output to a specified file with a helpful name
 Generate matching manifest and standardized contest names
 Add a more general contest spec, in addition to margin, with:
   A way to specify number of ballots on which contest does not appear
   Number of undervotes
   Contest name

 Allow for multiple ballot styles

SPDX-License-Identifier:	GPL-3.0-or-later

"""

import os
import sys
import logging
import argparse
from collections import namedtuple
import itertools

__version__ = "1.0.0"


parser = argparse.ArgumentParser(description='Generate test files for RLAtool')

parser.add_argument("ballots", type=int,
  help="Number of ballot cards to generate")

parser.add_argument("margins", type=int, nargs='+',
  help="Margins for each contest")

parser.add_argument("-u", "--undervotes", type=int, nargs='*',
  help="Undervotes for each contest")

parser.add_argument("-d", "--debuglevel", type=int, default=logging.WARNING,
  help="Set logging level to debuglevel, expressed as an integer: "
  "DEBUG=10, INFO=20, endpoint timing=25, WARNING=30, ERROR=40, CRITICAL=50. "
  "The default is %(default)s" )

parser.add_argument("-c", "--county", type=str,
                    help="prepend COUNTY to the first contest's name")

parser.add_argument("--test",  action="store_true", default=False,
  help="Run tests")


def _test():
    import doctest
    return doctest.testmod()


Contest = namedtuple('Contest', ['name', 'margin', 'votegen'])


def ids(cvrNumber):
    "Return the identification fields that precede the votes, for the given cvrNumber"

    # headers: CvrNumber,TabulatorNum,BatchId,RecordId,ImprintedId,BallotType,

    return '%d,1,1,%d,"1-1-%d",s1' % (cvrNumber, cvrNumber, cvrNumber)


def genvotes(ballots, margin, undervotes=None):
    """Return a generator for a stream of votes (one of (1,0), (0,1), or (0,0)) of
    length <ballots> with the given number of undervotes (or one more if necessary)
    in which the margin of victory between the first and the second choice is
    <margin> and there are the given number of undervotes.  Default for undervotes
    is about half of the non-margin-related votes.

    >>> list(genvotes(6, 3, 0))
    [(0, 0), (1, 0), (1, 0), (1, 0), (0, 1), (1, 0)]
    >>> list(genvotes(6, -3, 0))
    [(0, 0), (0, 1), (0, 1), (0, 1), (0, 1), (1, 0)]
    >>> list(genvotes(5, 7, 0))
    Traceback (most recent call last):
    ValueError: 0 undervotes + (-1 offsetting_pairs * 2) + 7 margin invalid for 5 ballots
    >>> list(genvotes(6, 3, 1))
    [(0, 0), (1, 0), (1, 0), (1, 0), (0, 1), (1, 0)]
    >>> list(genvotes(6, 3, 2))
    [(0, 0), (0, 0), (0, 0), (1, 0), (1, 0), (1, 0)]
    >>> list(genvotes(8, 3))
    [(0, 0), (0, 0), (0, 0), (1, 0), (1, 0), (1, 0), (0, 1), (1, 0)]
    """

    if undervotes is None:
        undervotes = (ballots - abs(margin)) // 2

    # If there are an odd number of votes that aren't dedicated to the margin,
    # increase the overvotes by one.
    undervotes += (ballots - abs(margin) - undervotes) % 2
    offsetting_pairs = (ballots - abs(margin) - undervotes) // 2

    logging.debug('undervotes: %d, offsetting_pairs: %d, margin: %d, ballots: %d' %
                         (undervotes, offsetting_pairs, margin, ballots))

    if undervotes + offsetting_pairs * 2 + abs(margin) != ballots:
        raise ValueError('%d undervotes + (%d offsetting_pairs * 2) + %d margin != %d ballots' %
                         (undervotes, offsetting_pairs, abs(margin), ballots))

    if undervotes < 0 or offsetting_pairs < 0:
        raise ValueError('%d undervotes + (%d offsetting_pairs * 2) + %d margin invalid for %d ballots' %
                         (undervotes, offsetting_pairs, abs(margin), ballots))

    for i in range(undervotes):
        yield((0,0))

    for i in range(abs(margin)):
        if margin >= 0:
            yield((1,0))
        else:
            yield((0,1))

    for i in range(offsetting_pairs):
        yield((0,1))
        yield((1,0))


def main(parser):
    "Run genelect with given argparse arguments"

    args = parser.parse_args()

    #configure the root logger.  Without filename, default is StreamHandler with output to stderr.
    # Default level is WARNING
    logging.basicConfig(level=args.debuglevel)   # ..., format='%(message)s', filename= "/file/to/log/to", filemode='w' )

    logging.debug("args: %s", args)

    if args.test:
        _test()
        sys.exit(0)

    # Default to specifying zero undervotes if not specified.
    if args.undervotes is None:
        args.undervotes = []

    # Zip up the margins and contests and generate a Contest for each such contest spec.
    contestspecs = itertools.zip_longest(args.margins, args.undervotes, fillvalue=0)
    #blank if not set, set if first contest and arg given
    prefix = lambda i: i == 0 and args.county and (args.county + ' ') or ''
    contest_name = lambda i: '%sProp %d (Vote For=1)' % (prefix(i),(i+1))
    contests = [Contest(contest_name(i), margin, genvotes(args.ballots, margin, undervotes))
                        for i, (margin, undervotes) in enumerate(contestspecs)
                        if margin != 'na']

    # Generate the CVR

    # Output description line
    headers = ('"genelect CVRs: %d ballots, margins %s, undervotes %s"%s' %
          (args.ballots, args.margins, args.undervotes, "," * (6 + len(args.margins) * 2)))
    print(headers[:-1])

    # Output contests line
    headers = ',,,,,,'
    for name, margin, _ in contests:
        names = "%s,%s" % (name, name)
        headers += '%s,' % names
    print(headers[:-1])

    # Output choices line
    headers = ',,,,,,'
    for name, margin, _ in contests:
        choices = ['Yes', 'No']
        headers += '%s,%s,' % tuple(choices)
    print(headers[:-1])

    # Output headers, parties line
    headers="CvrNumber,TabulatorNum,BatchId,RecordId,ImprintedId,BallotType,"
    for name, margin, _ in contests:
        headers += ",,"
    print(headers[:-1])

    # Output actual CVRs, including the result of the given genvotes generator for each contest
    for cvrNumber in range(1, args.ballots+1):
        votes = ''
        for name, margin, votegen in contests:
            choices = ['Yes', 'No']
            votes += '%d,%d,' % next(votegen)

        print("%s,%s" % (ids(cvrNumber), votes[:-1]))


if __name__ == "__main__":
    main(parser)
