#!/usr/bin/env python3
# coding=utf-8
"""
Analyze results of auditing results from rla_export, and assess whether
more auditing is needed.

Based on “BRAVO: Ballot-polling Risk-limiting Audits to Verify Outcomes”
 by Lindeman, Stark and Yates, 2012
 https://www.usenix.org/system/files/conference/evtwote12/evtwote12-final27.pdf

Note: for strict compliance with step 5 of the BRAVO algorithm as
presented, the votes have to be interpreted sequentially.

  5. If any T w` ≥ 1/α, reject the corresponding null hypothesis
  for each such T w`.  Once a null hypothesis is rejected, do not
  update its T w` after subsequent draws.

For contests with votes_allowed > 1, steps 3 and 4 imply that the number of
sampled votes for each candidate can vary depending on which other candidate
they are being compared with, since votes for both candidates are not counted
at all for either.

TODO: check also for sampling with replacement
"""

from __future__ import print_function
from __future__ import division

import random

import math
import os
import sys
import codecs
import json
import logging
import argparse
import collections
import pprint
import rlacalc
import functools
import types
from operator import itemgetter, attrgetter

parser = argparse.ArgumentParser(description='analyze_rounds.')

parser.add_argument("-d, --debuglevel", type=int, default=logging.WARNING, dest="debuglevel",
  help="Set logging level to debuglevel, expressed as an integer: "
  "DEBUG=10, INFO=20, endpoint timing=25, WARNING=30 (the default), ERROR=40, CRITICAL=50")

parser.add_argument('contests', nargs='?', default="contests.json",
                    help='contests.json file, default "contests.json"')
parser.add_argument('datadir', nargs='?',
                    help='Data directory with rla_export reports')

@functools.total_ordering
class Risk_record(object):
    """Record the risk that the reported results for a given choice
    in a given contest were wrong, along with expected sample size and
    associated data.
    """

    def __init__(self, risk_limit, winner_name, loser_name, winner_votes, loser_votes, winner_obs, loser_obs):
        self.winner_name = winner_name
        self.loser_name = loser_name
        self.winner_votes = winner_votes
        self.loser_votes = loser_votes
        self.winner_obs = winner_obs
        self.loser_obs = loser_obs

        self.margin = (winner_votes - loser_votes) / (winner_votes + loser_votes)
        self.risk = rlacalc.ballot_polling_risk_level(winner_votes, loser_votes, winner_obs, loser_obs)
        self.sample_est = max(0, rlacalc.findAsn(risk_limit, self.margin, self.risk))
        logging.info("Risk record: %s" % self)

    def __str__(self):
        return "Sample {sample_est}: Risk {risk:.2%} with margin: {margin:.2%}; counts W: {winner_votes} L: {loser_votes} w: {winner_obs} l: {loser_obs} for {winner_name} vs {loser_name}".format(**self.__dict__)

    def __eq__(self, other):
        return (self.sample_est == other.sample_est)

    def __lt__(self, other):
        return (self.sample_est < other.sample_est)

def outright_risk(contest, contests, possible_winner):
    """Calculate and display RLA risk level that reported results match sampled results
    on whether given candidate is actually outright winner.
    If a candidate has more votes than all others combined, they are the outright winner.

    Return Risk_record
    """

    # Get tallies for possible_winner and the pool of their opponents
    w = contests[contest['name']]['choices'][possible_winner]
    w_name = w['name']
    w_votes = w['votes']
    w_sample_tallies = w.get('sample_tally', 0)
    pool_name = "pool"
    pool = [choice for choice in contests[contest['name']]['choices'].values() if choice['name'] != w['name']]
    pool_votes = sum(choice['votes'] for choice in pool)
    pool_sample_tallies = sum(choice.get('sample_tally', 0) for choice in pool)

    # We want to confirm whether the reported result is right, and want
    # "w" to represent the reported winner, whether that is possible_winner or the pool.
    # So if possible_winner got less than 50%, swap with the sum of the pool of losers,

    if w_votes < pool_votes:
        winner_name, winner_votes, winner_obs = pool_name, pool_votes, pool_sample_tallies
        loser_name, loser_votes, loser_obs = w_name, w_votes, w_sample_tallies
    else:
        winner_name, winner_votes, winner_obs = w_name, w_votes, w_sample_tallies
        loser_name, loser_votes, loser_obs = pool_name, pool_votes, pool_sample_tallies

    risk_limit = 0.2 # TODO: set as an option

    risk_record = Risk_record(risk_limit, winner_name, loser_name, winner_votes, loser_votes, winner_obs, loser_obs)

    return risk_record


