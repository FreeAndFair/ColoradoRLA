#!/usr/bin/env bash

LOGDIR=${LOGDIR:-/tmp}
LOGTAG=${LOGTAG:-multi-winner-contest}

mkdir -p $LOGDIR

(
    echo -e "\nRegent contest audited at 10%. Adams has UPPERCASE CHOICES and Alamosa has Title Case Choices\n"
    ./main.py reset
    ./main.py dos_init -r 0.10

    ./main.py -c 1 -f ../2.0-test-data/adams-fun-case-cvrexport.csv -F ../2.0-test-data/adams-manifest.csv county_setup
    ./main.py -c 2 -f ../2.0-test-data/alamosa-cvrexport.csv -F ../2.0-test-data/alamosa-manifest.csv county_setup

    echo -e "\nPress <RET> when ready to launch the audit\n"
    read

    ./main.py -C 0 -s 12345678901234567890 dos_start
) | tee $LOGDIR/$LOGTAG.sm
