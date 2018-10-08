**rla_utils**: **parse_hart** and **analyze_rounds** allow the RLAtool used
for ballot-comparison risk-limiting audits in Colorado in 2017 (ColoradoRLA),
to be used in a ballot-polling risk-limiting audit.
`parse_hart` is used to create a mock CVR from the `contest_table` CSV file
produced by Hart's BallotNow system.
`rla_export` and `analyze_rounds` can then be used to estimate
an initial sample size.  Election officials can then use other software,
such as Philip Stark's [`https://www.stat.berkeley.edu/~stark/Vote/ballotPollTools.htm`](`https://www.stat.berkeley.edu/~stark/Vote/ballotPollTools.htm)
to record "Audit CVRs" to produce random selections from the official ballot
(and/or ballot-card) manifest.
One or more audit boards can then enter interpretations of each selected ballot
into the RLAtool in parallel, using one or more "counties" in RLAtool terminology.
`rla_export` and `analyze_rounds` can then be used to assess
the progress of the audit. It provides risk levels for all contests,
and an estimate of the number of remaining samples to audit.
If the risk limit has not yet been satisfied, additional rounds of audit board
entry and assesment can continue until either the risk limit is satisfied
or a full hand count is ordered.

Note: since Hart's system does not provide CVRs which can be matched to
the corresponding paper ballots, and RLAtool does not yet support
ballot-polling audits, we construct a mock CVR file just to get it
to allow entry of a sufficient number of ballots, all within a single huge round
which we never expect to complete.  The votes indicated in the mock CVR file
bear no resemblance to the actual votes, and the discrepancy reports from
the RLAtool are ignored.  We just use RLAtool to record and timestamp
the audit board entry of the ballots, and use other software to tally
the sampled ballots, calculate risk levels, and estimate sample sizes.

# Installation
The software requires Python 3.6,
and has been tested on Ubuntu Linux 18.04 LTS ("bionic").

Install the "ballot-polling" branch of rlacalc
from `https://github.com/nealmcb/audit_cvrs`.
Only the rlacalc.py and hug_noop.py files are needed.

Install the "orange" branch of ColoradoRLA from
https://github.com/nealmcb/ColoradoRLA/tree/orange

Install the [Python package rla_export](https://github.com/FreeAndFair/ColoradoRLA/releases/tag/v1.1.0.3).
It requires Python 2.7.

Add the directories of all those Python files to $PATH.

# Documentation
For now, use the `--help` option of each tool for help, and/or read the comments.

# Operation

    # Acquire the latest election results, as a `contest_table.txt` file (in CSV format).

    DATADIR=2018-primary
    cd $DATADIR
    wget http://ocvote.com/fileadmin/live/pri2018/media.zip
    unzip media.zip

    # Extract and tally the contests, and produce a mock cvr.csv file suitable
    # for driving the RLAtool.
    # Use the -C option to specify which contests to include in the CVR.
    # This creates /tmp/cvr.csv /tmp/contests.json

    parse_hart.py -C '[12, 13, 14, 15, 16]' $DATADIR/contest_table.txt > $DATADIR/parse_hart.out
    # FIXME: use a slightly different cvr count (always odd) for each county, so mock CVR numbers are different in each county.

    mv /tmp/cvr.csv /tmp/contests.json $DATADIR

    # Use $DATADIR/cvr.csv to manually initialize audit in RLAtool

    # Start a round.
    # Do the following steps until the risk limit is satisfied
    # or a full hand count is ordered
    rla_export -e $DATADIR/export
    analyze_rounds.py $DATADIR/export 2> $DATADIR/analyze_rounds.detail | tee $DATADIR/analyze_rounds.out

    # Use the data in $DATADIR/analyze_rounds.out to help choose a sample size,
    # or just use a small sample size like 10 or 20 samples per audit board.
    # If there are multiple cards per ballot, and they are sampled individually, multiply
    # sample size estimates by the average number of cards per ballot.
    # TODO: improve tool to help clarify which contest is closest, which sample sizes to go with

    # Use https://www.stat.berkeley.edu/~stark/Vote/ballotPollTools.htm
    # to select random samples based on the official manifest.
    # TODO: can I make that easier to script via some mostly-written python scripts?

    # Have audit boards log in to counties Use RLAtool to enter ballot interpretations.
    # Note that the Batch number and "Ballot Position #" values will not match the
    # ones printed out by ballotPollTools.  Record interpretations and Ballot Position #
    # on a paper worksheet so you have a permanent, auditable record of the audit evidence.

    # Go back up to "Start a round".
