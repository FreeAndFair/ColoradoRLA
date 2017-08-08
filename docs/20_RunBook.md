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
*** citation for Rule 25.

The RLA Tool enables the Secretary of State and each individual 
County to carry out a Risk-Limiting Audit. The Secretary of State can
enter the risk limit, the random seed and the contests that drive
the audit process. The Secretary of State can also identify 
contests that should go to a full hand count. Counties can upload
ballot manifests and cast vote record (CVR) files. The
RLA tool performs the necessary statistics (such as 
choosing random samples of ballots for audit and determining 
actual risk levels)
County Audit Boards can 
enter interpretations of ballots into the tool. And the Secretary of
State can monitor the progress of the individual Counties.

The RLA Tool also creates export files for use in the Audit
Center, a website disseminating audit-related information
to the public.
 
### What the RLA Tool Does Not Do

Although the RLA process requires Counties to 
generate and preserve
a Summary Results Report and a Results File Export, the RLA Tool 
does not interact with these files in any way. Nor does the RLA Tool 
aid in any way with the CVR Export Verification required by the 
RLA process. 

While the RLA Tool does provide for upload of the 
hashed Ballot Manifest, the hashed CVR file and their hashes, 
the RLA Tool does not provide a hashing utility.

The RLA Tool does not provide any support
for chain of custody logs. Nor does it provide any guidance for ballot 
interpretation. 

## For Secretary of State Users

### Logging In

Screenshot (SoS authentication) ---

### Identifying County Users

The Secretary of State will determine, for each individual
County, which user will be able to log in to the County-facing
part of the RLA Tool.

Screenshot (SoS County User Identification)

### Entering the Risk Limit

To enter the Risk Limit for comparison audits:`
Screenshot (SoS dash?) --- 

### Entering the Random Seed

To enter the random seed:
Screenshot (SoS dash?) ---

### Selecting Contests

To select the contests that will drive the sample size and stop/go
decisions for each round:
Screenshot (SoS dash?) ---

### Reading the Number of Ballots to Audit

The RLA Tool calculates the number of ballots to audit in each round of the RLA. To read
the number of ballots for each round:
Screenshot (SoS dash?) ---

### Declaring a Full Hand Count

Screenshot ---


Basic overview: Screenshots of the dashboard/tool, ordered in the sequence
they will be encountered, with brief, clear instructions describing what
is expected of the user(s) at each step, starting with the login screen
and proceeding through the completion of the audit, or determination that
a full hand count is required. First SOS, then County, then Public.

## For County Administrators

For the 2017 Colorado implementation, the full functionality of the RLA Tool requires exports from the 
Dominion System ???. 
***Colorado2017
***which Dominion system?

### Logging In

Screenshot (County Admin Authentication) ---


### <a name="comparison-audit-upload"></a>Comparison Audit Upload

To prepare for upload the 
ballot manifest and the CVR file must be first verified and hashed. 
The RLA Tool does not provide a utility for the 
verification and hashing. 
***ballot manifest upload prep

Once the ballot manifest and CVR files are verified and hashed, 
they can be uploaded.

Screenshot (page:county) ---

Note that the RLA Tool does not support export of the
tabulation results to the Election Night Reporting System. 

### Ballot Polling Audit Upload
The RLA Tool is designed for comparison audits, which require a voting
system capable of exporting CVR files. However, Counties whose voting systems do not
export CVR files can use the RLA Tool to upload ballot manifests, described
in the [Comparison Audit Upload](#comparison-audit-upload) 
section.

### List of Ballot for Audit
To see the list of ballots to be audited in the next round:
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
***Review CDOS

Click "Review"

Screenshot (Review Screen) ---

Check that the information on the review screen reflects the 
Audit Board's interpretation of the ballot. Note that in case of 
an overvote, the review screen will say, simply, "overvote".

Note: do *not* use your browser's "back" arrow during the audit process.
If the review screen does not match the Audit Board's interpretation,
click "Back".

If the review screen matches the Audit Board's interpretation,
click "Submit and Next Ballot". This submission is final. There 
is no way to revise a ballot interpretation once it has been submitted
from the Review Screen.

# Comments and Questions on Draft

***should the run book deal with the SOS establishing the risk
limit, or any of their pre-audit responsibilities like the selection
of which races to audit? (SS says, yes, briefly, in the context of 
what needs to be entered into the tool.)

***should the run book cover the preparation/creation of these artifacts
they’ll be uploading? SS says: no, but should specify requirements for 
these artifacts.

Description of the process of generating the random seed, perhaps with a
photograph of what the process will look like with folks looking on.
***SS says: out of scope for the run book

***Will the risk limit be auto populated to the counties’ dashboards, or
will they be instructed to enter it themselves? SS says: From my 
understanding of the process, Counties don't actually need to know the 
risk limit. If they want it, they should be able to find it on the public dash.

***should more detailed instructions/descriptions for the retrieving of
ballots, and the rules about interpretation of ballot marks, be included
in the run book? SS says: no.

Contest landing page ---?

Contest detail page ---?
*** SS asks: what are these pages?

I don't think (but will happily stand corrected) the run book needs to contain
the 'why' -- only the 'how' and where/when/etc. of the process as experienced
by the users/auditors. Each screenshot should have an associated index
describing key features/fields/etc. which can be numerically identified.
Instructions will instruct auditors to loop back if another round (or rounds)
of ballots need to be inspected.
***SS agrees.

Besides a printed document with screenshots and instructions, we could also
offer to create a tutorial video (videos, probably) where we film people using
the tool/dashboards, with narration describing what they are doing, and what comes
next depending on what’s happening.
***SS says, it would be nice, but out of scope. We're so far over budget already....

Brief introduction to RLAs in non-technical, human friendly language.
At the county level, something along the lines of "This tool is going
to help you cross reference votes recorded on paper ballots with how 
the computers read those ballots, to determine whether the computers
did their job accurately…” Try to put their minds at ease about the
process. :)

***should the run book cover the mechanics of storing ballots and the
other physical aspects of the RLA process which are not ‘in’ the RLA
tool/system? SS says, no.


