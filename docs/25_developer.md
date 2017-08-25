\pagebreak

Developer Instructions
======================

This document is for developers interested in contributing to this
project. It describes the technologies used, the modules and
libraries on which we depend, and how to build the system. It also
covers how to perform quality assurance, validation, verification, and
deployment of the system.

Determining if the system is correct includes both checking behavioral
properties and non-behavioral properties. Behavioral properties are
all about correctness and boil down to decidable (*yes or no*)
questions about the system.  Non-behavioral properties are
measurable---such as how many resources the system uses, how reliable
it is, or whether it is secure---and checking them entails ensuring
that measures are as we specify. More specifically, we cover system
performance and reliability.

Finally, the document closes with a link to our project dashboard.

History
-------

* Outline and first draft, 5 July 2017 by Joe Kiniry.
* Second draft that is mostly textually complete, 6 July 2017 by Joe
  Kiniry.
* Third draft with updates for phase-1 delivery, 17 August 2017 by Joe
  Kiniry.
* Fourth draft with updates for phase-2 delivery, 24 August 2017 by
  Joe Kiniry.

Platform and Programming Languages
----------------------------------

To fulfill Colorado’s requirements, we use a modular system
design and standard, well-understood web application technologies.

We are using a Java-based web application running on a Linux server,
hosted in the CDOS data center. Deployment is targeted for a JVM version
that supports Java 8.

The choice of deployment on JVM is made due to IT constraints on the
part of CDOS. (See page 11, "Hosting Environment", of the DQ.)  While
we could have developed on .Net, and there are very good tools for
rigorous engineering on such (which we have used in, e.g., our
electronic poll book demonstrator), there are still significant
challenges in multi-platform development and deployment.  We would
rather have a straightforward cross-platform system development and
deployment story, thus we use Java 8 on the server-side.

