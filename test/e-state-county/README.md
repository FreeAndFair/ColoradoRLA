# e-state-county: test for auditing three contests in 1, 2 or 3 counties

These files are test input for auditing three contests. One is in a single tiny county, one is in two counties, and 1 is in three counties.

Run via `audit.bash`.

Contests:

 * Prop 1, 20%, counties 1 2 and 3
 * Prop 2, 40%, counties 2 and 3
 * Prop 3, 10%, county 2

Ballot counts per county: 1000, 10, 100

CVR files generated via:

    genelect 1000 200 > cvr-1000--200.csv
    genelect 10 -2 4 1 > cvr-10--2-4-1.csv
    genelect 100 24 40 > cvr-100--24-40.csv
