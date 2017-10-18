Documentation for ``rla_export``
================================

The ``rla_export`` command exports ColoradoRLA data for publication
on the Audit Center web site.

Installation
------------

Installing required packages:

  ``pip install rla_export``

Information on the installed package:

  ``pydoc rla_export``

Examples
--------

With no options, the command will run queries using
all the standard .sql files provided in the package, and
put the resulting exported data in files in the current directory.

  ``rla_export``

The optional -p argument specifies connection information via
a database properties file, which should be the same file used
for the ``java jar`` command line. The output can also optionally be put
in a different output directory using the -e argument.

  ``rla_export [-p properties_file] [-e export_directory]``

Export a query in json and csv format for selected sql files:

  ``rla_export file.sql ...``

Full command line usage synopsis:

  ``rla_export -h``

Exported files
--------------

Some export files are the same as the files available via the GUI interface,
for example ``state_report.xlsx``.

Other export files are generated based on ``sql`` query files.
These are always produced in two formats: json and csv.
The basename of each resulting file is the same as the basename of the query file.
Thus, given the query file``seed.sql``, the files ``seed.json`` and ``seed.sql``
will be produced.

For each type of information, the export file or the query file are listed here.
When a county_id is used in an output file, it is indicated with the letter ``n``.

For now this is organized based on the requirements, identified by letters or numbers,
roughly as included in the email exchanges on public exports for the Audit Center.

a. Seed for randomization

  seed.sql

b. random ballot order

  ballot_list_n.csv

c. Number of ballots to be audited overall in each audited contest in each county

  ballots\_to\_audit\_per\_county.sql

d. List of Audit Rounds (number of ballots, status by
   County, download links). Links should be to all the finalized
   ballot-level interpretations and comparison details, in sufficient
   detail to independently verify the calculated risk levels. [as allowable
   by CORA]

  [partially implemented]

  discrepancies.sql

e. Status (audit required, audit in progress, audit
   complete, hand count required,, hand count complete) by audited contest
   (i.e., contest "selected for audit" by SoS

  [not yet implemented]

f. Final Audit Report

  state_report.xlsx

g. Audit Board names and political parties by County

  auditboards.sql

h. County Ballot Manifests and Hashes (status & download links)

  manifest\_hash.sql

  cvr\_hash.sql

1. The CVR file. This is only extracted if the ``-c True`` option is used.

  county_cvr_n.csv

2. The outcomes, vote counts and margins as calculated by the RLA
   Tool from the CVR file for contests chosen for audit

  tabulate.sql

11. The final audit report by county

  county_report_n.xlsx

12. The ballot manifest file

  county_manifest_n.csv

03. For each audited contest and each audited ballot card, the Audit
    Board interpretation (including “no consensus” designations or
    ballots not found).

  acvrs.sql
