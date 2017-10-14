# Documentation for `rla_tool_export`

The `rla_tool_export` command exports ColoradoRLA data for publication on
the Audit Center web site.

## Installation

Installing required packages:

`pip install rla_tool_export`

## Abbreviated usage

This will run queries using all the standard .sql files provided in the
package:

`rla_tool_export [-p properties_file] [-e export_directory]`

## Export a query in json and csv format for selected sql files

`rla_tool_export [-e export_directory] file.sql ...`

## Full command line usage synopsis

`rla_tool_export -h`

## Exports for the public dashboard are produced in these files

Done:

* Python` rla_tool_export/__main__.py` code to automatically run json exports for queries in the `scripts/sql` directory, with authentication

* a. Seed for randomization
  * seed.sql
* b. random ballot order
* c. Number of ballots to be audited overall in each audited contest in each county
   * ballots_to_audit_per_county.sql
* f. Link to Final Audit Report
* g. Audit Board names and political parties by County
   * auditboards.sql
* h. County Ballot Manifests and Hashes (status & download links)
   * manifest_hash.sql
   * cvr_hash.sql
* 01 The CVR file
* 11 The final audit report
* 12 The ballot manifest file
* 02 The outcomes, vote counts and margins as calculated by the RLA Tool from the CVR file for contests chosen for audit
  *  tabulate.sql
* 03 For each audited contest and each audited ballot card, the Audit Board interpretation (including “no consensus” designations or ballots not found).
  * acvrs.sql

Not done:
* d. List of Audit Rounds (number of ballots, status by County, download links). Links should be to all the finalized ballot-level interpretations and comparison details, in sufficient detail to independently verify the calculated risk levels. [as allowable by CORA]
* e. Status (audit required, audit in progress, audit complete, hand count required,, hand count complete) by audited contest (i.e., contest "selected for audit" by SoS

* Extras, may not be necessary, may go away:
  * discrepancies.json
    * Details on each audited contest including ballot cards to audit, discrepancies
  * prefix_length.json
