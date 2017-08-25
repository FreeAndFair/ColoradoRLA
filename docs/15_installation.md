\pagebreak

Installation and Use
====================

Downloading
-----------

The Colorado Risk-Limiting Audit (CORLA, for short) system is open
source and freely available via [GitHub](http://github.com). One can
download the entire project, including all development artifacts and
source code, via
the
[GitHub Colorado RLA project](https://github.com/FreeAndFair/ColoradoRLA) webpage,
either by cloning its git repository or as an archive file.

We also automatically build a distribution archive using the Node
package manager (for the front-end) and an Maven-based Java archive
file compilation (for the back-end). See
the [client/README.md](client README file) for more information about
the former, particularly the `npm run pack` command. See the
[server/README-ECLIPSE.md] file for more information about the latter.

Installation
------------

This system is installed at the moment by installing a Postgres
database and running the server in a Java 8 Virtual Machine (JVM).

Database installation and setup, which is a one-time operation, is
documented in the *Data Persistence* section of the *Developer
Instructions* chapter of this book.

Example of Use
--------------

See the [User Manual](user_manual.docx) and [Run Book](runbook.docx).

Running the latest development version
--------------------------------------

For testing purposes, you can run the latest versions of the client
and server directly from a clone of
the [git repository](https://github.com/FreeAndFair/ColoradoRLA). One
must configure the database as discussed previously and run both the
client and the server to test the CORLA system.

For the server, follow the directions
in [`server/README-ECLIPSE`](../server/README-ECLIPSE.md) to install
Eclipse, and use Eclipse's Run menu.

Building and packaging the server is accomplished by running `mvn
package` from the `../server/eclipse-project` directory, as in
```
cd server/eclipse-project
mvn package
```

Running the server can also be accomplished by running the application
directly (as in `java -jar
../server/eclipse-project/target/colorado_rla-VERSION-shaded.jar`, for
some specific *VERSION* number) or installing the system in a
webserver, as documented in the `INSTALL.html` documentation delivered
with the system to CDOS.

To run the client in a standalone manner (rather than from a deployed
server), you will need to
get [Node.js and npm](https://www.npmjs.com/get-npm). Next, run: 
```
cd client
npm install
npm start
```

Then visit [`http://localhost:3000/`](http://localhost:3000/) in a
supported web browser and you will be able to authenticate to the
CORLA system.
