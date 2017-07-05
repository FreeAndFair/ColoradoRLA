Developer Instructions
======================

History
-------

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

Dependencies
------------

Building
--------

Quality Assurance
-----------------

Validation and Verification
---------------------------

System Reliability
------------------
  
To ensure business continuity, we are applying techniques we have been
developing since the 1990s to create systems for clients requiring no
more than 0.001% downtime. These techniques include practical applied
formal methods (the application of mathematical techniques to the
design, development, and assurance of software systems) and a
peer-reviewed rigorous systems engineering methodology. Our
methodology was recently recommended by a NIST internal report (IR
8151 “Dramatically Reducing Software Vulnerabilities”), presented to
the White House Office of Science and Technology at their request in
November, 2016.


Build Status
------------
