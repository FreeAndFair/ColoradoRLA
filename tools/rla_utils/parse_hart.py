#!/usr/bin/env python
# coding=utf-8
"""
Read in contest_table.csv
Summarize contents by contest:
  Sort contests by reverse number of precincts per contest
  Generate mock cvr file
  Overall results per choice per contest

Todo:
    Calculate winners and losers, assuming all are 2-winner contests
    Skip single-choice contests
    Option to select contests
    See inline FIXME and TODO lists
"""

from __future__ import print_function
from __future__ import division

import os
import sys
import codecs
import csv
import logging
import argparse
import json
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

parser.add_argument("-n, --num_cvrs", type=int, default=2000, dest="num_cvrs",
  help="number of CVRs to list in mock cvr (max number of audited ballots)")

parser.add_argument("-p, --minprecinctcount", type=int, default=-1, dest="minprecinctcount",
  help="minimum precinct count for selected contests in mock cvr."
       " -1 (the default) means the max precinct count seen")

parser.add_argument("-d, --debuglevel", type=int, default=logging.WARNING, dest="debuglevel",
  help="Set logging level to debuglevel, expressed as an integer: "
  "DEBUG=10, INFO=20, endpoint timing=25, WARNING=30 (the default), ERROR=40, CRITICAL=50")

parser.add_argument('-C, --cvr_contests', dest="cvr_contests",
                    help='Contests to include in CVR, as an JSON array.')

"""
parser.add_argument('-C, --cvr_contests', nargs='+',
                    help='Contests to include in CVR, by index.')

but get: parse_hart: error: too few arguments
"""

parser.add_argument('precinct_er',
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

    def tally(self):
        "Tally the contest. FIXME: assumes 2-winner contest"

        ranked = sorted(self.choices.values(), key=attrgetter('votes'), reverse=True)
        self.winners = [choice.name for choice in ranked[:2]]
        self.losers = [choice.name for choice in ranked[2:]]

        pool_votes = sum([choice.votes for choice in ranked[1:]])
        voted_ballots = ranked[0].votes + pool_votes

        if voted_ballots > 0:
            if len(ranked) > 2:
                self.margin = (ranked[1].votes - ranked[2].votes) / voted_ballots
            else:
                self.margin = 1.0
            self.majority_margin = (ranked[0].votes - pool_votes) / voted_ballots
        else:
            self.margin = float('NaN')
            self.majority_margin = self.margin


def obj_dict(obj):
    return obj.__dict__


def unicode_csv_DictReader(unicode_csv_data, dialect=csv.excel, **kwargs):
    # csv.py doesn't do Unicode; encode temporarily as UTF-8:
    csv_reader = csv.DictReader(utf_8_encoder(unicode_csv_data),
                            dialect=dialect, **kwargs)
    for row in csv_reader:
        # decode UTF-8 back to Unicode, cell by cell:
        yield [unicode(cell, 'utf-8') for cell in row]

def utf_8_encoder(unicode_csv_data):
    for line in unicode_csv_data:
        yield line.encode('utf-8')


def parse_hart_contest_table(parser):

    args = parser.parse_args()
    logging.basicConfig(level=args.debuglevel)
    print("parse_hart: %s" % (args,))

    contests = {}
    last_title = None

    # Tally Election results by precinct
    if True:  # "with" won't work: AttributeError: DictReader instance has no attribute '__exit__'
        reader = csv.DictReader(open(args.precinct_er))
        # FIXME: get utf-8 working e.g. via reader = unicode_csv_DictReader(open(name))

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

    max_precinct_count = max((contest.precinct_count for contest in contests.values()))
    print("Max precinct count: %d" % max_precinct_count)

    if args.minprecinctcount == -1:
        # -1 means use the maximum precinct count seen as the minimum requirement:
        # I.e. only audit county-wide contests
        args.minprecinctcount = max_precinct_count

    contests_sorted = contests.values()
    contests_sorted.sort(key=attrgetter('precedence'))
    contests_filtered = [contest for contest in contests_sorted if contest.precinct_count >= args.minprecinctcount]
    contests_sorted = contests_filtered

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

    # Set sensible default for value of cvr_contests
    if args.cvr_contests is None:
        args.cvr_contests = range(len(contests_filtered))
    else:
        args.cvr_contests = json.loads(args.cvr_contests)

    contests_filtered = [contest for contest in contests_sorted if contest.precinct_count >= args.minprecinctcount]
    contests_filtered = [contests_filtered[i] for i in args.cvr_contests]

    for contest in contests.values():
        contest.tally()
        # print(contest.winners.toJSON())

    for contest in contests_filtered:
        contest.selected = True
        print("Margin %.4f MajMargin %.4f for %d-candidate %s; winners: %s" % (contest.margin, contest.majority_margin, len(contest.choices), contest.name, contest.winners))
        # print(json.dumps(contest.choices.__dict__))
        #json.dump(contest.choices, open("/tmp/choices.json", "w"))

    #json.dump(contests.__dict__, open("/tmp/contests.json", "w"))

    #json.dump(contests.values(), open("/tmp/contests.json", "w"), default=obj_dict, indent=4)
    json.dump(contests, open("/tmp/contests.json", "w"), default=obj_dict, indent=4)

    with codecs.open("/tmp/cvr.csv", "wb", "utf-8") as cvrfile:
        csvwriter = csv.writer(cvrfile)
        info_headers = ["CvrNumber","TabulatorNum","BatchId","RecordId","ImprintedId","BallotType"]

        # line 1: election info
        csvwriter.writerow(['MockCVR', 'v1.0'])

        # line 2: contest names, one per choice
        row = [None] * len(info_headers)
        for contest in contests_filtered:
            row.extend([(contest.name + ' (Vote For=1)')] * len(contest.choices))
        csvwriter.writerow(row)

        # line 3: choice names.
        row = [None] * len(info_headers)
        for contest in contests_filtered:
            for choice in contest.choices:
               row.append(choice)
        csvwriter.writerow(row)

        # line 4: nominally party names
        row = info_headers
        row.extend([None] * sum((len(contest.choices) for contest in contests_filtered)))
        csvwriter.writerow(row)

        # content lines
        for cvrid in range(1, args.num_cvrs + 1):
            row = [cvrid, 1, 1, cvrid, "1-1-%d" % cvrid, "std"]
            for i, contest in enumerate(contests_filtered):
                # First 7 lines are for the winner, then alternate for loser and for winner
                # TODO: make 7 a parameter
                if len(contest.choices) > 1:
                    if (cvrid > 5) and (cvrid % 2 == 1):
                        row.extend([0, 1])
                    else:
                        row.extend([1, 0])
                    row.extend([0] * (len(contest.choices) - 2))
                else:
                    row.extend([1])
            csvwriter.writerow(row)


if __name__ == "__main__":
    parse_hart_contest_table(parser)
