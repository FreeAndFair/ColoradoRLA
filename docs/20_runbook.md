\pagebreak

# Run Book 

## Introduction

### What the RLA Tool Does

The Risk-Limiting Audit (RLA) tool, developed by Free & Fair 
for the Colorado Department of
State for use in elections from November 2017 forward, supports a 
Risk-Limiting Audit as required by Colorado statute and 
as described in the Colorado Secretary of State's Rule 25 for
elections. RLA-related terms and processes are defined and prescribed in Rule 25.
***http://www.sos.state.co.us/pubs/rule_making/CurrentRules/8CCR1505-1/ElectionRules.pdf

The RLA Tool enables the Secretary of State and each individual 
County to carry out a Risk-Limiting Audit. The Secretary of State can
enter the risk limit, the random seed and the contests that drive
the audit process. The Secretary of State can also identify 
contests that should go to a full hand count. Counties can upload
ballot manifests and cast vote record (CVR) files. The
RLA tool performs the necessary calculations (such as 
choosing random samples of ballots for audit and determining 
actual risk levels). County Audit Boards can 
enter interpretations of ballots into the tool. And the Secretary of
State can monitor the progress of the individual Counties.

The RLA Tool also creates export files for use in the Audit
Center, a website disseminating audit-related information
to the public.
 
### What the RLA Tool Does Not Do

Although the rules governing the RLA requires Counties to 
generate and preserve a Summary Results Report and a Results File Export, the 
RLA Tool does not interact with these particular files in any way. 
Nor does the RLA Tool aid the CVR Export Verification required by the 
rules. These processes and files are not discussed in this Run Book.

While the RLA Tool does provide for upload of the 
hashed Ballot Manifest, the hashed CVR file and their hashes, 
the RLA Tool does not provide a hashing utility.

The current version of the RLA Tool does not provide any support
for chain of custody logs. Nor does it provide any rules 
or suggestions for interpretation of the marks on any particular ballot. 

## For Secretary of State Users

### Logging In

Screenshot (SoS authentication) ---

### Entering the Risk Limit

The Sectretary of State will enter the Risk Limit for comparison audits.

Screenshot (SoS dash?) --- 

### Entering the Random Seed

The Secretary of State will enter the random seed.

Screenshot (SoS dash?) ---

### Selecting Contests

The Secretary of State will select the contests that will
drive the sample size and stop/go decisions for each round.

Screenshot (SoS dash?) ---

### Reading the Number of Ballots to Audit

The RLA Tool calculates the number of ballots to audit in each
round of the RLA and provides that number to the Secretary of State Dashboard.

Screenshot (SoS dash?) ---

### Audit Ongoing Page

This screen provides an overview of the audit in progress, both
counties, and contests.

### Declaring a Full Hand Count

Screenshot ---

## For County Administrators

For the 2017 Colorado implementation, the full functionality of the RLA Tool
requires exports from the Dominion System ???. ***Colorado2017 ***which Dominion system?

### Logging In

Screenshot (County Admin Authentication) ---

### <a name="comparison-audit-upload"></a>Comparison Audit Upload

To prepare for upload the ballot manifest and the CVR file must be
first verified and hashed. The RLA Tool does not provide a utility for the 
verification and hashing.

***ballot manifest upload prep

Once the ballot manifest and CVR files are verified and hashed, 
they can be uploaded. If the upload process is interrupted the
process will have to be repeated, it will not resume uploads.

Screenshot (page:county) ---

Note that the RLA Tool does not support export of the
tabulation results to the Election Night Reporting System. 

### Ballot Polling Audit Upload

The current version RLA Tool is designed for comparison audits, which require a voting
system capable of exporting CVR files. However, Counties whose voting systems do not
export CVR files can use the RLA Tool to upload ballot manifests, described
in the [Comparison Audit Upload](#comparison-audit-upload) 
section.

### List of Ballot for Audit

To see the list of ballots to be audited in the next round.

Screenshot (???) ---

## For County Audit Boards

========

### Signing In

The Audit Board does not log directly into the RLA Tool with
computer system usernames and passwords. However, whenever 
the Audit Board begins to interact with the RLA Tool, either 
at the beginning of an audit round or after taking a break, 
there is an informal sign-in process. After the authenticated County Audit 
Administrator formally logs in, there is a screen where the identity and 
party affiliation of Audit Board Members can be entered or changed.

Screenshot (page:Acme County) ---

Click on "Start Audit"

Screenshot (page:Audit Board Sign In) ---

Click "Next"

### Auditing Ballots

Note: do *not* use your browser's "back" arrow during the audit process.

Screenshot (Ballot Verification) ---

Before recording voter intent, please double-check that the paper ballot
ID and Ballot Style match the ID and ballot style listed on the screen.
Then record on the screen all final voter intent marked on the paper ballot.
In case of an overvote, mark each of the (too many) choices the voter 
intended; the RLA tool will recognize the overvote. In case the Audit 
Board cannot reach consensus, mark the "No Consensus" box on the screen.
Audit Board members can make notes in the comment field.
***Review CDOS

Click "Review"

Screenshot (Review Screen) ---

Check that the information on the review screen reflects the 
Audit Board's interpretation of the ballot. Note that in case of 
an overvote, the review screen will say, simply, "overvote".

Note: do *not* use your browser's "back" arrow during the audit process.
If the review screen does not match the Audit Board's interpretation,
click the "Back" button.

If the review screen matches the Audit Board's interpretation,
click "Submit and Next Ballot". This submission is final. There 
is no way to revise a ballot interpretation once it has been submitted
from the Review Screen.

This process continues until the audit is complete, or a Full Hand Count
has been called by the CDOS.
