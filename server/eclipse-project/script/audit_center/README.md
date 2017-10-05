# ColoradoRLA Exports for the public Audit Center

Usage: audit_center

Exports for the public dashboard are produced in these files:

seed.json:
   Seed for randomization

__:
   Random ballot order

ballots_to_audit_per_county.json:
   Number of ballots to be audited overall in each county

prefix_length.json:
   The number of ballots 

discrepancies.json:
   Details on each audited contest including ballot cards to audit, discrepancies

acvrs.json:
   Details about each ACVR entry

d. List of Audit Rounds (number of ballots, status by County, download links). Links shouldl be to all the finalized ballot-level interpretations and comparison details, in sufficient detail to independently verify the calculated risk levels. [as allowable by CORA]

e. Status (audit required, audit in progress, audit complete, hand count required,, hand count complete) by audited contest (i.e., contest "selected for audit" by SoS

f. Link to Final Audit Report

g. Audit Board names and political parties by County

manifest_hash.json:
  County Ballot Manifests and Hashes

