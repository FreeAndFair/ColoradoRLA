#!/usr/bin/env python
# coding=utf-8
"""
Read in contest_table.csv
Summarize contents by contest:
  Sort contests by reverse number of precincts per contest
  Generate mock cvr file
  Overall results per choice per contest

Todo:
    See inline FIXME and TODO lists
"""

from __future__ import print_function

import os
import sys
import codecs
import csv
import logging
import argparse
import argparse
from datetime import datetime
from operator import itemgetter, attrgetter

Sample_data = """"Precinct_Name","Split_Name","precinct_splitId","Reg_voters","Ballots","Reporting","Contest_id","Contest_title","Contest_party","Choice_id","Candidate_name","Choice_party","Candidate_Type","Absentee_votes","Early_votes","Election_Votes"
"02008","","14933","1167","460","1","718","UNITED STATES SENATOR","","2873","GEORGE C. YANG","REP","C","6","0","3"
"02008","","14933","1167","460","1","718","UNITED STATES SENATOR","","2874","JERRY J. LAWS","REP","C","8","0","1"
"02008","","14933","1167","460","1","718","UNITED STATES SENATOR","","2875","GAIL K. LIGHTFOOT","LIB","C","10","0","2"
"02008","","14933","1167","460","1","718","UNITED STATES SENATOR","","2880","DUF SUNDHEIM","REP","C","21","0","10"
"02008","","14933","1167","460","1","718","UNITED STATES SENATOR","","2882","LORETTA L. SANCHEZ","DEM","C","102","0","106"
"02008","","14933","1167","460","1","718","UNITED STATES SENATOR","","2893","KAMALA D. HARRIS","DEM","C","37","0","37"
"02008","","14933","1167","460","1","718","UNITED STATES SENATOR","","2896","MASSIE MUNROE","DEM","C","1","0","4"
"02008","","14933","1167","460","1","718","UNITED STATES SENATOR","","2897","ELEANOR GARCÍA","","C","2","0","3"
"02008","","14933","1167","460","1","718","UNITED STATES SENATOR","","2877","PAMELA ELIZONDO","GRN","C","4","0","6"
"""

parser = argparse.ArgumentParser(description='ColoradoRLA utilities.')

parser.add_argument("-n, --num_cvrs", type=int, default=1099, dest="num_cvrs",
  help="number of CVRs to list in mock cvr")

parser.add_argument("-p, --minprecinctcount", type=int, default=1561, dest="minprecinctcount",
  help="minimum precinct count for selected contests in mock cvr")

parser.add_argument("-d, --debuglevel", type=int, default=logging.WARNING, dest="debuglevel",
  help="Set logging level to debuglevel, expressed as an integer: "
  "DEBUG=10, INFO=20, endpoint timing=25, WARNING=30 (the default), ERROR=40, CRITICAL=50")

parser.add_argument('precinct_er', nargs='+',
                    help='Election results by precinct, in Hart contest_table.csv format')

class Choice(object):
    def __init__(self, name):
        self.name = name
        self.absentee_votes = 0
        self.early_votes = 0
        self.election_votes = 0
        self.votes = 0

    def __str__(self):
        self.name

class Contest(object):
    """
    An election contest, along with the set of choices and vote totals for each choice.
    """
    num_instances = 0

    def __init__(self, name):
        Contest.num_instances += 1
        self.precedence = Contest.num_instances
        self.name = name

        self.choices = {}
        self.precinct_count = 0
        self.registered = 0
        self.ballots = 0

    def __str__(self):
        return "%d\t%d\t%d\t%s): %s" % (self.registered, self.ballots, self.precinct_count, self.name)

def parse_hart_contest_table(parser):

    args = parser.parse_args()

    contests = {}
    last_title = None

    for name in args.precinct_er:

        reader = csv.DictReader(open(name))

        for row in reader:
            title = row['Contest_title']
            contest = contests.setdefault(title, Contest(title))  # FIXME: only generate new Contest if needed, also below
            if last_title != title:
                contest.precinct_count += 1
                last_title = title
                contest.registered += int(row['Reg_voters'])
                contest.ballots += int(row['Ballots'])

            candidate_name = row['Candidate_name']
            choice = contest.choices.setdefault(candidate_name, Choice(candidate_name))
            choice.absentee_votes += int(row['Absentee_votes'])
            choice.early_votes += int(row['Early_votes'])
            choice.election_votes += int(row['Election_Votes'])
            choice.votes = choice.absentee_votes + choice.early_votes + choice.election_votes

    contests_sorted = contests.values()
    contests_sorted.sort(key=attrgetter('precedence'))
    contests_filtered = [contest for contest in contests_sorted if contest.precinct_count >= args.minprecinctcount]
    contests_sorted = contests_filtered # TODO: decide whether to keep this and never show anything about filtered-out contests

    with codecs.open("/tmp/contests.csv", "w", "utf-8") as contestfile:
        print("registered\tballots\tprecinct_count\tcontest_name", file=contestfile)
        for contest in contests_sorted:
            print("{registered}\t{ballots}\t{precinct_count}\t{name}".format(**contest.__dict__), file=contestfile)

    with codecs.open("/tmp/choices.csv", "w", "utf-8") as choicefile:
        print("contest\tvotes\tabsentee_votes\tearly_votes\telection_votes\tchoice_name", file=choicefile)
        for contest in contests_sorted:
            for choice in sorted(contest.choices.values(), key=attrgetter('votes'), reverse=True):
                print("{contest.name}\t{choice.votes}\t{choice.absentee_votes}\t{choice.early_votes}\t{choice.election_votes}\t{choice.name}"
                      .format(**locals()), file=choicefile)

    contests_filtered = [contest for contest in contests_sorted if contest.precinct_count >= args.minprecinctcount]

    with codecs.open("/tmp/cvr.csv", "w", "utf-8") as cvrfile:
        # line 1: election info
        cvrfile.write('MockCVR,1.0,,,,\n')

        # line 2: contest names, one per choice
        cvrfile.write(',,,,,')
        for contest in contests_filtered:
            cvrfile.write((',"' + contest.name + ' (Vote For=1)"') * len(contest.choices))
        cvrfile.write('\n')

        # line 3: choice names.
        cvrfile.write(',,,,,')
        for contest in contests_filtered:
            for choice in contest.choices:
               cvrfile.write(",'%s'" % choice) # FIXME: replace with robust csv-quoting of choice name
        cvrfile.write('\n')

        # line 4: nominally party names
        cvrfile.write('"CvrNumber","TabulatorNum","BatchId","RecordId","ImprintedId","BallotType"')
        cvrfile.write(',' * sum((len(contest.choices) for contest in contests_filtered)))
        cvrfile.write('\n')

        # content lines
        for cvrid in range(1, args.num_cvrs + 1):
            cvrfile.write("%d,%d,%d,%d,%s,%s" % (cvrid, 1, 1, cvrid, '"1-1-1"', 'std'))
            for i, contest in enumerate(contests_filtered):
                # First line (cvrid == 1) is for the winner, then alternate for loser and for winner
                if len(contest.choices) > 1:
                    if (cvrid > 1) and (cvrid % 2 == 1):
                        cvrfile.write(',0,1')
                    else:
                        cvrfile.write(',1,0')
                    cvrfile.write((',0') * (len(contest.choices) - 2))
                else:
                    cvrfile.write(',1')
            cvrfile.write('\n')


if __name__ == "__main__":
    parse_hart_contest_table(parser)
