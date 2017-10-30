Documentation for ``rla_export``
================================

The ``rla_export`` command exports ColoradoRLA data for publication
on the Audit Center web site.

Installation
------------

If some version of Python's package manager pip is already installed, skip to the package installation pip command. If not, you will have to install pip. The pip installation depends on the operating system and version.

For RHEL 7, as a one-time step on each RLA Tool server, run these commands to install
"pip" command for installing packages and the necessary dependencies.

::

    yum install epel-release
    curl https://dl.fedoraproject.org/pub/epel/epel-release-latest-7.noarch.rpm > epel-release-latest-7.noarch.rpm
    yum install ./epel-release-latest-*.noarch.rpm
    yum install python-pip
    sudo pip install --upgrade pip


Once pip is installed, you can use it to install rla_export and all the packages it depends on, by
* running a command like this on the file containing the rla_export package (the ``*.tar.gz`` file provided).
  Note that the version number may be different from ``1.0.4.dev2`` and you'll need to specify a
  properly qualified path name if it isn't in the current directory.

  ``pip install rla_export-1.0.4.dev2.tar.gz``

Running the Export Command
--------------------------

With no options, the command will run queries using
all the standard ``.sql`` files provided in the package, and
put the resulting exported data in files in the current directory.

It will also download any reports that are available, and a csv-format
list of ballot cards for auditing for all rounds defined to date.

  ``rla_export``

If you are using a customized database properties file on the ``java jar``
command line, you should provide that same file with the ``-p`` option,
so that the program knows the proper connection and login information for the database:

  ``rla_export -p properties_file``

The output can also optionally be put in a different output directory
using a ``-e export_directory`` argument.

If you wish to export the results of a custom database query in json and csv format, put the query into ``file.sql`` and add the file name as an argument:

  ``rla_export file.sql``

The export from this command will have just a json and a csv file for the single SQL command in ``file.sql``.

Full command line usage synopsis:

  ``rla_export -h``

Exported files
--------------

Some export files are the same as the files available via the GUI interface,
for example ``state_report.xlsx``.

Other export files are generated based on ``sql`` query files.
These are always produced in two formats: json and csv.
The basename of each resulting file is the same as the basename of the query file.
Thus, given the query file ``seed.sql``, the files ``seed.json`` and ``seed.csv``
will be produced.

For now this is organized based on the requirements, identified by letters or numbers,
roughly as included in the email exchanges on public exports for the Audit Center.

For each requirement, the export file or the query file are listed here.
When a county_id is used in an output file, it is indicated with the letter ``n``.

a. Seed for randomization

  seed.sql
 * - Column Name
   - Type
   - Interpretation
 * - seed
   - 20-digit string
   - the random seed

b. random ballot order

  ballot_list_n.csv

c. Number of ballots to be audited overall in each audited contest in each county

  ballots_to_audit_per_county.sql

d. List of Audit Rounds (number of ballots, status by
   County, download links). Links should be to all the finalized
   ballot-level interpretations and comparison details, in sufficient
   detail to independently verify the calculated risk levels. [as allowable
   by CORA]

  [partially implemented]

  discrepancies.sql
 * - Column Name
   - Type
   - Interpretation
 * - name
   - String
   - name of contest
 * - id
   - Integer
   - internal database id for the contest
 * - winners_allowed
   - Integer
   - maximum number of winners of the given contest
 * - one_vote_over_count
   - Integer
   - number of one-vote overstatements found so far by the audit board for the given contest
 * - one_vote_under_count
   - Integer
   - 
 * - two_vote_over_count
   - 
   - 
 * - two_vote_under_count
   - Integer
   - 
 * - audit_reason
   - 
   - 
 * - audit_status
   - 
   - 
 * - audited_sample_count
   - 
   - 
 * - disagreement_count
   - Integer
   - 
 * - estimated_samples_to_audit
   - Integer
   - 
 * - estimated_recalculate_needed
   - True/False
   - 
 * - gamma
   - Numeric
   - 
 * - optimistic_recalculate_needed
   - True/False
   - 
 * - optimistic_samples_to_audit
   - Integer
   - 
 * - risk_limit
   - Numeric
   - Risk limit chosen by the Secretary of State for the given contest
 * - county_name
   - String
   - name of county
 * - 
   - 
   - 
 * - 
   - 
   - 
 * - 
   - 
   - 
 * - 
   - 
   - 
 * - 
   - 
   - 
 * - 
   - 
   - 



  prefix_length.sql
 * - Column Name
   - Type
   - Interpretation
 * - county_name
   - String
   - name of county
 * - audited_prefix_length
   - Integer
   - length of the prefix that will be complete at the end of the round 
	in the given county. Note that the "prefix" is part of the random 
	sequence of ballot cards, which may contain duplicates. For example,
	if a single ballot card appears twice in the random sequence generated 
	by the pseudorandom number generator up through the current round, 
	that ballot card would count twice toward the prefix length.


e. Status (audit required, audit in progress, audit
   complete, hand count required,, hand count complete) by audited contest
   (i.e., contest "selected for audit" by SoS

  [not yet implemented]

f. Final Audit Report

  state_report.xlsx

g. Audit Board names and political parties by County

  auditboards.sql

h. County Ballot Manifests and Hashes (status & download links)

  manifest_hash.sql

 * - Column Name
   - Type
   - Interpretation
 * - county_name
   - String
   - name of county
 * - hash
   - String
   - hash value entered by the given county after any successful upload


  cvr_hash.sql

1. The CVR file. This is only extracted if the ``-c True`` option is used.

  county_cvr_n.csv

2. The outcomes, vote counts and margins as calculated by the RLA
   Tool from the CVR file for contests chosen for audit

  tabulate.sql

 * - Column Name
   - Type
   - Interpretation
 * - county_name
   - String
   - name of county
 * - contest_name
   - String
   - name of contest
 * - choice
   - String
   - candidate or answer to question
 * - votes
   - Integer
   - number of votes for the given choice in the given contest
	in the given county
 * - votes_allowed
   - Integer
   - maximum number of votes one voter may cast in the given contest
 * - winners
   - List of Strings
   - list of names of winning choices in the given contest
	counting votes in the given county only
 * - min_margin
   - Integer
   - smallest difference between any winner's and any loser's vote totals 
	in the given contest, counting votes in the given county only
 * - county_ballot_count
   - Integer
   - number of ballot cards cast in the given county
 * - contest_ballot_count
   - Integer
   - number of ballot cards in the given county containing the given contest
	

11. The final audit report by county

  county_report_n.xlsx

12. The ballot manifest file

  county_manifest_n.csv

03. For each audited contest and each audited ballot card, the Audit
    Board interpretation (including “no consensus” designations or
    ballots not found).

  acvrs.sql

 * - Column Name
   - Type
   - Interpretation
 * - selection
   - 
   - 
 * - county
   - Integer
   - county number
 * - imprinted_id
   - String
   - imprinted id from CVR file
 * - record_type
   - String
   - 
 * - timestamp
   - Timestamp
   - time of submission of the audit board interpretation
 * - counted
   - True/False
   - 
 * - disagreement
   - List of Strings
   - 
 * - discrepancy
   - List of Strings
   - 
 * - comment
   - 
   - 
 * - consensus
   - Yes/No
   - "NO" if audit board reported no consensus for the given contest, otherwise "YES"
 * - contest_id
   - 
   - 
 * - cvr_id
   - 
   - 
 * - machine_choices
   - 
   - 
 * - acvr_id
   - 
   - 
 * - audit_board_choices
   - 
   - 


