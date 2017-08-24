\pagebreak

Installation and Use
====================

Downloading
-----------

This system is open source and freely available via GitHub. One can
download the entire project, including all development artifacts and
source code, via the
\href{https://github.com/FreeAndFair/ColoradoRLA}{GitHub Colorado RLA
project} webpage, either by cloning its git repository or as an
archive file.

We also automatically build a distribution archive using the Node
package manager (for the front-end) and an Maven-based Java archive
file compilation (for the back-end). See
the [client/README.md](client README file) for more information about
the former, particularly the `npm run pack` command. See the
[server/README-ECLIPSE.md] file for more information about the latter.

Installation
------------

This system is installed at the moment by installing a Postgres
database and running the server in a Java 8 Virtual Machine (VM).

Database installation and setup is documented in the *Data
Persistence* section of the *Developer Instructions* chapter of this
book.

Example of Use
--------------

See the [User Manual](user_manual.docx) and [Run Book](runbook.docx).

Running the latest development version
--------------------------------------

For testing purposes, you can run the latest versions of the client
and server directly from a clone of
the [git repository](https://github.com/FreeAndFair/ColoradoRLA).

For the server, follow the directions
in [`server/README-ECLIPSE`](../server/README-ECLIPSE.md) to install
Eclipse, and use Eclipse's Run menu.

Building and packaging the server is accomplished by running `mvn
package` from the `../server/eclipse-project` directory, as in
```
cd server/eclipse-project
mvn package
```

Running the system can be accomplished by running the application
directly (as in `java -jar
../server/eclipse-project/target/colorado_rla-0.9.1-shaded.jar`) or
installing the system in a webserver with a Java container.

To test the client in a standalone manner (rather than from the
server), you'll need to
get [Node.js and npm](https://www.npmjs.com/get-npm). Next, run:
```
cd client
npm install
npm start
```

Then visit [`http://localhost:3000/`](http://localhost:3000/) in a
supported web browser.
