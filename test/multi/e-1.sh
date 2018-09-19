#!/usr/bin/env bash
../smoketest/main.py reset dos_init
../smoketest/main.py -c 1 county_setup -f ../multi/adams-regent.csv -F ../multi/adams-manifest.csv
../smoketest/main.py -c 2 county_setup -f ../multi/alamosa-regent.csv -F ../multi/alamosa-manifest.csv
../smoketest/main.py dos_start -C 0
../smoketest/main.py -c 1 county_audit -p '1 3' -R 1
../smoketest/main.py -c 2 county_audit -p '1 3' -R 1
