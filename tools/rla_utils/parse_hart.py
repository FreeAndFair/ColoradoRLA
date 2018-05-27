#!/usr/bin/env python
# coding=utf-8
"""
Read in contest_table.csv
Summarize contents by contest:
  Sort contests by reverse number of precincts per contest
  Generate mock cvr file
  Overall results per choice per contest

Todo:
    produce separate files for contest summaries and results by contest by choice
"""

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

import os
import sys
import csv
import argparse
import argparse
from datetime import datetime
from operator import itemgetter, attrgetter

parser = argparse.ArgumentParser(description='ColoradoRLA utilities.')
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
    def __init__(self, name):
        self.name = name
        self.choices = {}
        self.precinct_count = 0
        self.registered = 0
        self.ballots = 0

    def __str__(self):
        #return "%s %s\t%s\t%s" % (self.FirstName, self.LastName, self.Committee, self.Contribution)
        # return ', '.join([a + ": '" + str(getattr(self, a)) + "'"   for a in dir(self)  if not a.startswith("_")])
        return "%d\t%d\t%d\t%s): %s" % (self.registered, self.ballots, self.precinct_count, self.name)

def parse_hart_contest_table(parser):

    args = parser.parse_args()

    contests = {}
    last_title = None

    for name in args.precinct_er:

        reader = csv.DictReader(open(name))

        for row in reader:
            title = row['Contest_title']
            contest = contests.setdefault(title, Contest(title))
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

    print("registered\tballots\tprecinct_count\tcontest_name")
    for contest in sorted(contests.values(), key=attrgetter('precinct_count'), reverse=True):
        print("{registered}\t{ballots}\t{precinct_count}\t{name}".format(**contest.__dict__))
        #print("%s: %d" % (contest.name, contest.precinct_count))

    print("votes\tabsentee_votes\tearly_votes\telection_votes\tchoice_name")
    for contest in sorted(contests.values(), key=attrgetter('precinct_count'), reverse=True):
        for choice in sorted(contest.choices.values(), key=attrgetter('votes'), reverse=True):
            print("{votes}\t{absentee_votes}\t{early_votes}\t{election_votes}\t{name}".format(**choice.__dict__))

if __name__ == "__main__":
    parse_hart_contest_table(parser)
