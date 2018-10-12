#!/usr/bin/env bash

# As of v2.0.4, if an audited cvr contained a discrepancy on a contest
# that was targeted, but the ballot card had not been selected for audit
# in that contest, the discrepancy would appear in the "Audited Contest
# Discrepancies" column of the DoS dashboard.
#
# Given these parameters, the ballot imprinted with "9-3200-9" will have
# both contests on it and be selected for audit in the Adams County
# Commissioner contest. Adding a discrepancy in the Regent contest
# should put a tick in the "Non-audited Contest Discrepancies" column.

# Run from test/smoketest

# Output goes in $LOGDIR (/tmp by default, created if necessary) in files and export directories
# tagged by $LOGTAG (default "non-audited-contest-discrepancies")

LOGDIR=${LOGDIR:-/tmp}
LOGTAG=${LOGTAG:-non-audited-contest-discrepancies}

mkdir -p $LOGDIR

(echo -e "\nDefining audit and upload county data\n"
 ./main.py reset
 ./main.py dos_init -r 0.10
 ./main.py -c 1 -f ../2.0-test-data/adams-cvrexport.csv -F ../2.0-test-data/adams-manifest.csv county_setup
 ./main.py -c 2 -f ../2.0-test-data/alamosa-cvrexport.csv -F ../2.0-test-data/alamosa-manifest.csv county_setup
 # Contests: 0=Adams Commissioner, 1=Regent, (2=Regent,) 3=Alamosa Commissioner
 ./main.py -C 0 -C 1 -C 3 -s 01234567890123456789 dos_start
 echo -e "\nStatewide Regent and Alamosa County Commissioner @ 10%, seeded '01234567890123456789'\n"
) | tee $LOGDIR/$LOGTAG.sm


echo -e "\nPress <RET> when ready to run county audits!\n"
read

rla_export -e $LOGDIR/$LOGTAG-setup.export

(
    echo -e \
         "\nAdams County has a discrepancy on ballot '9-3200-9'."\
         "\nBallot selected for Adams Commissioner Contest, but the discrepancy is in the Regent contest.\n"
    ./main.py -c 1 -p '8 17' -l "Distant Loser" -R 1 county_audit
    ./main.py -c 2 -p '-1 1' -R 1 county_audit
) | tee -a $LOGDIR/$LOGTAG.sm

rla_export -e $LOGDIR/$LOGTAG-round1.export