def contest_risk(contest, contests):
    """Calculate and display risk levels for the first phase of a runoff election.
    If a candidate has more votes than all others combined, they are the outright winner
    Otherwise, two candidates advance to a runoff.

    Return Risk_record
    """

    # print margin
    logging.debug("Contest with %d candidates: %s" % (len(contest['choices']), contest['name']))
    if 'selected' not in contest:
        return

    # TODO: avoid kludge to create magic risk_record that is less than any other
    max_risk_record = types.SimpleNamespace()
    max_risk_record.sample_est = -99999

    for choice in sorted(contests[contest['name']]['choices'].values(), key=itemgetter('votes'), reverse=True):
        risk_record = outright_risk(contest, contests, choice['name'])
        print("       %s" % risk_record)
        max_risk_record = max(max_risk_record, risk_record)

    # If highest-vote-getter got more than 50%, all we had to audit is whether they
    # really won outright.

    if contest['majority_margin'] > 0:
        return max_risk_record

    risk_limit = 0.2 # FIXME: set as an option

    # Compute risk levels for each pair of a winner and a loser
    contest['risk_levels'] = []
    for winner in contest['winners']:
        w = contests[contest['name']]['choices'][winner]
        for loser in contest['losers']:
            l = contests[contest['name']]['choices'][loser]
            risk_record = Risk_record(risk_limit, w['name'], l['name'], w['votes'], l['votes'], w.get('sample_tally', 0), l.get('sample_tally', 0))
            print("       %s" % risk_record)
            max_risk_record = max(max_risk_record, risk_record)

    return max_risk_record


def analyze_rounds(parser):

    args = parser.parse_args()
    logging.debug("analyze_rounds command-line arguments: %s" % (args,))

    # Read in contests, with winners and losers.
    contests = json.load(open(args.contests, "r"))

    acvrs = []
    if args.datadir:
        try:
            acvr_file = args.datadir + "/all_contest_audit_details_by_cvr.json"
            acvrs = json.load(open(acvr_file))
        except (IOError):
            logging.warning("Can't open ACVR file %s, assuming there are none" % acvr_file)

    # Count ACVRs per contest
    acvr_ballots = collections.Counter(cvr['contest_name'] for cvr in acvrs)
    logging.info("ACVRs entered: %s" % acvr_ballots.items())

    # Tally ACVRs
    # FIXME: check for consensus = "YES"
    # FIXME: check for, ignore overvotes
    acvr_tallies = collections.Counter((cvr['contest_name'], cvr['choice_per_audit_board']) for cvr in acvrs).items()
    logging.debug("acvr tallies: %s" % (acvr_tallies,))

    # Update contests data structure with sample_tally data
    for id, sample_votes in acvr_tallies:
        contest, choices = id
        for choice in json.loads(choices):
            contests[contest]['choices'][choice]['sample_tally'] = sample_votes

    logging.debug("Contests: %s" % contests)

    overall_max_risk_record = types.SimpleNamespace()
    overall_max_risk_record.sample_est = -99999

    for contest in contests.values():
        if 'selected' not in contest:
            continue

        print("Contest: %s, with %d candidates. %d samples entered" %
              (contest['name'], len(contest['choices']), acvr_ballots[contest['name']]))

        print()

        for choice in sorted(contests[contest['name']]['choices'].values(), key=itemgetter('votes'), reverse=True):
            print("  %d reported votes, %d sample votes for %s" % (choice['votes'], choice['sample_tally'], choice['name']))

        print()

        risk_record = contest_risk(contest, contests)
        print("\n  Max: %s\n\n" % risk_record)
        overall_max_risk_record = max(overall_max_risk_record, risk_record)

    # Report minumum across all contests, then levels for all contests
    # print("\n\nOverall max: %s" % overall_max_risk_record)


if __name__ == "__main__":
    args = parser.parse_args()
    logging.basicConfig(level=args.debuglevel)
    logging.info("analyze_rounds: %s" % (args,))

    analyze_rounds(parser)
