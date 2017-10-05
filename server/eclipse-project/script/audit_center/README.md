# Audit_Center: Export ColoradoRLA data for publication on Audit Center web site

## Abbreviated usage:

This will run queries using all the .sql files in the `$SQL_DIR`
environmental variable, which is the current directory by default:

`audit_center [-e export_directory]`

## Export a query on selected sql files:

`audit_center [-e export_directory] file.sql ...`

## Full command line usage synopsis:

`audit_center -h`

Exports for the public dashboard are produced in these files:

Done:

* Python` audit_center/__main__.py` code to automatically run json exports for queries in the `scripts/sql` directory, with authentication

* a. Seed for randomization
  * seed.sql
* c. Number of ballots to be audited overall in each audited contest in each county
   * ballots_to_audit_per_county.sql
* g. Audit Board names and political parties by County
   * auditboards.sql
* h. County Ballot Manifests and Hashes (status & download links)
   * manifest_hash.sql
   * cvr_hash.sql
* 02 The outcomes, vote counts and margins as calculated by the RLA Tool from the CVR file for contests chosen for audit
  *  tabulate.sql
* 03 For each audited contest and each audited ballot card, the Audit Board interpretation (including “no consensus” designations or ballots not found).
  * acvrs.sql

Not done:

* b: random ballot order
  * via crtest including all audited and dups (plus some?)
* d. List of Audit Rounds (number of ballots, status by County, download links). Links should be to all the finalized ballot-level interpretations and comparison details, in sufficient detail to independently verify the calculated risk levels. [as allowable by CORA]
* e. Status (audit required, audit in progress, audit complete, hand count required,, hand count complete) by audited contest (i.e., contest "selected for audit" by SoS
* f. Link to Final Audit Report
  * via crtest
* 01 The CVR file
   * via crtest
* 11 The final audit report
   * via crtest
* 12 The ballot manifest file
   * via crtest

* Extras, may not be necessary, may go away:
  * discrepancies.json
    * Details on each audited contest including ballot cards to audit, discrepancies
  * prefix_length.json
