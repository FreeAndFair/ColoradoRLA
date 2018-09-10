# Release process

There are a couple of ways you might build a release, but before you do anything,
have you updated [The CHANGELOG?](CHANGELOG.md)

One way to do it would be to run
`server/eclipse-project/script/build-release`, but there's a path that
might not exist on your host machine, VM, etc.

## How We Do It

- Create a release directory (hereafter `$RELEASE_DIR`) somewhere on
  your filesystem. This should be named something like
  `colorado-rla-release-x.y.z`, where `x.y.z` is your version number.
- Make a commit bumping the server (`server/eclipse-project/pom.xml`)
  and client (`client/package.json`) version to the target release
  version. Use `vx.y.x` as the message format for this commit.
- Create a signed tag at this commit: `git tag -s -m 'vx.y.z' vx.y.z
  $COMMIT` and then `git push --tags`.
- In `server/eclipse-project`, use `mvn clean package` and copy the `-shaded` JAR into
  `$RELEASE_DIR` as `corla-server-x.y.z.jar`.
  - `cp $NAME-shaded.jar $RELEASE_DIR/corla-server-x.y.z.jar`
- In `client`, ensure the `dist` directory is removed and package that up
  - `rm -rf dist && npm run dist`
- `zip` up the client and add that release artifact to `$RELEASE_DIR`:
  - `zip -r $RELEASE_DIR/corla-client-x.y.z.zip client/dist/*`
- Copy `server/deploy/corla.conf` to `$RELEASE_DIR`.
- Copy `test/corla-test-credentials.sql` to `$RELEASE_DIR`.
- Create a full release artifact (note, it is important to get the directory
  name in there to avoid bombing somebody who is about to unpack the ZIP):
  - `zip -r colorado-rla-release-x.y.z.zip $RELEASE_DIR`

For a more concrete example, see [an example PR](https://github.com/democracyworks/ColoradoRLA/pull/19).

## Create a GitHub Release

Once you've got an artifact built, you need to [build a GitHub
Release][corla-releases].

- Find the checksum of your artifact: `shasum -a256 colorado-rla-release-x.y.z.zip`
- Create a new GitHub release; here's a basic template you can use:

  ```
  # Version x.y.z

  This version represents the work done for sprint ending January 19, 2038 (2038-01-19).

  See the [vx.y.z changelog](https://github.com/democracyworks/ColoradoRLA/blob/master/CHANGELOG.md#xyz--year2038) for more details about changes in this release.

  | SHA-256 hash | File |
  | ------------------ | ----- |
  | some-sha-256-value | colorado-rla-release-x.y.z.zip |
  ```

[corla-releases]: https://github.com/democracyworks/ColoradoRLA/releases
