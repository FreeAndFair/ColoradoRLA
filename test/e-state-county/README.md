# e-state-county: test for auditing four contests in 1, 2 or 3 counties

These files are test input for auditing four contests. Two are in all three
counties, one is in two counties, and one is in a single tiny county.

One is opportunistic, across all 3 counties, and the others are all
selected, to drive audits in the three corresponding ballot universes.

Run via `audit.bash`.

Contests:

 * Prop 1, 2%, counties 1 2 and 3 - opportunistic audit
 * Prop 2, 20%, counties 1 2 and 3
 * Prop 3, 40%, counties 2 and 3
 * Prop 4, 10%, county 2

Ballot counts per county: 1000, 10, 100

CVR files generated via:

    genelect 1000 20 200 > cvr-1000--20-200.csv
    genelect 10 0 -2 4 1 > cvr-10--0--2-4-1.csv
    # alternative which doesn't trigger 'tie' bug: genelect 10 1 -2 4 1 > cvr-10--1--2-4-1.csv
    genelect 100 2 24 40 > cvr-100--2-24-40.csv
