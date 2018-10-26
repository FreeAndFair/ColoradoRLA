#!/usr/bin/env bash

# Run from test/smoketest
# Output goes in $LOGDIR (/tmp by default, created if necessary) in files and export directories
# tagged by $LOGTAG (default "no-round-two")

LOGDIR=${LOGDIR:-/tmp}
LOGTAG=${LOGTAG:-contest-not-auditable}

#
# When a contest
# has an overall margin of
# zero, the contest would be NOT_AUDITABLE (at least, as we know how to
# do it currently.)
#
# If this happens, we shouldn't start an audit for it. Consider it
# finished, maybe, but never should it hold up the rest of the process.
#
# Let's see if we can reproduce that scenario!

mkdir -p $LOGDIR

(echo -e "\nDenver LAT contains a tied state rep district 16. It is not auditable and should not hold things back.\n"
 ./main.py reset dos_init -r 0.45
 ./main.py -c 16 -f ../2.0-test-data/DenverCVREdited.csv -F ../2.0-test-data/Denver_2018General_PublicLatManifest.csv county_setup
 # 1 = Gov / Lt. Gov
 # 6 = State Senator District 16
 ./main.py -d 10 -C 1 -C 6 -s 12345123451234512345 dos_start
 echo -e "\nGov / Lt Gov, State Senator District 16 targeted at 45% and started."
 echo -e "How many ballots in Denver County?\n"
) | tee $LOGDIR/$LOGTAG.sm

#rla_export -e $LOGDIR/$LOGTAG-setup.export
(
    echo -e "Press <RET> when ready to start all the rounds! You get either an infinite loop or complete in one round!"
    read
    ./main.py -c 16 -p '-1 1' county_audit
) | tee -a $LOGDIR/$LOGTAG.sm
