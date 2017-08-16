# Smoketest of RLA server

This smoketest has been tested against master commit 126b74c.
See results in smoketest.*.out, generated like this:

* Find most recent master commit in git log.
* Run `time ./smoketest.bash 2>&1 | tee history/smoketest.<commit>.out`

Note: the pytest tests from zerotest are currently unused and disabled.

## Installing Test Dependencies
For now, you'll need to install
[zerotest](https://github.com/jjyr/zerotest)
to run these tests, and/or generate new ones in the same way.

This is mainly tested with python3, but has been seen to work
on python2 also.  You will need these libraries for some of the tests:

`pip install zerotest requests`

or `pip3 install zerotest requests` (or similar) if you have
multiple Python installations.

## Running a smoketest

The current tests are based on the cvrs at
`test/e-1/arapahoe-regent-3-clear-CVR_Export.csv`
and the manifest at `test/e-1/arapahoe-manifest.csv`.

To run the smoketest, cd to this directory, and run

`./smoketest.bash`

That will run most of the current tests.

Some tests fail only because of ASM checking. To disable them, in
`server/eclipse-project/src/main/java/us/freeandfair/corla/endpoint/AbstractEndpoint.java`,
find the indicated line and change "false" to "true", leaving a warning comment.

```
  public static final boolean DISABLE_ASM = true;   // FIXME: should be false, Don't let "true" in to master!
```

You can do this via

`sed -i '/boolean DISABLE_ASM/s|false;|true;   // FIXME: should be false, Do not let "true" in to master...|' ../../server/eclipse-project/src/main/java/us/freeandfair/corla/endpoint/AbstractEndpoint.java`

To run tests by hand, see below.

### Resetting the database

Stop the server and look to see if there are any sessions
using the `corla` database:

`psql -c 'SELECT * FROM pg_stat_activity'`

Kill them.  E.g. `pkill -f java.-jar.target/colorado_rla`

Reset the `corla` database to be empty:

`dropdb corla; createdb -O corla corla`

and start up a fresh server:

```
cd server/eclipse-project
mvn package
java -jar target/colorado_rla-0.0.1-shaded.jar &
cd -
```

(Or whatever the latest version is - see `smoketest.bash` for a handy way to extract it).

Load the credentials for testing:

`psql -d corla -a -f ../corla-test-credentials.psql`

### Uploading CVRs and manifests

Run `./main.py` to upload the cvr and manifest files.

Run `psql -d corla -a -f tabulate.sql | diff tabulate.out -`
to check for differences in the tabulation.

Run `pytest server_test.py` to test some requests for information on the
cvr and manifest.

## Testing Audit Board procedures

First, prep for the audit [currently just setting up the CVRs].

Run ./util.py to make a random selection and create some aCVRs to audit it with.
You can then upload them one by one from the corla-server-test.html file.

TODO: use "refresh" to pull the actual seed and parameters, and
calculate audit parameters for a given contest.

## Updating tests
Prior to doing an update, perhaps first look at output of curl -I for each query below.
If there are failures, compare `test/corla-server_test.html` with the previous working commit.

Run the RLA tool server on port 8887, e.g. with an Eclipse Run
Configuration in which "Arguments" is set to
`src/main/resources/us/freeandfair/corla/proxiable.properties`.

In a different window, load a manifest and CVR with the --update option,
which runs them against port 8887:

`./main.py --update`

In a spare terminal, run this, where you can capture and watch the
queries which are being run:

```
zerotest server -p 8888 http://localhost:8887 -f server_test.json
```

Query the endpoints via this command:
```
bash -xv <<"EndOfInput" > curls.out 2>&1
 # opts="-I"

 curl $opts http://localhost:8888/ballot-manifest/county?3
 curl $opts http://localhost:8888/ballot-manifest

 curl $opts http://localhost:8888/contest
 curl $opts http://localhost:8888/contest/county?3
 curl $opts http://localhost:8888/contest/id/71
 curl $opts http://localhost:8888/cvr/county?3
 curl $opts http://localhost:8888/acvr/county?3
 curl $opts http://localhost:8888/acvr
EndOfInput
```

Stop the `zerotest server` process, e.g. with Cntl-C.

Interim test of captured queries:

`zerotest replay --ignore-all-headers server_test.json`

Generate raw test script which should work with existing database:

```
zerotest generate --ignore-all-headers server_test.json > server_test_raw.py 
sed 's,localhost:8887,localhost:8888,' server_test_raw.py > server_test_raw2.py
```

Stop and restart the server running on port 8888 as usual, and test it:

`pytest server_test_raw2.py`

Drop and rebuild the database in a new server to generate data in a possibly new
order:


`dropdb corla; createdb -O corla corla`

Load the credentials for testing:

`psql -d corla -a -f ../corla-test-credentials.psql`

Run `./main.py` to upload the cvr and manifest files.

Run `psql -d corla -a -f tabulate.sql | diff tabulate.out -`
to check for differences in the tabulation.

Run the new tests:

`pytest server_test_raw2.py > /tmp/server_test.out`

Fix any failing tests by e.g. changing

`matcher.match_responses(expect, real)`

to

`assert(abs(len(expect.body) - len(real.body)) < 10)`

and re-run until it works.  Save the working script in `server_test.py`

## Generating test data

To make an ACVR, download a CVR and modify it so that cvr['record_type']
is 'AUDITOR_ENTERED' instead of 'UPLOADED'.

Note that overvotes are allowed in ACVRs.

## Database schema changes
Errors reported from the scripts, such as

```
psql:../corla-test-credentials.psql:82: ERROR:  column "administrators_id" of relation "county_administrator" does not exist
LINE 1: insert into county_administrator (county_id, administrators_...
```

indicate that the schema has changed, and that script needs to be updated.

To view the current schema, run

`pg_dump -s corla > history/db-schema-$(git log --pretty=format:'%h' -n 1).sqlschema`

Old schemas for comparison can be found in the `history` subdirectory.


## Miscellaneous

The `server_test.json` file has the raw data generated by the
`zerotest server` command, describing each request and the response.
