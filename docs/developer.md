Developer Instructions
======================

*TDB abstract and introduction to this document.*

History
-------

* Outline and first draft, 5 July 2017 by Joe Kiniry.

Technologies in Use
-------------------

To fulfill Colorado’s requirements, we will use a modular system
design and standard, well-understood web application technologies. We
are using a Java-based web application running on a Linux server,
hosted in the CDOS data center. We will be deploying on a JVM version
that supports Java 8. We are using CDOS's standard SQL relational
database management system for data persistence. As the user interface
(UI) will be browser-based, we are writing the client in TypeScript, a
mainstream, Microsoft-supported variant of JavaScript that offers
opt-in, Java-like type safety. TypeScript compiles to plain,
human-readable JavaScript, so this choice will support client-side
correctness without requiring any special web browser support.

Our current plan is to use the following tools: 

* GitHub for distributed version control, change tracking, and
  development documentation
* PVS for specifying formal domain models and automatically
  synthesizing system tests
* SAW for formal verification of intermediate representations and
  reasoning
* lightweight static checkers of various kinds (e.g., CheckStyle, PMD,
  and FindBugs for the JVM platform) for ensuring that designs and
  code are appropriately formatted, well-designed, maintainable, etc.
* OpenJML and Eclipse with various plugins, for performing runtime
  verification, extended static checking, and full functional
  verification of implementations against specifications
* (optionally) Coq for formally specifying and reasoning about various
  formal models of the system and elections in general
* (optionally) Cryptol for specifying and reasoning about
  cryptographic algorithms
* (optionally) F* and Tamarin for specifying and reasoning about
  cryptographic protocols
* gcc, clang and (optionally) CompCert for compiling C code
* various automated theorem provers such as Z3, Lean, CVC4, Yices, and
  ABC for automatically reasoning about formal models
* Emacs, Eclipse, IntelliJ, and other JetBrains technologies for
  Integrated Development Environments,
* (optionally) CZT, Event-B, Overture, and RAISE for specifying formal
  models
* the OpenJDK Java compiler 
* JMLUnitNG for automatic test code generation
* standard test coverage tools such as JCov and Cobertura 
* (optionally) Java Pathfinder and similar model checkers for
  reasoning about safety properties of implementations
* OmniGraffle for drawing system diagrams
* the BON and GSSL tool suites for system specification
* the Beetlz checker for refinement checking of BON specifications
  against JML/Java implementations
* (optionally) ProVerif, UPPAAL, and the TLA+ tools for distributed
  algorithm specification and reasoning
* the TypeScript tool set for ... @todo ranweiller
* and Travis for continuous integration.

*TDB addition of hyperlinks to the above and a discussion of
bootstrapping developers through the use of snapshot builds of IDEs,
kickstarter workspaces, and VM images.*

Dependencies
------------

*TBD a concrete list of dependencies, preferably at the module/library
level, complete with versioning information.*

Building
--------

*TBD discussion of which build systems we use and why.*

Quality Assurance
-----------------

*TBD discussion of the various facets of quality and how these ideas
are concretized into metrics and measures that are automatically
assessed and reported upon, both within the IDE and during continuous
V&V.*

Validation and Verification
---------------------------

*TBD high level discussion here about how the goals and technologies
discussed in the V&V document are realized.*

Deployment
----------

Free & Fair develops open source software systems in full public
view. Therefore, all artifacts associated with a given project or
product are immediately available to all stakeholders, at any time,
via a web-based collaborative development environment such as
GitHub. This means that various versions of the same system (e.g.,
builds for various platforms, experimental branches in which new
features are being explored, etc.) are immediately available to anyone
who browses the project website and clicks on the right download link,
or clones the repository and builds it for themselves.

Delivery of production systems to a client or stakeholder is
accomplished by providing the modern equivalent of "golden master
disks" of yesteryear. The nature of these deliveries differs according
to decisions made during contracting and development, in tandem with
the client.

For example, if the deployment platform is a flavor of Linux, one of
the standard software packaging systems such as RPM or dpkg is used to
deliver products. If the deployment system is Microsoft Windows or
macOS, the standard open source packaging software is used to deploy
production systems.

We believe that the deployment of a certified version of a product to
hundreds or thousands of devices requires a different mechanism than
the traditional "hire dozens of interns to do everything by hand."
While the details of such a deployment depend significantly on
industrial design decisions, we are inclined to use a public
large-scale parallel export of (cryptographically forensically checked
and signed) product masters onto COTS SD cards for manual insertion in
all systems. Such a deployment mechanism would use inexpensive media
that is long-lasting, easy and cheap to archive, and would be designed
to address our deep concerns with regards to the deployment and
auditing of certified product versions. Counties could then archive
all of the digital materials relevant to an entire election in one
small lockbox or safe deposit box.

System Reliability
------------------
  
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


Build Status
------------

*TBD discussion of what build status means, where build failure
notifications go, where build logs are archived, and a link to our
live dashboard.*