We are using [PostgreSQL](https://www.postgresql.org/), via
the [Hibernate ORM](http://hibernate.org/orm/), for data persistence.

The user interface (UI) is browser-based. The client is written
in [TypeScript](https://www.typescriptlang.org/), a mainstream,
Microsoft-supported variant of JavaScript that offers opt-in,
Java-like type safety. TypeScript compiles to plain, human-readable
JavaScript, so this choice supports client-side correctness without
requiring any special web browser support.

Developer Tools
---------------

We are using many of the following tools. Developers need not download
and install this long list of technologies. We have provided developers
with a pre-configured [Eclipse](https://eclipse.org/) install,
a bootstrapping Eclipse workspace,
and a VM image which can be loaded into VirtualBox or other comparable
virtualization platform. We provide these resources in order to both
decrease new developers' ramp-up time as well as to standardize on
specific versions of tools for development.

Instructions for installing Eclipse and automatically installing and
configuring the plugins we use are found
in [README-ECLIPSE.md](../server/README-ECLIPSE.md).

* [GitHub](https://github.com/FreeAndFair/ColoradoRLA) for distributed
  version control, issue tracking, and development documentation
* the
  [PVS specification and verification system](http://pvs.csl.sri.com/)
  or the [Alloy tool](http://alloy.mit.edu/alloy/) for specifying and
  reasoning about formal domain models and automatically synthesizing
  system tests
* the [Software Analysis Workbench (SAW)](https://saw.galois.com/) for
  formal verification of intermediate representations and reasoning
* the [CheckStyle](http://checkstyle.sourceforge.net/) lightweight
  static checkers for ensuring code standard conformance,
* the [FindBugs](http://findbugs.sourceforge.net/) lightweight static
  checker for code quality evaluation,
* the [PMD](https://pmd.github.io/) lightweight static checker for
  code quality evaluation,
* the [Java Modeling Language (JML)](http://jmlspecs.org/) for
  formally specifying the behavior of our Java implementation
* the [OpenJML tools suite](http://www.openjml.org/) for performing
  runtime verification, extended static checking, and full functional
  verification of Java implementations against JML specifications
* the [JMLunitNG](http://insttech.secretninjaformalmethods.org/software/jmlunitng/)
  tool for automatically generating test benches from JML-annotated
  Java code
* the [Coq proof assistant](https://coq.inria.fr/) for formally
  specifying and reasoning about various formal models of the system
  and elections in general
* (optionally) the [KeY tool](https://www.key-project.org/) for
  performing full functional verification and test case generation of
  implementations with JML formal specifications
* (optionally) the [Cryptol tool](https://cryptol.net/) for specifying
  and reasoning about cryptographic algorithms
* (optionally) the [F* tool](https://www.fstar-lang.org/)
  and [Tamarin prover](https://tamarin-prover.github.io/) for
  specifying and reasoning about cryptographic protocols
* the [Gnu Compiler Collection (gcc)](https://gcc.gnu.org/), the [clang
  compiler](https://clang.llvm.org/), and (optionally) the [CompCert
  compiler](http://compcert.inria.fr/) for compiling C code
* various automated theorem provers such
  as [Z3](https://github.com/Z3Prover/z3),
  the
  [Lean theorem prover](https://leanprover.github.io/),
  [CVC4](http://cvc4.cs.stanford.edu/web/),
  [Yices](http://yices.csl.sri.com/),
  and [ABC](https://people.eecs.berkeley.edu/~alanmi/abc/abc.htm) for
  automatically reasoning about formal
  models *
  [Emacs](https://www.gnu.org/software/emacs/),
  [Eclipse](https://eclipse.org/),
  [IntelliJ IDEA](https://www.jetbrains.com/idea/),
  and
  [other JetBrains technologies](https://www.jetbrains.com/products.html?fromMenu) for
  Integrated Development Environments,
* (optionally)
  the [Community Z Tools (CZT)](http://czt.sourceforge.net/)
  supporting the Z formal method,
  the [Rodin platform](http://www.event-b.org/install.html) supporting
  the [Event-B formal method](http://www.event-b.org/),
  the [Overture tool](http://overturetool.org/) supporting
  the [VDM formal method](http://overturetool.org/method/), and
  the [RAISE tool](http://spd-web.terma.com/Projects/RAISE/)
  supporting the RAISE specification language (RSL) for specifying and
  reasoning about formal models of systems
* the [OpenJDK](http://openjdk.java.net/) Java developers kit
* [JMLUnitNG](http://insttech.secretninjaformalmethods.org/software/jmlunitng/) for
  automatic test code generation
* standard test coverage tools such
  as [JCov](https://yp-engineering.github.io/jcov/)
  and [Cobertura](http://cobertura.github.io/cobertura/)
* (optionally)
  [Java PathFinder (JPF)](http://javapathfinder.sourceforge.net/) and
  similar model checkers for reasoning about safety properties of
  implementations
* [OmniGraffle](https://www.omnigroup.com/omnigraffle) for drawing diagrams
* various [BON-related tools](https://github.com/FreeAndFair/BON),
  including
  the [BONc](http://kindsoftware.com/products/opensource/BONc/)
  and
  [FAFESSL](https://github.com/FreeAndFair/BON/tree/master/FAFESSL/BON) tool
  suites, which are based upon
  the [BON method](http://www.bon-method.com/), for system
  specification
* the [Beetlz tool](https://github.com/FreeAndFair/Beetlz/) for
  refinement checking of BON specifications against JML-annotated Java
  implementations
* (optionally)
  [ProVerif](http://prosecco.gforge.inria.fr/personal/bblanche/proverif/),
  [UPPAAL](http://www.uppaal.org/), and
  the [TLA+ tools](http://lamport.azurewebsites.net/tla/tools.html)
  for distributed algorithm specification and reasoning
* the [TypeScript](https://www.typescriptlang.org/) language for front-end development, using
  the [React](https://facebook.github.io/react/) UI framework
* *TBD Daikon*
* *TBD AutoGrader*
* and [Travis CI](https://travis-ci.org/) for continuous integration

Dependencies
------------

*TBD: A concrete list of dependencies, preferably at the module/library
level, complete with versioning information. Note that we prefer that
this dependency list is automatically generated and kept up-to-date by
the build system.*

### Spark

*TBD: Describe [Spark](http://sparkjava.com/) and its use.*

### Data Persistence

In order to use the Postgres database in development, one must:

1. Install PostgreSQL (`brew install postgres` on MacOS, `apt-get
   install postgresql` on many Linux distributions, or whatever is
   appropriate) and start it running.
2. Create a database called "`corla`", and grant all privileges on it
   to a user called "`corla`" with password "`corla`".
3. Initialize the "`corla`" database with test administrator data.
   For example, to accomplish the above on MacOS using Homebrew, one
   issues the following commands:
```
brew install postgres
createuser -P corla
createdb -O corla corla
```
On Linux, one would replace the first command with something akin to
`sudo apt-get install postgresql`.

That's it. If the database is there the server will use it and will,
at this stage, create all its tables and such automatically.
4. Run the server (to create all the database tables). Recall that
   this is accomplished by either running the server in Eclipse using
   the Run button or running it from a command line using a command
   akin to `java -jar colorado_rla-VERSION-shaded.jar`.
5. Load test authentication credentials into the database, by
   executing the SQL in `corla-test-credentials.psql` (found in the
   `test` directory of the repository). This can be done with the
   following command on OS X:
```
psql -U corla -d corla -a -f corla-test-credentials.psql
```
   or the following command on Linux:
```
psql -U corla -h localhost -d corla -a -f corla-test-credentials.psql
```

If you need to delete the database---perhaps because due to a recent
merge the DB schema has evolved---use the `dropdb corla` command and
then recreate the DB following the steps above.

There are helpful scripts for automating these actions located in the
`server/eclipse_project/script` directory.

*TBD: Describe the [Hibernate ORM](http://hibernate.org/orm/) and its
use.*

### Communication Formats

*TBD: Describe
the [Google GSon library and tools](https://github.com/google/gson).*

Code Review and Source Management
---------------------------------

We use the Git SCM for version control, with GitHub for hosting and
code review. The development worfklow is as follows:

1. Locally, pull the latest changes on the `master` branch of the
   upstream repo, hosted on GitHub.
1. Check out a new topic branch based on the above changes.
1. Commit changes to your local branch.
1. For the sake of visibility, you may open a work-in-progress Pull
   Request from your branch to `master`. If you do, add the `wip`
   label.
1. When you are ready to merge to `master`, make sure your branch has
   been pushed to the remote repository and open a Pull Request (if
   you haven't already). Remove any `wip` label and add the `review`
   label.
1. If appropriate, use the GitHub "Reviewers" dropdown to formally
   request a review from a specific person. Either way, paste a link
   to the PR in Slack to alert others who may wish to review it.
1. At least one other person must review any changes to the `master`
   branch and approve it via the GitHub PR interface comments. A
   _reviewer_ should check that all new commits are signed, and all
   necessary comments are addressed.
1. Before it can be merged, you will generally have to `rebase` your
   branch on to the `master` branch in order to preserve a clean commit
   history. You can do this with commands in your branch: `git fetch`,
   then `git rebase origin/master` (addressing any merge conflicts if
   necessary), and finally
   `git push --force-with-lease origin <yourbranch>`.
1. Note that *force-pushes can be dangerous*, so make sure that you know
   that no one else has pushed changes to the branch which aren't in the
   history of your branch.  If others on the team are pulling and
   testing it locally, they will need fix up their local branches with
   `git checkout <yourbranch>`, `git fetch`, and
   `git reset --hard origin/<yourbranch>`.
   For more details, see
   [The Dark Side of the Force Push - Will Anderson](http://willi.am/blog/2014/08/12/the-dark-side-of-the-force-push/)
   and [--force considered harmful; understanding git's --force-with-lease - Atlassian Developers](https://developer.atlassian.com/blog/2015/04/force-with-lease/)
1. Finally, a _reviewer_ with merge permissions can merge the PR using the
   GitHub "Merge pull request" button.
   This will introduce an _unsigned_ merge commit, but
   preserve the signatures on the actual branch's commits. Finally,
   the PR submitter, not the reviewer, should delete the merged
   branch.

**Guidelines:**

- Do not commit directly to `master`.
- To support bisecting, do not merge WIP commits that break the build.
  On topic branches, squash commits as needed before merging.
- Write short, useful commit messages with a consistent style. Follow
  these
  [seven rules](https://chris.beams.io/posts/git-commit/#seven-rules),
  with the amendment that on this project, we have adopted the
  convention of ending the subject line with a period.
- Keep your topic branches small to facilitate review.
- Before merging someone else's PR, make sure other reviewers'
  comments are resolved, and that the PR author considers the PR ready
  to merge.
- For security-sensitive code, ensure your changes have received an
  in-depth review, preferably from multiple reviewers.
- Configure Git so that your commits are
  [signed](https://git-scm.com/book/en/v2/Git-Tools-Signing-Your-Work).
- Whenever possible, use automation to avoid committing errors or
  noise (e.g. extraneous whitespace). Use linters, automatic code
  formatters, test runners, and other static analysis tools. Configure
  your editor to use them, and when feasible, integrate them into the
  upstream continuous integration checks.
- WARNING: sub-projects (e.g. the client, server) should _not_ directly
  depend on files outside of their directory tree. Our CI is configured
  to run checks only for projects that have had some file changed. If
  you must depend on out-of-tree files, update [`.travis.yml`](../.travis.yml)
  to avoid false positives.

Building
--------

*TBD discussion of which build systems we use and why.*

We provide both an integrated Eclipse-based build system and a
traditional Make-based build system. The former permits us to support
a rich and interactive design, development, validation, and
verification experience in an IDE. The latter facilitates
cross-platform, IDE-independent builds and continuous integration with
Travis CI.

The Eclipse-based build system is built into our Eclipse IDE image and
our workspace, as specified in `server/eclipse.setup`.

The Make-based system is rooted in our
top-level [Makefile](../Makefile). That build system not only compiles
the RLA tool, but also generates documentation, analyzes the system
for quality and correctness, and more. *(Ed. note: The make-based
build system has not yet been written.)*

See the instructions in the **Installation and Use** chapter on
running the development system.

Note that the production client build configuration expects server
endpoints to have an `/api` path prefix. To support user testing, we
currently enable browser console logging in all builds.

Quality Assurance
-----------------

*TBD discussion of the various facets of quality and how these ideas
are concretized into metrics and measures that are automatically
assessed and reported upon, both within the IDE and during continuous
V&V.*

We measure quality of systems by using a variety of *dynamic* and *static*
techniques.

Dynamic analysis means that we run the system and observe it,
measuring various properties of the system and checking to see if
those measures are in the range that we expect. We say "range" because
many measures have a sweet spot---a good value is not too high and not
too low. Running the system means that we either execute the system in
a normal environment (e.g., a Java virtual machine) or we execute a
model of the system in a test environment (e.g., an instrumented
executable or a debugger).

Static analysis entails examining a system *without* executing it.
Static analysis that only examines a system's *syntactic* structure is
what we call lightweight static analysis. For example, the source
code's style and shape is syntactic.  Static analysis that examines a
system's *semantic* structure is what we call heavyweight static
analysis. For example, theorem proving with extended static checking
is heavyweight static analysis.

Each kind of static analysis results in a *measure* of a
*property*. Decidable properties are either *true* or *false*, thus a
good measure for a property is simply "yes" or "no". Other static
analyses have more interesting measures, such as grades ("A" through
"F") or a number.

In order to automatically measure the quality of a system, we define
the set of properties that we wish to measure and the what the optimal
ranges are for the measure of each property. We automate this
evaluation, both in the IDE and in continuous integration.
The current status of our continuous integration checks is displayed
via a dynamic status image on our [repository's home page](../README.md).

Also, we have a tool called the AutoGrader that automatically combines
the output of multiple analyses and "grades" the system, and
consequently its developers. By consistently seeing automated feedback
from a set of tuned static analysis tools, developers quickly learn
the development practices of a team and a project and also often learn
more about rigorous software engineering in general.

Validation and Verification
---------------------------

*TBD high level discussion here about how the goals and technologies
discussed in the V&V document are realized.*

Determining whether the system you are creating is the system that a
client wants is called *validation*.  *Testing* is one means by which
to perform validation.  Mathematically proving that a system
performs exactly as specified under an explicitly stated set of
assumptions is called *verification*.

Some of the quality assurance tools and techniques discussed above are
a part of validation and verification.

The [Validation and Verification](40_v_and_v.md) document focuses on
this topic in great detail.

Deployment
----------

Free & Fair develops open source software systems in full public
view. Therefore, all artifacts associated with a given project or
product are immediately available to all stakeholders, at any time,
via a web-based collaborative development environment, such as
our [GitHub organization](https://github.com/FreeAndFair). This means
that various versions of the same system (e.g., builds for various
platforms, experimental branches in which new features are being
explored, etc.) are immediately available to anyone who browses the
project website and clicks on the right download link, or clones the
repository and builds it for themselves.

Delivery of production systems to a client or stakeholder is
accomplished by providing the modern equivalent of "golden master
disks" of yesteryear. The nature of these deliveries differs according
to decisions made during contracting and development, in tandem with
the client.

For example, if the deployment platform is a flavor of Linux, one of
the standard software packaging systems such as RPM or dpkg is used to
deliver products. If the deployment system is Microsoft Windows or Apple
OS X, the standard open source packaging software is used to deploy
production systems.

System Performance
------------------

*TBD discussion of automated performance testing*

System Reliability
------------------

*TBD discussion of automated deployment reliability testing*

To ensure business continuity, we are applying techniques we have been
developing since the 1990s to create systems for clients requiring no
more than 0.001% downtime. These techniques include practical applied
formal methods (the application of mathematical techniques to the
design, development, and assurance of software systems) and a
peer-reviewed rigorous systems engineering methodology. Our
methodology was recently recommended by a NIST internal report (IR
8151 "Dramatically Reducing Software Vulnerabilities"), presented to
the White House Office of Science and Technology at their request in
November, 2016.

Project Dashboard
-----------------

We use [Travis CI](https://travis-ci.org/) for continuous integration.
It produces new builds for every check-in and runs our test suites, as
specified in our [`.travis.yml`](../.travis.yml) file and in the
[`ci`](../ci) directory.

Current build status for the `master` branch status is reflected in the
`README.md` of our main GitHub page. Build status logs, can be found at
our dashboard:
[FreeAndFair/ColoradoRLA](https://travis-ci.org/FreeAndFair/ColoradoRLA).

The raw build logs provide details on which versions of our build tools
and libraries were used each step along the way.


Bibliography
------------

*TBD add references to appropriate papers*
