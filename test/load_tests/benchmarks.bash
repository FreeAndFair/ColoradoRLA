#!/bin/bash
# Run a series of benchmarks.
# First set the environmental variable URL to the url to use
# For example: $0 mytest "2 4 8" 4 large
# Provide these arguments, of which only the first is mandatory:

testname=$1             # Directory to create and save results in
series=${2:-2 4 8 16 32 64} # a list (in quotes) of values for the number of counties to run in parallel
repeat=${3:-3}		# Number of times to repeat each element in series
cvrs=${4:-medium}	# cvrs to load for each test,
			# e.g. "medium" for medium_cvrs.csv and medium_manifest.csv

mkdir -p $testname
cd $testname

# Save all script output in benchmark, while watching as it comes out
exec > >(tee benchmarks.out)

echo Test $testname, loading $cvrs on $URL for n=$series

results=""

for n in $series; do
  for i in $(seq $repeat); do
    crtest -u $URL reset dos_init

    start=$(date +%s)
    echo Start: $(date -Is), $n in parallel, trial $i

    for c in $(seq $n); do
	crtest -u $URL -c $c county_setup -f ../${cvrs}_cvrs.csv -F ../${cvrs}_manifest.csv > $c.log 2> $c.err &
    done

    for job in `jobs -p`; do
	wait $job
    done
    end=$(date +%s)
    interval=$((end - start))
    results="$results	$interval"
    echo "Done:  $(date -Is) with n=$n, trial $i, after $interval seconds"
  done
done

echo Paste series, repeats, results into spreadsheet: "$series	$repeat	$results"
