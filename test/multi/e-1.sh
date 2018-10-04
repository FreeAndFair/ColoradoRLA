#!/usr/bin/env bash
./main.py reset dos_init
./main.py -c 1 county_setup -f ../multi/adams-regent.csv -F ../multi/adams-manifest.csv
./main.py -c 2 county_setup -f ../multi/alamosa-regent.csv -F ../multi/alamosa-manifest.csv
./main.py dos_start -C 0
./main.py -c 1 county_audit -p '1 3' -R 1
./main.py -c 2 county_audit -p '1 3' -R 1
