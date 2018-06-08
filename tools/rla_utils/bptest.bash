# Run test of ballot polling tools.
# Convert contest_table_test.csv to a CVR file, simulate auditing cvrs from it, export the results
# and analyze them.

# Execute from tools/rla_utils directory

set -eux -o pipefail

# Need rla_export python package
outdir="/tmp/bptest$$"
exports=$outdir/bptest.exports
mkdir $outdir

# contest_table=${contest_table:contest_table_test.csv}

./parse_hart.py "$@"    # creates files like /tmp/cvr.csv /tmp/contests.json
# ../../test/smoketest/main.py -p '-1 1' -f /tmp/cvr.csv | tee $outdir/bptest.crtest.out

# crtest -N 519 -R 1 -p '-1 1' -f /tmp/cvr.csv -F ../../test/e-1/arapahoe-manifest.csv -B tallyfile | tee $outdir/bptest.crtest.out # 13.8% margin, 248 * 2.09 (90th percentile)
crtest -N 176 -C -1 -R 1 -p '-1 1' -f /tmp/cvr.csv -F ../../test/e-1/arapahoe-manifest.csv -B tallyfile | tee $outdir/bptest.crtest.out # 13.8% margin, 248 * 0.71 (50th percentile)

# crtest -N 474 -R 1 -p '-1 1' -f /tmp/cvr.csv -F ../../test/e-1/arapahoe-manifest.csv -B tallyfile | tee $outdir/bptest.crtest.out  # 1157 * .41
# crtest -N 822 -R 1 -p '-1 1' -f /tmp/cvr.csv -F ../../test/e-1/arapahoe-manifest.csv -B tallyfile | tee $outdir/bptest.crtest.out # 1157 * .71 (50th percentile)
# crtest -N 2419 -R 1 -p '-1 1' -f /tmp/cvr.csv -F ../../test/e-1/arapahoe-manifest.csv -B tallyfile | tee $outdir/bptest.crtest.out # 1157 * 2.09 (90th percentile)
rla_export -e $exports
# fix it so that some winners and some losers get votes
# sed -i -e 's,JOHN DOWELL,BERNIE SANDERS,' -e 's,DARRYL W. PERRY,HILLARY CLINTON,' $exports/all_contest_audit_details_by_cvr.json
# sed -i -e 's,JOHN DOWELL,BERNIE SANDERS,' -e 's,DARRYL W. PERRY,HILLARY CLINTON,' $exports/all_contest_audit_details_by_cvr.json

echo $exports

analyze_rounds $exports 2> $outdir/analyze_rounds.detail | tee $outdir/analyze_rounds.out # reads from rla_export detail and tallies from /tmp/contests.json
