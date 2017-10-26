#!/bin/sh
./main.py -u http://corla-test.galois.com/api/ reset dos_init > 0.log
for c in 1 2 3 4 5 6; do
    ./main.py -u http://corla-test.galois.com/api/ -c $c county_setup -f large_cvrs.csv -F large_manifest.csv > $c.log &
done
for job in `jobs -p`; do
    wait $job
done
