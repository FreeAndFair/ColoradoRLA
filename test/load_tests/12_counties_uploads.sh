#!/bin/sh
./main.py -u http://corla-test.galois.com/api/ reset dos_init > 0.log
for c in 1 2 3 4; do
    ./main.py -u http://corla-test.galois.com/api/ -c $c county_setup -f large_cvrs.csv -F large_manifest.csv > $c.log &
done &
for c in 5 6 7 8; do
    ./main.py -u http://corla-test.galois.com/api/ -c $c county_setup -f medium_cvrs.csv -F medium_manifest.csv > $c.log &
done &
for c in 9 10 11 12; do
    ./main.py -u http://corla-test.galois.com/api/ -c $c county_setup -f small_cvrs.csv -F small_manifest.csv > $c.log &
done &
for job in `jobs -p`; do
    wait $job
done

