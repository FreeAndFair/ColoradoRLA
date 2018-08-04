#!/bin/sh
# argument 1 is delay between simulated user interactions (in seconds)
# argument 2 is delay between county starts (in seconds)
if [ "$#" != "2" ]; then
    echo "Usage: upload_script.sh <user delay> <county start delay>"
    echo "See script source for details."
    exit
fi
./main.py -u http://192.168.24.43/api/ reset dos_init > 0.log
for c in 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20; do
    ./main.py -u http://192.168.24.43/api/ -T $1 -c $c county_setup -f 20.csv -F small_manifest.csv > $c.log &
    sleep $((RANDOM % $2 + 1));
done &
for c in 21 22 23 24 25 26 27 28 29 30; do
    ./main.py -u http://192.168.24.43/api/ -T $1 -c $c county_setup -f 30.csv -F medium_manifest.csv > $c.log &
    sleep $((RANDOM % $2 + 1));
done &
for c in 31 32 33 34 35 36 37 38 39 40; do
    ./main.py -u http://192.168.24.43/api/ -T $1 -c $c county_setup -f 50.csv -F large_manifest.csv > $c.log &
    sleep $((RANDOM % $2 + 1));
done &
for c in 41 42 43 44 45; do
    ./main.py -u http://192.168.24.43/api/ -T 3 -c $c county_setup -f 100.csv -F huge_manifest.csv > $c.log &
    sleep $((RANDOM % 3 + 1));
done &
for c in 46 47 48 49; do
    ./main.py -u http://192.168.24.43/api/ -T 3 -c $c county_setup -f 250.csv -F huge_manifest.csv > $c.log &
    sleep $((RANDOM % 3 + 1));
done &
for c in 50 51 52 53 54; do
    ./main.py -u http://192.168.24.43/api/ -T 3 -c $c county_setup -f 300.csv -F huge_manifest.csv > $c.log &
    sleep $((RANDOM % 3 + 1));
done &
for c in 55 56 57 58; do
    ./main.py -u http://192.168.24.43/api/ -T 3 -c $c county_setup -f 500.csv -F huge_manifest.csv > $c.log &
    sleep $((RANDOM % 3 + 1));
done &
for job in `jobs -p`; do
    wait $job
done

