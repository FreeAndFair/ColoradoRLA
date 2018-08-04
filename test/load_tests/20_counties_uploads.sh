#!/bin/sh
# argument 1 is delay between simulated user interactions (in seconds)
# argument 2 is delay between county starts (in seconds)
if [ "$#" != "2" ]; then
    echo "Usage: upload_script.sh <user delay> <county start delay>"
    echo "See script source for details."
    exit
fi
./main.py -u http://corla-test.galois.com/api/ reset dos_init > 0.log
for c in 1 2 3 4 5 6 7 8; do
    ./main.py -u http://corla-test.galois.com/api/ -T $1 -c $c county_setup -f small_cvrs.csv -F small_manifest.csv > $c.log &
    sleep $((RANDOM % $2 + 1));
done &
for c in 9 10 11 12 13 14 15 16; do
    ./main.py -u http://corla-test.galois.com/api/ -T $1 -c $c county_setup -f medium_cvrs.csv -F medium_manifest.csv > $c.log &
    sleep $((RANDOM % $2 + 1));
done &
for c in 17 18; do
    ./main.py -u http://corla-test.galois.com/api/ -T $1 -c $c county_setup -f large_cvrs.csv -F large_manifest.csv > $c.log &
    sleep $((RANDOM % $2 + 1));
done &
for c in 19 20; do
    ./main.py -u http://corla-test.galois.com/api/ -T 3 -c $c county_setup -f huge_cvrs.csv -F huge_manifest.csv > $c.log &
    sleep $((RANDOM % 3 + 1));
done &
for job in `jobs -p`; do
    wait $job
done

