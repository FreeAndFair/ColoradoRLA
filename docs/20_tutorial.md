\pagebreak

Run Book outline
========

***should the run book deal with the SOS establishing the risk
limit, or any of their pre-audit responsibilities like the selection
of which races to audit? (SS says, yes, briefly, in the context of 
what needs to be entered into the tool.)

Brief introduction to RLAs in non-technical, human friendly language.
At the county level, something along the lines of "This tool is going
to help you cross reference votes recorded on paper ballots with how 
the computers read those ballots, to determine whether the computers
did their job accurately…” Try to put their minds at ease about the
process. :)

Terms and process for the RLA are defined and prescribed in Rule 25.
*** citation for Rule 25.

***should the run book cover the mechanics of storing ballots and the
other physical aspects of the RLA process which are not ‘in’ the RLA
tool/system?

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
***review please!

The RLA Tool does not provide any support for paper ballot retrieval or 
for chain of custody logs. Nor does it provide any guidance for ballot 
interpretation. 

## For Secretary of State Users

### Logging In

Screenshot (SoS authentication) ---

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
the Audit Board must sign in.

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


Basic overview: Screenshots of the dashboard/tool, ordered in the sequence
they will be encountered, with brief, clear instructions describing what
is expected of the user(s) at each step, starting with the login screen
and proceeding through the completion of the audit, or determination that
a full hand count is required. First SOS, then County, then Public.

Detailed instructions with associated screenshot about the process of
Audit Board members signing in.

Detailed descripton of the SOS dashboard with associated screenshot with
descriptions of all fields and/or information/updates.

Detailed description with associated screenshot of the County dashboard
with instructions about the process of uploading ballot manifests and
CVRs, and associated hash functions.

***should the run book cover the preparation/creation of these artifacts
they’ll be uploading?

Description of the process of generating the random seed, perhaps with a
photograph of what the process will look like with folks looking on.

***Will the risk limit be auto populated to the counties’ dashboards, or
will they be instructed to enter it themselves?

***should more detailed instructions/descriptions for the retrieving of
ballots, and the rules about interpretation of ballot marks, be included
in the run book?

Contest landing page ---?

Contest detail page ---?

I don't think (but will happily stand corrected) the run book needs to contain
the 'why' -- only the 'how' and where/when/etc. of the process as experienced
by the users/auditors. Each screenshot should have an associated index
describing key features/fields/etc. which can be numerically identified.
Instructions will instruct auditors to loop back if another round (or rounds)
of ballots need to be inspected.

Besides a printed document with screenshots and instructions, we could also
offer to create a tutorial video (videos, probably) where we film people using
the tool/dashboards, with narration describing what they are doing, and what comes
next depending on what’s happening.
