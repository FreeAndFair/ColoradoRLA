#!/usr/bin/env bash
set -eux -o pipefail

# Put these somewhere else before running via travis
export TRAVIS_BUILD_DIR=`git rev-parse --show-toplevel`
export CLIENT_DIR="${TRAVIS_BUILD_DIR}/client"
export SERVER_DIR="${TRAVIS_BUILD_DIR}/server/eclipse-project"
export TEST_DIR="${TRAVIS_BUILD_DIR}/test"


# Exit early if there were no server changes.
"${TRAVIS_BUILD_DIR}/ci/changes-in-dir" server || exit 0

cd "${SERVER_DIR}"

pkill -f java.-jar.target/colorado_rla || true
dropdb corla || true
createdb -O corla corla

# TODO do I want this?  mvn package> target/mvn.stdout

# Surprising how kludgey this seems to be https://stackoverflow.com/a/45657043/507544
version=$(sed < pom.xml '2 s/xmlns=".*"//g' | xmllint --xpath '/project/version/text()' - 2>/dev/null)
jar=target/colorado_rla-$version-shaded.jar
echo Built $jar

java -jar $jar src/main/resources/us/freeandfair/corla/proxiable.properties > target/server.stdout &

( tail -f -n0 target/server.stdout & ) | grep -q "INFO Server:444 - Started"

cd ${TEST_DIR}/smoketest
psql -d corla -a -f ../corla-test-credentials.psql > credentials.stdout

./main.py --update

rm -f server_test.json

# TODO: Figure out why this line sometimes causes the script to exit. Just retry until then.
zerotest server -p 8888 http://localhost:8887 -f server_test.json > zerotest.stdout 2> zerotest.stderr || true &

echo hit return when ready
read response

bash -xv <<"EndOfInput" > curls.out 2>&1
 # opts="-I"

 curl $opts http://localhost:8888/ballot-manifest/county?3
 curl $opts http://localhost:8888/ballot-manifest

 curl $opts http://localhost:8888/contest
 curl $opts http://localhost:8888/contest/county?3
 curl $opts http://localhost:8888/contest/id/201            # FIXME - parse out variable contest id
 curl $opts http://localhost:8888/cvr/county?3
 curl $opts http://localhost:8888/acvr/county?3
 curl $opts http://localhost:8888/acvr
EndOfInput

echo hit return when ready
read response

killall zerotest
pkill -f java.-jar.target/colorado_rla

#zerotest generate --ignore-headers date server_test.json > server_test_raw.py    # TODO how to call this?
zerotest generate --ignore-all-headers server_test.json > server_test_raw.py
sed 's,localhost:8887,localhost:8888,' server_test_raw.py > server_test_raw2.py

# pytest server_test_raw2.py

echo To see the logs, tail "${SERVER_DIR}"/target/server.stdout

echo 'make an archive directory, then cp -p server_test.json *.stdout *.stderr curls.out *raw*.py tabulate.out there'

echo You made it all the way to the end of the script!
