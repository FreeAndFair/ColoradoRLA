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

The exported files include files necessary for independent verification of 
the RLA, whether by candidates, parties, other organizations. Note that the 
CVR and Ballot Manifest files originally uploaded by the Counties are
an essential ingredient for any independent verification. 

If the Colorado Department 
of State provides to the public all the files exported by the rla_export command (detailed below), along with:

- all CVR files
- all ballot manifest files
- opportunity to observe the random seed selection
- opportunity to observe the activities of the County Audit Boards
- announced tabulated results for contests selected for audit
- the risk limit
- the error inflation factor
then the Colorado Department of State will have fulfilled the minimum
requirements of a publicly-verifiable audit.

The data files supporting independent verification are generated based on ``sql`` query files.
These are always produced in two formats: json and csv.
The basename of each resulting file is the same as the basename of the query file.
Thus, given the query file ``seed.sql``, the files ``seed.json`` and ``seed.csv``
will be produced.

+ List of contests selected by Secretary of State for audit, with current status. 
  Which contests has the 
  Secretary selected for audit? Which contests (if any) has the 
  Secretary selected for hand count?

m_status.sql
  * - Column Name
    - Data Type
    - Meaning
  * - county_name
    - Text String
    - Name of County
  * - contest_name
    - Text String
    - Name of contest
  * - audit_status
    - Text String
    - Blank, NOT_STARTED, IN_PROGRESS, RISK_LIMIT_ACHIEVED, or ENDED. 
      Before a user from the Department of State clicks the "Launch Audit" button, 
      the audit status is blank.
      ENDED means "ended without achieving the risk limit" because the 
      Secretary of State desgnated the contest for a hand count. 

+ Hashes of CVR and ballot manifest files
m_cvr_hash.sql
  * - Column Name
    - Type
    - Meaning
  * - county_name
    - Text String
    - Name of County
  * - hash
    - Text String
    - Hash value entered by the given county after uploading the cast vote record file
      to be used in the audit

m_manifest_hash.sql
  * - Column Name
    - Type
    - Meaning
  * - county_name
    - Text String
    - Name of County
  * - hash
    - Text String
    - Hash value entered by the given county after uploading the ballot manifest file
     to be used in the audit


+ Random sequence of ballot cards used for the audit. 
  (This random sequence is generated “with replacement” and thus may include duplicates.)
m_random_sequence.sql
  * - Column Name
    - Data Type
    - Meaning
  * - county_name
    - Text String
    - Name of County
  * - review_index
    - Integer
    - The position in the random sequence for the given County
  * - scanner_id
    - Integer
    - TabulatorNum from Dominion CVR export file, 
      identifying the tabulator used to read the physical ballot card   
      with the given review-index
  * - batch_id
    - Integer
    - BatchId from Dominion CVR export file, 
      identifying the batch of physical ballot cards in which the card
      with the given review-index was scanned
  * - record_id
    - Integer
    - RecordId from Dominion CVR export file,
      indicating the position of the card 
      with the given review-index
      in its batch of physical ballot cards 
  * - imprinted_id
    - Text String
    - combination of scanner, batch and record ids 
      that uniquely identifies the ballot card 
      with the given review-index
      and may be imprinted on the card
  * - ballot_type
    - Text String
    - BallotType from Dominion CVR export file, a code for the set of contests that 
      should be present on the physical ballot card
      with the given review-index


+ List of ballot cards assigned to Audit Board for review. 
  (This list could be created from the random sequence by removing duplicates 
  and ordering by tabulator, batch and position within the batch.) 
  Within each county, the list is ordered by rounds 
  and, within each round, by tabulator, batch and position within the batch.
m_ballot_list_for_review.sql
  * - Column Name
    - Data Type
    - Meaning
  * - county_name
    - Text String
    - Name of County
  * - round
    - Integer
    - The audit round number in which the ballot card is assigned 
      to the given County's Audit Board for review.
  * - scanner_id
    - Integer
    - TabulatorNum from Dominion CVR export file, 
      identifying the tabulator used to read the physical ballot card   
  * - batch_id
    - Integer
    - BatchId from Dominion CVR export file, 
      identifying the batch of physical ballot cards in which the card
      was scanned
  * - record_id
    - Integer
    - RecordId from Dominion CVR export file,
      indicating the position of the card 
      in its batch of physical ballot cards 
  * - imprinted_id
    - Text String
    - combination of scanner, batch and record ids 
      that uniquely identifies the ballot card 
      and may be imprinted on the card
  * - ballot_type
    - Text String
    - BallotType from Dominion CVR export file, a code for the set of contests that 
      should be present on the physical ballot card

+ For each contest under audit, and for each ballot examined in the audit, 
  the RLA system's record of the Audit Board's interpretation of the marks 
  on the physical ballot for that contest
  * - Column Name
    - Data Type
    - Meaning
  * - county_name
    - Text String
    - Name of County
  * - contest_name
    - Text String
    - Name of contest
  * - scanner_id
    - Integer
    - TabulatorNum from Dominion CVR export file, 
      identifying the tabulator used to read the physical ballot card   
  * - batch_id
    - Integer
    - BatchId from Dominion CVR export file, 
      identifying the batch of physical ballot cards in which the card
      was scanned
  * - record_id
    - Integer
    - RecordId from Dominion CVR export file,
      indicating the position of the card 
      in its batch of physical ballot cards 
  * - imprinted_id
    - Text String
    - combination of scanner, batch and record ids 
      that uniquely identifies the ballot card 
      and may be imprinted on the card
  * - ballot_type
    - Text String
    - BallotType from Dominion CVR export file, a code for the set of contests that 
      should be present on the physical ballot card
  * - computer_interpretation_of_voter_choice
    - List of Text Strings
    - List of voter choices in the given contest on the given ballot card, as interpreted
      by the vote-tabulation computer system
  * - audit_board_interpretation_of_voter_choice
    - List of Text Strings
    - List of voter choices in the given contest on the given ballot card, as interpreted
      by the Audit Board
  * - did_audit_board_agree
    - Yes/No
    - "Yes" if the Audit Board came to consensus on the interpretation
      of the given ballot card; "No" if not; 
      blank if the card has not been reviewed by the Audit Board.
  * - audit_board_comment
    - Text String
    - Text of comment entered by Audit Board 
      about the given contest on the given ballot card
  * - review_index
    - Integer
    - 
  * - 
    - 
    - 




Other export files are the same as the files available via the GUI interface,
for example ``state_report.xlsx``.

