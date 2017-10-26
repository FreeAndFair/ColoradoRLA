#!/bin/sh
./main.py -u http://corla-test.galois.com/api/ reset dos_init > 0.log
for c in 1; do
    ./main.py -u http://corla-test.galois.com/api/ -c $c county_setup -f huge_cvrs.csv -F huge_manifest.csv > $c.log &
done
for job in `jobs -p`; do
    wait $job
done

