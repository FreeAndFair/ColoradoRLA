#!/bin/bash

set -m #job control

function go() {
    pushd ../../server/eclipse-project
    mvn clean compile
    if [ $? -ge 1 ]; then
        exit 1
    fi
    mvn exec:exec &
    sleep 10 # to boot
    popd
}

if [ "$1" == "go" ]; then
    go
    fg
else
    # empty db
    dropdb -U postgres corla
    if [ $? -ge 1 ]; then
        exit 1
    fi
    createdb -U postgres corla --owner corla
    go
    psql -h 127.0.0.1 -U corla -d corla -a -f ../corla-test-credentials.psql
    fg

fi
