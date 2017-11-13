#!/usr/bin/env bash
set -eux -o pipefail
function cleanup {
  set +x
  echo
  echo "To see the ends of the logs (some of which might not have been created):"
  echo tail "${SERVER_DIR}"/target/mvn.stdout
  echo tail "${SERVER_DIR}"/target/server.stdout
  echo tail credentials.stdout
  echo tail server_test.stdout
  echo "If you're done with the server: pkill -f java.-jar.target/colorado_rla"
  echo "If this script doesn't exit now, kill it from the keyboard. FIXME."
}
trap cleanup EXIT

echo Smoketest at `date`
git log -1
git status -uno

# Put these somewhere else before running via travis
export TRAVIS_BUILD_DIR=`git rev-parse --show-toplevel`
export CLIENT_DIR="${TRAVIS_BUILD_DIR}/client"
export SERVER_DIR="${TRAVIS_BUILD_DIR}/server/eclipse-project"
export TEST_DIR="${TRAVIS_BUILD_DIR}/test"
export SQL_DIR=${SERVER_DIR}/script/rla_export/rla_export/sql


# Exit early if there were no server changes.
# Don't run this until we're in travis. It may exit early....
# "${TRAVIS_BUILD_DIR}/ci/changes-in-dir" server || exit 0

cd "${SERVER_DIR}"

pkill -f java.-jar.target/colorado_rla || true
dropdb corla || true
createdb -O corla corla

mkdir -p target
mvn package > target/mvn.stdout

# Surprising how kludgey this seems to be https://stackoverflow.com/a/45657043/507544
version=$(sed < pom.xml '2 s/xmlns=".*"//g' | xmllint --xpath '/project/version/text()' - 2>/dev/null)
jar=target/colorado_rla-$version-shaded.jar
echo Built $jar

# for port 8887, add this argument: src/main/resources/us/freeandfair/corla/proxiable.properties
java -jar $jar > target/server.stdout &

( tail -f -n0 target/server.stdout & ) | grep -q "INFO Server:444 - Started"

cd ${TEST_DIR}/smoketest
psql -d corla -a -f ../corla-test-credentials.psql > credentials.stdout

./main.py

# Compare tabulation results and vote totals
echo "Run tabulation test: psql -d corla -f $SQL_DIR/m_tabulate.sql | diff tabulate.out -"
psql -d corla -f $SQL_DIR/m_tabulate.sql | diff tabulate.out - && echo "Success: tabulation test"

echo "Run manifest vs cvr test: psql -d corla -f $SQL_DIR/batch_count_comparison.sql | diff manifest-vs-cvr.out -"
psql -d corla -f $SQL_DIR/batch_count_comparison.sql | diff manifest-vs-cvr.out - && echo "Success: manifest vs cvr"

echo "All done!"

# pytest server_test.py > server_test.stdout || true

# egrep '====|____' server_test.out
