#!/usr/bin/env bash

# Run from test/smoketest
# Output goes in $LOGDIR (/tmp by default, created if necessary) in files and export directories
# tagged by $LOGTAG (default "no-round-two")

LOGDIR=${LOGDIR:-/tmp}
LOGTAG=${LOGTAG:-no-round-two}

mkdir -p $LOGDIR

(
    echo -e "\nSetup counties 1-5 with the same Adams County CVRs\n"
    ./main.py reset
    ./main.py dos_init -r 0.10

    ./main.py -c 1 -f ../2.0-test-data/adams-cvrexport.csv -F ../2.0-test-data/adams-manifest.csv county_setup
    ./main.py -c 2 -f ../2.0-test-data/adams-cvrexport.csv -F ../2.0-test-data/adams-manifest.csv county_setup
    ./main.py -c 3 -f ../2.0-test-data/adams-cvrexport.csv -F ../2.0-test-data/adams-manifest.csv county_setup
    ./main.py -c 4 -f ../2.0-test-data/adams-cvrexport.csv -F ../2.0-test-data/adams-manifest.csv county_setup
    ./main.py -c 5 -f ../2.0-test-data/adams-cvrexport.csv -F ../2.0-test-data/adams-manifest.csv county_setup

    ./main.py -C 0 -s 12345678901234567890 dos_start
    echo -e "\nStatewide Regent targeted at 10% and started."
    echo -e "4 ballots in Adams County\n"
    echo -e "0 ballots in Alamosa County\n"
    echo -e "2 ballots in Arapaho County\n"
    echo -e "0 ballots in Archuleta County\n"
    echo -e "3 ballots in Baca County\n"
) | tee $LOGDIR/$LOGTAG.sm

#rla_export -e $LOGDIR/$LOGTAG-setup.export

echo -e "Press <RET> when ready to start round one."
read

(
    echo -e "One discrepancies in Adams County\n"
    ./main.py -c 1 -p '1 4' -R 1 county_audit

    echo -e "\nAlamosa County has nothing to do and signs-off\n"
    ./main.py -c 2 login_signoff_round

    echo -e "\nZero discrepancy in Arapaho County\n"
    ./main.py -c 3 -p '-1 100' -R 1 county_audit

    echo -e "\nArchuleta County has nothing to do and signs-off\n"
    ./main.py -c 4 login_signoff_round

    echo -e "\nZero discrepancy in Baca County\n"
    ./main.py -c 5 -p '-1 100' -R 1 county_audit

    echo -e "\nEveryone should be waiting for round start"
) | tee -a $LOGDIR/$LOGTAG.sm


echo -e "Press <RET> when ready to start round two."
read

(
    ./main.py start_audit_round
    echo -e "Adams County has nothing to do and signs-off\n"
    ./main.py -c 1 login_signoff_round

    echo -e "\nAlamosa County has nothing to do and signs-off\n"
    ./main.py -c 2 login_signoff_round

    echo -e "\nArapaho County has nothing to do and signs-off\n"
    ./main.py -c 3 login_signoff_round

    echo -e "\nZero discrepancy in Archuleta County\n"
    ./main.py -c 4 -p '-1 1' -R 1 county_audit

    echo -e "\nZero discrepancy in Baca County\n"
    ./main.py -c 5 -p '-1 100' -R 1 county_audit

    echo -e "\nAudit should be complete"
) | tee -a $LOGDIR/$LOGTAG.sm


    echo -e "\nM"
