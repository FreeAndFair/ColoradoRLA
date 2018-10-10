#!/usr/bin/env bash

# Automate auditing of 3 contests in 3 counties. 3 discrepancies in
# Adams move to a second round; no further discrepancies allow risk
# limit to be achieved.

# Run from test/smoketest

# Output goes in $LOGDIR (/tmp by default, created if necessary) in files and export directories
# tagged by $LOGTAG (default "state-county")

LOGDIR=${LOGDIR:-/tmp}
LOGTAG=${LOGTAG:-state-county}

mkdir -p $LOGDIR

(
    # try skipping this also
    ./main.py reset

    echo -e "\nStart 'Define audit' step and upload county audit data\n"
    ./main.py dos_init -r 0.05

    ./main.py -c 1 -f ../e-state-county/cvr-1000--20-200.csv -F ../e-state-county/manifest-1-1000.csv county_setup
    ./main.py -c 2 -f ../e-state-county/cvr-10--0--2-4-1.csv -F ../e-state-county/manifest-2-10.csv county_setup
    ./main.py -c 3 -f ../e-state-county/cvr-100--2-24-40.csv -F ../e-state-county/manifest-3-100.csv county_setup

    echo -e "\nFinish 'Define audit' step to select contests, enter the random seed, and Launch the Audit\n"

    ./main.py -C 0 -C 1 -C 4 -C 5 -s 12341234123412341234 dos_start

) | tee $LOGDIR/$LOGTAG.sm

rla_export -e $LOGDIR/$LOGTAG-setup.export

(
    echo -e "\nRun first round, with one discrepancy in county 1 (Adams)\n"

    ./main.py -c 1 -p '1 100' -R 1 county_audit
    ./main.py -c 2 -p '-1 100' -R 1 county_audit
    ./main.py -c 3 -p '-1 100' -R 1 county_audit
) | tee -a $LOGDIR/$LOGTAG.sm

rla_export -e $LOGDIR/$LOGTAG-round1.export

echo -e "\nPress <C-c> if you don't want to start a second round! Otherwise, press <RET> when ready...\n"
read
# Second round, no discrepancies
(
    echo -e "\nRun second round\n"

    ./main.py -c 1 -p '-1 100' -R 1 county_audit
    ./main.py -c 2 -p '-1 100' -R 1 county_audit
    ./main.py -c 3 -p '-1 100' -R 1 county_audit
) | tee -a $LOGDIR/$LOGTAG.sm

rla_export -e $LOGDIR/$LOGTAG-round2.export
