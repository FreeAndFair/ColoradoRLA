#!/usr/bin/env bash

LOGDIR=${LOGDIR:-/tmp}
LOGTAG=${LOGTAG:-multi-winner-contest}

mkdir -p $LOGDIR

# The Williamsburg Town Trustees is a vote for four out of five contest.
#
# 1. Donnita Hawkins: 19
# 2.    Eva L. Mares: 12
# 3.   Steve Ricotta: 7
# 4.     John Purvis: 5
# 5. William R. Esch: 4

# As of v2.0.6, when we load this data the we get something like this:
#
# corla=> select * from county_contest_result;
# -[ RECORD 1 ]--------+----------------------------------------------------------------
# id                   | 262
# contest_ballot_count | 23
# county_ballot_count  | 56
# losers               | ["William R Esch"]
# max_margin           | 15
# min_margin           | 1
# version              | 1
# winners              | ["Steve Ricotta","Eva L Mares","John Purvis","Donnita Hawkins"]
# winners_allowed      | 4
# contest_id           | 261
# county_id            | 22

# Time: 0.412 ms
# corla=> select * from contest_result;
# -[ RECORD 1 ]---+------------------------------------------------------------------------------------------------------------------
# id              | 327
# audit_reason    | 1
# ballot_count    | 56
# contest_cvr_ids | [265,266,270,274,275,276,279,280,284,286,287,289,290,293,294,295,300,301,302,303,305,306,309,311,312,315,317,318]
# contest_name    | Williamsburg Town Trustees
# diluted_margin  | 0.13
# losers          | ["Steve Ricotta","Eva L Mares","John Purvis","William R Esch"]
# min_margin      | 7
# version         | 2
# winners         | ["Donnita Hawkins"]
# winners_allowed | 1

# What we should see is something like this:
#
# corla=> select * from county_contest_result;
# -[ RECORD 1 ]--------+----------------------------------------------------------------
# id                   | 530
# contest_ballot_count | 23
# county_ballot_count  | 56
# losers               | ["William R Esch"]
# max_margin           | 15
# min_margin           | 1
# version              | 1
# winners              | ["Steve Ricotta","Eva L Mares","John Purvis","Donnita Hawkins"]
# winners_allowed      | 4
# contest_id           | 529
# county_id            | 22
#
# Time: 0.542 ms
# corla=> select * from contest_result;
# -[ RECORD 1 ]---+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
# id              | 595
# audit_reason    | 1
# ballot_count    | 56
# contest_cvr_ids | [531,532,533,534,535,536,537,538,539,540,541,543,544,545,546,547,548,549,550,551,552,553,554,555,556,557,558,559,560,561,562,563,564,565,566,567,568,569,570,571,572,573,574,575,576,577,578,579,580,581,582,583,584,585,586,587]
# contest_name    | Williamsburg Town Trustees
# diluted_margin  | 0.02
# losers          | ["William R Esch"]
# min_margin      | 1
# version         | 2
# winners         | ["Steve Ricotta","Eva L Mares","John Purvis","Donnita Hawkins"]
# winners_allowed | 4
# max_margin      | 15
#
# Time: 0.636 ms

# The smallest margin is 1, so with 56 ballots cast - not all of them
# contain the contest, but that's not considered - the diluted margin is
# 0.017857142857142856, so we should see 268 samples needed to achieve a
# risk limit of 10%
#
# user> (import 'us.freeandfair.corla.math.Audit)
# ;;=> us.freeandfair.corla.math.Audit
# user> (import '(java.math BigDecimal))
# ;;=> java.math.BigDecimal
# user> (Audit/optimistic
#         (BigDecimal. 0.1)          ;; 10% risk limit
#         (BigDecimal. (/ 1.0 56)))  ;; diluted margin
# ;;=> 268M
#
# Looks like that's right according to both the code and Neal's tool
# (http://bcn.boulder.co.us/~neal/electionaudits/rlacalc.html)
# 🗳

(
    echo -e "\nWilliamsburg Town Trustees (Vote for 4 out of 5) audited at 10%.\n"
    ./main.py reset
    ./main.py dos_init -r 0.10

    ./main.py -c 22 -f ../2.0-test-data/fremont-multi-winner-cvr.csv -F ../2.0-test-data/fremont-manifest.csv county_setup

    echo -e "\nPress <RET> when ready to launch the audit\n"
    read

    ./main.py -C 0 -s 12345678901234567890 dos_start
) | tee $LOGDIR/$LOGTAG.sm
