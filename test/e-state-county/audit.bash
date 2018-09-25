#!/usr/bin/env bash

# automate auditing of 3 contests in 3 counties
# Output goes in $LOGDIR (/tmp by default, created if necessary) in files and export directories
# tagged by $LOGTAG (default "state-county")

LOGDIR=${LOGDIR:-/tmp}
LOGTAG=${LOGTAG:-state-county}

mkdir -p $LOGDIR

(
    # try skipping this also
    ./main.py reset

    echo -e "\nStart 'Define audit' step and upload county audit data\n"
    ./main.py dos_init

    ./main.py -c 1 -f ../e-state-county/cvr-1000--20-200.csv -F ../e-state/manifest-1-1000.csv county_setup
    # Avoid bug with contest "Prop 1" tied in just one county, use alternate CVR with margin of 1 there
    #./main.py -c 2 -f ../e-state-county/cvr-10--0--2-4-1.csv -F ../e-state/manifest-2-10.csv county_setup
    ./main.py -c 2 -f ../e-state-county/cvr-10--1--2-4-1.csv -F ../e-state/manifest-2-10.csv county_setup
    ./main.py -c 3 -f ../e-state-county/cvr-100--2-24-40.csv -F ../e-state/manifest-3-100.csv county_setup

    echo -e "\nFinish 'Define audit' step to select contests, enter the random seed, and Lanuch the Audit\n"

    ./main.py -C 1  -C 3 -C 4 -C 5  -C 7 -C 8 dos_start

) | tee $LOGDIR/$LOGTAG.sm

rla_export -e $LOGDIR/$LOGTAG-setup.export

(
    echo -e "\nRun first round, with one discrepancy in county 1 (Adams)\n"

    ./main.py -c 1 -p '1 100' -R 1 county_audit
    ./main.py -c 2 -p '-1 100' -R 1 county_audit
    ./main.py -c 3 -p '-1 100' -R 1 county_audit
) | tee -a $LOGDIR/$LOGTAG.sm

rla_export -e $LOGDIR/$LOGTAG-round1.export

# Final round, no discrepancies
(
    echo -e "\nRun second round\n"

    ./main.py -c 1 -p '-1 100' -R 1 county_audit
    ./main.py -c 2 -p '-1 100' -R 1 county_audit
    ./main.py -c 3 -p '-1 100' -R 1 county_audit
) | tee -a $LOGDIR/$LOGTAG.sm

rla_export -e $LOGDIR/$LOGTAG-round2.export
