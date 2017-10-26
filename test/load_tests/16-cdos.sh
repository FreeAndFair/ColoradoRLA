#!/bin/sh
./main.py -u http://192.168.24.43/api/ reset dos_init > 0.log
for c in 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16; do
    ./main.py -u http://192.168.24.43/api/ -c $c county_setup -f medium_cvrs.csv -F medium_manifest.csv > $c.log &
done
for job in `jobs -p`; do
    wait $job
done
