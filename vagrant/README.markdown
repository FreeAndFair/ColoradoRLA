# Colorado RLA Vagrant instructions

## Requirements

- [rsync][rsync-home] (ships with macOS by default)
- [Vagrant][vagrant-home]

## Installation

References to `${PROJECT_ROOT}` in these instructions are talking about the root
of the checked out RLA code.

If you need Vagrant, a quick way on macOS with `brew`:

```sh
brew cask install virtualbox
brew cask install vagrant
```

You **must** be in the `${PROJECT_ROOT}/vagrant` directory for these
instructions to work.

1. Bring up the VM and provision it:
   ```sh
   vagrant up
   ```
   This will take 2-5 minutes to run the first time, especially if you need to
   download the base image for the CentOS/RHEL VM.

   Do not be alarmed if you see some commands in red toward the end of the
   provisioning step. Unless you see output from `vagrant` itself (typically
   prefixed with `==>`-style fat arrows), everything worked well.
2. The provisioning process does not start the RLA client or server, assuming
   they may not have been built yet. Build them this way if you like:
   ```sh
   # Client (assumes ${PROJECT_ROOT} is the root of the RLA software)
   cd ${PROJECT_ROOT}/client
   npm run dist

   # Server
   cd ${PROJECT_ROOT}/server/eclipse-project
   mvn package
   ```
3. You should run `vagrant rsync` here to get the new artifacts synced to the
   VM. As described in [Usage](#usage), however, you can run
   `vagrant rsync-auto` in another terminal to get the same effect without
   manually running a command.
4. Start the RLA server, which will populate the database schema. For the
   following step, you will want a **separate terminal window**, as you likely
   want the server running in the foreground in order to see live log output.
   ```sh
   vagrant ssh
   ## On the server (you may need to adjust the version number here):
   java -jar /vagrant/server/eclipse-project/target/colorado_rla-1.1.0-shaded.jar
   ```
5. Back in your main terminal, populate the RLA tool with test users:
   ```sh
   vagrant ssh -c \
     'psql -U corla -d corla -h localhost \
        -f /vagrant/test/corla-test-credentials.psql'
   ```
   You should see something like:
   ```
   INSERT 0 8
   ...
   Connection to 127.0.0.1 closed.
   ```
6. All done! Visit `localhost:8080` to see the running and seeded system. You
   may need to refresh (C-r or C-Shift-R) in your browser in order to get
   something to come up.

## Usage

### Synchronizing local changes with the VM

If you are actively developing against this code, you will want to run

```
vagrant rsync-auto
```

in some terminal to get automatic synchronization of files that you change
locally with those in the VM.

### Working with the Java server

You can `vagrant ssh` from another terminal and leave the Java server running
there in the foreground, as described in [Installation](#installation). You will
have to restart it (after a `mvn package`) if you make changes and want to see
them reflected in the VM.

[rsync-home]: https://rsync.samba.org/
[vagrant-home]: https://www.vagrantup.com/
