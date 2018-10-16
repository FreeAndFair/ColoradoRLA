#!/bin/bash

n=10
pause=5
url="http://localhost:8888"
cookie_jar="default.jar"
username=""
login_url="${url}/auth-admin"
start_time=$(date +%s)
seconds=0

function help() {
    echo "$0 countyadmin1 -n 10 -p 5 -u http://localhost:8888"
    echo "-n number of requests"
    echo "-p seconds to pause between requests"
    echo "-u base url; scheme, host, port - no path!"
}

function parseArgs() {
    while [ "$#" -gt 0 ]; do
        case "$1" in
            -n) n="$2"; shift 2;;
            -p) pause="$2"; shift 2;;
            -u) url="$2"; shift 2;;
            -h) help
                exit 1;;

            --help) help; exit 1;;
            -*) echo "unknown option: $1" >&2; exit 1;;
            *) username="$1"; cookie_jar="${1}.jar"; shift;;
        esac
    done
}

function get(){
    path=$1;
    # not sure how to get the path into the output here
    curl -w "${seconds},%{time_total},${path},${username},%{http_code}\n" \
         -ksSL -o /dev/null \
         --cookie-jar $cookie_jar \
         --cookie $cookie_jar \
         --cookie "session={%22type%22:%22county%22}" \
         "${url}${path}"
}

function post(){
    curl -ksSL -o /dev/null \
         --cookie-jar $cookie_jar \
         --cookie $cookie_jar \
         --cookie "session={%22type%22:%22county%22}" \
         -XPOST -H "Content-Type: application/json" \
         -d "{\"username\": \"${username}\", \"password\": \"anything\", \"second_factor\": \"123\"}" \
         $@
}


# get cookie
function login(){
    post "${url}/auth-admin"
    post "${url}/auth-admin"
}


function req(){
    path=$1;
    get $path >> request-times.csv;
}

# poll the dashboard many times
function poll() {
    trap exit INT #easy quit

    for ((i=0; i<$n; i++)); do
        seconds=$(expr `date +%s` - $start_time) # close enough for a general time line
        req /county-dashboard;
        req "/contest/county?${str:$username:-1}";
        req /county-asm-state;
        req /audit-board-asm-state;
        printf "."
        sleep $pause
    done
    rm $cookie_jar;
    echo "${username} done"
}


# cat admins.txt | xargs -n 1 -P 64 ./polling.sh -p 1 -n 5
function main() {
    parseArgs $*
    login
    if [ $? -ge 1 ]; then
        exit 1
    fi
    poll
}

main $*
