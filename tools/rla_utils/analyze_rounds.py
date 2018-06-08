#!/usr/bin/env python
# coding=utf-8
"""
Analyze results of auditing results from rla_export, and assess whether more auditing is needed.

Based on “BRAVO: Ballot-polling Risk-limiting Audits to Verify Outcomes” by Lindeman, Stark and Yates, 2012
 https://www.usenix.org/system/files/conference/evtwote12/evtwote12-final27.pdf

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
from operator import itemgetter, attrgetter


parser = argparse.ArgumentParser(description='analyze_rounds.')

parser.add_argument("-d, --debuglevel", type=int, default=logging.WARNING, dest="debuglevel",
  help="Set logging level to debuglevel, expressed as an integer: "
  "DEBUG=10, INFO=20, endpoint timing=25, WARNING=30 (the default), ERROR=40, CRITICAL=50")

parser.add_argument('exports', nargs='+',
                    help='Directory with rla_export reports')


def analyze_rounds(parser):

    args = parser.parse_args()
    print("analyze_rounds: %s" % (args,))
    # Read in contests, with winners and losers.
    #  Calculate from parse_hart.py?  Saved as json??
    #  Read in from rla_export all_contest_static.json?  No, that's based on wrong info. Could it be made right?

    contests = json.load(open("/tmp/contests.json", "r"))

    acvrs = json.load(open(args.exports[0] + "/all_contest_audit_details_by_cvr.json"))

    # Tally acvrs. TODO: check if necessary for consensus = "YES"
    acvr_tallies = collections.Counter((cvr['contest_name'], cvr['choice_per_audit_board']) for cvr in acvrs).items()
    print("acvr tallies: %s" % (acvr_tallies,))
    for id, sample_votes in acvr_tallies:
        contest, choices = id
        for choice in json.loads(choices):
            contests[contest]['choices'][choice]['sample_tally'] = sample_votes

    # print(contests)
    for contest in contests.values():
        # print margin
        logging.debug("Contest with %d candidates: %s" % (len(contest['choices']), contest['name']))
        contest['risk_levels'] = []
        for winner in contest['winners']:
            w = contests[contest['name']]['choices'][winner]
            for loser in contest['losers']:
                l = contests[contest['name']]['choices'][loser]
                risk_level = ballot_polling_risk_level(w['votes'], l['votes'], w.get('sample_tally', 0), l.get('sample_tally', 0))
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

        # TODO: deal with majority option
        w = contests[contest['name']]['choices'][contest['winners'][0]]
        w_votes = w['votes']
        w_sample_tallies = w.get('sample_tally', 0)
        others = [choice for choice in contests[contest['name']]['choices'].values() if choice['name'] != w['name']]
        other_votes = sum(choice['votes'] for choice in others)
        other_sample_tallies = sum(choice.get('sample_tally', 0) for choice in others)

        # If highest-vote-getter got less than 50%, swap with the sum of the others
        if w_votes < other_votes:
            w_votes, other_votes = other_votes , w_votes
            w_sample_tallies, other_sample_tallies = other_sample_tallies, w_sample_tallies

        majority_risk = ballot_polling_risk_level(w_votes, other_votes, w_sample_tallies, other_sample_tallies)
        contest_name = contest['name']
        logging.warning("risk {majority_risk} for {contest_name}: {w_votes}, {w_sample_tallies}  vs {other_votes}, {other_sample_tallies}".format(**locals()))
        max_risk = max(max_risk, majority_risk)

        # TODO: compute an initial mean sample size
        print("%.3f is max risk for contest with %d candidates: %s" % (max_risk, len(contest['choices']), contest['name']))
    # Report minumum across all contests, then levels for all contests

    # debugging
    for key, result in sorted(collections.Counter((cvr['contest_name'], cvr['choice_per_audit_board']) for cvr in acvrs).items()):
        print("%s\t%s\t%s" % (result, key[0], key[1]))


# TODO: move to rlacalc.py
def ballot_polling_risk_level(winner_votes, loser_votes, winner_obs, loser_obs):
    """
    Return the ballot polling risk level for a contest with the given overall
    vote totals and observed votes on selected ballots during a ballot polling
    risk-limiting audit.

    From the election tabulation system vote totals, for each contest, for each winner-loser pair (w,k),
    calculate s_wk = (number of votes for w)/(number of votes for w + number of votes for k)
    For each contest, for each winner-loser pair (w,k), set T_wk =1.
    For each line in `all_contest_audit_details_by_cvr` with consensus = "YES", change any T_wk values as indicated by the BRAVO algorithm.
    Multiply the final T_wk values by the risk limit. If for any contest the result is greater than or equal to one, that contest has met the risk limit.

    >>> ballot_polling_risk_level(1410, 1132, 170, 135) # Custer County 2018
    0.1342382069344729
    >>> ballot_polling_risk_level(2894, 1695, 45, 32)   # Las Animas County 2018
    0.47002027242290234
    >>> ballot_polling_risk_level(0, 0, 2000, 0)
    1.0
    >>> ballot_polling_risk_level(2894, 0, 1130, 0)   # Test overflow
    nan
    >>> ballot_polling_risk_level(100000, 0, 50000, 0)   # Test overflow
    nan

    The code is equivalent to this, but uses logs to prevent overflow
    T_wl = 1.0
    T_wl = T_wl * ((s_wl)/0.5) ** winner_obs
    T_wl = T_wl * ((1.0 - s_wl)/0.5) ** loser_obs
    """

    try:
        s_wl = winner_votes / (winner_votes + loser_votes)
    except ZeroDivisionError:
        return 1.0

    log_T_wl = math.log(1.0)
    try:
        log_T_wl = log_T_wl + ((math.log(s_wl) - math.log(0.5)) * winner_obs)
        log_T_wl = log_T_wl + ((math.log(1.0 - s_wl) - math.log(0.5)) * loser_obs)
        risk_level = math.log(1.0) - log_T_wl
    except ValueError:
        risk_level = float('NaN')

    return math.exp(risk_level)


if __name__ == "__main__":
    analyze_rounds(parser)
