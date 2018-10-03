#!/usr/bin/env bash

# Automate auditing of 4 contests in 3 counties. This is using the seed
# and risk limit that we've been hand-testing this data set with.

# Run from test/smoketest

# Output goes in $LOGDIR (/tmp by default, created if necessary) in files and export directories
# tagged by $LOGTAG (default "2-point-oh")

LOGDIR=${LOGDIR:-/tmp}
LOGTAG=${LOGTAG:-2-point-oh}

mkdir -p $LOGDIR

(
    # try skipping this also
    ./main.py reset

    echo -e "\nStart 'Define audit' step and upload county audit data\n"
    ./main.py dos_init -r 0.10

    ./main.py -c 1 -f ../2.0-test-data/adams-cvrexport.csv -F ../2.0-test-data/adams-manifest.csv county_setup
    ./main.py -c 2 -f ../2.0-test-data/alamosa-cvrexport.csv -F ../2.0-test-data/alamosa-manifest.csv county_setup

    echo -e "\nStatewide Regent and Alamosa County Commissioner targeted at 10% and started!\n"
    ./main.py -C 0 -C 3 -s 12341234123412341234 dos_start
) | tee $LOGDIR/$LOGTAG.sm


echo -e "Press <RET> when ready to audit..."
read

rla_export -e $LOGDIR/$LOGTAG-setup.export

(
    echo -e "\nRun first round, no discrepancies \n"

    ./main.py -c 1 -p '-1 100' -R 1 county_audit
    ./main.py -c 2 -p '-1 100' -R 1 county_audit
) | tee -a $LOGDIR/$LOGTAG.sm

rla_export -e $LOGDIR/$LOGTAG-round1.export
