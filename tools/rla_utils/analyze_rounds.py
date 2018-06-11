#!/usr/bin/env python
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
from operator import itemgetter, attrgetter


parser = argparse.ArgumentParser(description='analyze_rounds.')

parser.add_argument("-d, --debuglevel", type=int, default=logging.WARNING, dest="debuglevel",
  help="Set logging level to debuglevel, expressed as an integer: "
  "DEBUG=10, INFO=20, endpoint timing=25, WARNING=30 (the default), ERROR=40, CRITICAL=50")

parser.add_argument('exports', nargs='?',
                    help='Directory with rla_export reports')


def analyze_rounds(parser):

    args = parser.parse_args()
    print("analyze_rounds: %s" % (args,))
    # Read in contests, with winners and losers.
    #  Calculate from parse_hart.py?  Saved as json??
    #  Read in from rla_export all_contest_static.json?  No, that's based on wrong info. Could it be made right?

    contests = json.load(open("/tmp/contests.json", "r"))

    try:
        acvrs = json.load(open(args.exports + "/all_contest_audit_details_by_cvr.json"))
    except:
        acvrs = []

    # Tally acvrs.
    # FIXME: check for consensus = "YES"
    # FIXME: check for, ignore overvotes
    acvr_tallies = collections.Counter((cvr['contest_name'], cvr['choice_per_audit_board']) for cvr in acvrs).items()
    print("acvr tallies: %s" % (acvr_tallies,))
    for id, sample_votes in acvr_tallies:
        contest, choices = id
        for choice in json.loads(choices):
            contests[contest]['choices'][choice]['sample_tally'] = sample_votes

    # print(contests)
    print("maxsample\t2nd_vs_3rd\toutright\tmaxrisk\toutrightrisk\tcandidates\tcontest")
    for contest in contests.values():
        # print margin
        logging.debug("Contest with %d candidates: %s" % (len(contest['choices']), contest['name']))
        if 'selected' not in contest:
            continue
        contest['risk_levels'] = []
        for winner in contest['winners']:
            w = contests[contest['name']]['choices'][winner]
            for loser in contest['losers']:
                l = contests[contest['name']]['choices'][loser]
                risk_level = rlacalc.ballot_polling_risk_level(w['votes'], l['votes'], w.get('sample_tally', 0), l.get('sample_tally', 0))
                logging.warning("%.3f for %s: %d %d %d %d in %s vs %s" % (risk_level, contest['name'], w['votes'], l['votes'], w.get('sample_tally', 0), l.get('sample_tally', 0), w['name'], l['name']))
                contest['risk_levels'].append((risk_level, w, l))
                # contests[contest['name']]['choices'][choice]['risk_level'] = risk_level
                # Track minimum risk level across all combinations
                # print("%s %s %s: %s" % (contest, w, l, ballot_polling_risk_level(winner_votes, loser_votes, winner_obs, loser_obs)))
                # If minimum is more than risk level, estimate how much more to do
        if contest['risk_levels']:
            max_risk = max(triple[0] for triple in contest['risk_levels'])
        else:
            max_risk = 0.0

        # Deal with single outright majority winner outcome: winner vs all others combined in a pool
        w = contests[contest['name']]['choices'][contest['winners'][0]]
        w_votes = w['votes']
        w_sample_tallies = w.get('sample_tally', 0)
        pool = [choice for choice in contests[contest['name']]['choices'].values() if choice['name'] != w['name']]
        pool_votes = sum(choice['votes'] for choice in pool)
        pool_sample_tallies = sum(choice.get('sample_tally', 0) for choice in pool)

        # If highest-vote-getter got less than 50%, swap with the sum of the pool of losers
        if w_votes < pool_votes:
            w_votes, pool_votes = pool_votes , w_votes
            w_sample_tallies, pool_sample_tallies = pool_sample_tallies, w_sample_tallies

        majority_risk = rlacalc.ballot_polling_risk_level(w_votes, pool_votes, w_sample_tallies, pool_sample_tallies)
        contest_name = contest['name']
        logging.warning("risk {majority_risk} for {contest_name}: {w_votes}, {w_sample_tallies}  vs {pool_votes}, {pool_sample_tallies}".format(**locals()))
        max_risk = max(max_risk, majority_risk)

        # Compute an initial mean sample size.
        risk_limit = 0.2 # TODO: set as an option
        sample_size = rlacalc.findAsn(risk_limit, contest['margin'], max_risk)
        sample_size_maj = rlacalc.findAsn(risk_limit, contest['majority_margin'], majority_risk)
        max_sample_size = max(sample_size, sample_size_maj)
        print("%.0f\t%.0f\t%.0f\t%.3f\t%.3f\t%s\t%s" % (max_sample_size, sample_size, sample_size_maj, max_risk, majority_risk, len(contest['choices']), contest['name']))
        # print("2nd vs 3rd: %d mean sample size, %.3f max risk for contest with %d candidates: %s" % (sample_size, max_risk, len(contest['choices']), contest['name']))
        # print("Majority: %d mean sample size, %.3f risk for contest with %d candidates: %s" % (sample_size_maj, majority_risk, len(contest['choices']), contest['name']))
    # Report minumum across all contests, then levels for all contests

    # debugging
    for key, result in sorted(collections.Counter((cvr['contest_name'], cvr['choice_per_audit_board']) for cvr in acvrs).items()):
        print("%s\t%s\t%s" % (result, key[0], key[1]))


if __name__ == "__main__":
    analyze_rounds(parser)
