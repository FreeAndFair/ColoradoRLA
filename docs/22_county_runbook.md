% County Run Book
% Colorado Risk Limiting Audit Tool v1.0.0 alpha-2
% 2017


# Introduction

This Run Book guides County election administrators 
who
will be implementing a Risk Limiting Audit (RLA) with a comparison
audit.  The RLA Tool, developed by Free & Fair for the Colorado
Department of State for use in elections in November 2017 and later,
supports running a Risk-Limiting Audit as required by Colorado statute
and as specified in
the
[Colorado Secretary of State's Rule 25](http://www.sos.state.co.us/pubs/rule_making/CurrentRules/8CCR1505-1/ElectionRules.pdf) for
elections.

Orange arrows on the screenshots indicate features described in the
text of the Run Book.

 
# County Admin Functions
## Launching and Logging In

A County user can launch the RLA Tool by pointing a browser to the
URL provided by the Colorado Department of State.

![Login](./screenshots/100_login_screen_1.png)

Successful entry of username and password will lead to 
a two-factor authentication grid challenge.

![Grid Challenge](./screenshots/101_login_screen_2.png)

## Navigation

Once two-factor authentication is complete, County users will see the County Home Page
and can enter the information to set up the audit.

![County Home Page at Start](./screenshots/county/200_County_Home_Before.png)


In the upper left corner of every page the County site has a menu that
can be used to navigate to the County Home Page, the Audit Board
Sign-In/Out pages or the Audit (Ballot Review) pages. Click on the
navigation icon to bring up the navigation menu.

![Navigation Icon](./screenshots/county/000_main_nav.png)

![Navigation Menu](./screenshots/county/001_menu_choices.png)

In the upper right corner of each page is a button to return to 
the County Home Page 

![Home Button](./screenshots/000_home_icon.png)

and a button to log out of the system 
altogether.

![Logout Button](./screenshots/000_logout_button.png)


## Ballot Manifest and CVR File Uploads

To prepare for upload, the County's ballot manifest and CVR files must
each be hashed using any SHA-256 hash utility. The RLA Tool itself
does not provide a utility for hashing, but uploaded files are
verified against the provided hash.

Once the ballot manifest and CVR files are hashed, they can be
uploaded into the RLA Tool. If the upload is interrupted, the process
will have to be repeated. It is not possible to resume an interrupted
upload—just start that file upload again.  If a County uploads
multiple Ballot Manifest files, only the data from the last file will
be used. Similarly, if a County uploads multiple CVR files, only the
data from the last file will be used. To upload the ballot manifest:

![To upload the CVRs:](./screenshots/county/100_upload_proper_ballot_manifest.png)

***

![](./screenshots/county/102_upload_proper_CVR.png)

If the hash does not match the file (either because the wrong hash was
entered, or because the file was changed as it traveled over the
network), the data from the file will be uploaded but the data will
not be imported. The user will see the following messages.

![Hash Mismatch Error](./screenshots/county/104_hash_mismatch.png)

**Remedies and Mitigations**

In this situation, please double-check that you have: (a) chosen and
uploaded the correct file, (b) hashed the correct file, (c) use the
correct kind of hash (it must be SHA-256), and (d) copied the entire
hash code into the appropriate text box.

If the hash matches the file but the file is not in the proper format,
the data from the file will not be imported. The user will see the
following messages.

![Format Error](./screenshots/county/225_bad_format.png)

If the hash matches and the file format is correct, the system will
import the data from the file. The user will see the following
messages.

![File Uploads Successful](./screenshots/county/106_successfully_uploaded.png)

**Remedies and Mitigations**

In this situation, please double-check that you have: (a) chosen and
uploaded the correct file, (b) removed the "CountingGroup" column 
from the CVR file, (c) not edited or tampered with the file in
any other fashion, and (d) are uploading a CSV file, not a JSON file.

The time required for data import depends on the size of the
file. Import time for the CVR file will depend on the number of ballot
cards represented in the file. A file with fewer than 10,000 CVR lines
should take less than a minute, while a file with 500,000 CVR lines or
more might take about a half hour. The ballot manifest file upload and
import will be quicker than the CVR file upload and import.

Once both the ballot manifest and CVR files are successfully uploaded
and imported users will see them both listed as **uploaded**. It is
possible to re-upload either or both files, if necessary.

After successfully upload and import of both the Ballot Manifest and
the CVR files, the Contest Info table near the bottom of the County
Home Page lists all contests from the CVR file. The user may have to
scroll down to see all contests.

![Contest Info Displayed on Home Page](./screenshots/county/202_contest_info.png)

At this stage in the audit process the County must wait for the
Department of State to choose which contests to audit and what the
risk limits will be on those contests.  Once the Department of State
has started the audit, the County may continue to the next step of the
audit process.

## Monitoring the Audit

While the audit is on-going, the County Info table shows how many Ballot Cards
are required for the current round, and how many have already been
audited. The number of "disagreements" is the number of ballot cards
on which the Audit Board could not come to complete consensus. The
number of "discrepancies" is the number of ballot cards where the
Audit Board's interpretation differs from the interpretation in the
uploaded CVR file, or where the ballot card in question could not be
found.

![County Home Page During Audit](./screenshots/county/201_County_Home_Mid_Round.png)

# Audit Board Functions

To sign the Audit Board in or out, click the "Audit Board" button on the County
home page.

![Audit Board Sign-In Button](./screenshots/county/248_nav_to_AB_page.png)

Another way is to use the navigation menu.

![Navigation to Audit Board Identification Page](./screenshots/county/002_menu_choices_ab.png)

Either method will take the user to the Audit Board page.

![Audit Board page](./screenshots/county/250_AB_sign_in.png)

## Audit Board Sign In

The Audit Board does not log directly into the RLA Tool with usernames
and passwords. However, whenever the Audit Board begins to interact
with the RLA Tool, either at the beginning of an audit round or after
taking a break, there is an informal sign-in process.
Note that all audit board
members must input first and last names as well as party affiliation.
If an audit board member has only a single name, include it as they
normally would on any digital input form and put "N/A" in the unused
field.

After the Audit Board has signed in, the Audit Board Screen
will show the names and party affiliations. 

![Audit Board Identity](./screenshots/county/251_AB_members.png)

## Ballot Card Retrieval

After the Audit Board has signed in, they can start the audit process.
Use the 
"Start Audit" button on the Audit Board screen. Or
use the navigation menu at the top left. 

![Navigation to Ballot Card Review](./screenshots/county/003_menu_choices_audit.png)

There is a welcome screen for the audit. Click "Next" to proceed.

![Welcome Screen](./screenshots/county/260_Audit_Screen_1.png)


If the Secretary of State has launched a round of the audit the County
user will see a list of ballot cards to be audited in the current
round. The list includes the Scanner, Batch, and Ballot Position
numbers, and (if available) the Storage Bin. As the round progresses the rightmost 
column will contain check marks for ballots that have been reviewed. 

![Ballot Cards to Audit](./screenshots/county/262_Cards_to_Audit.png)

This page has  a button to download a csv file that can be saved or printed, containing 
the list of ballots.

![Download CSV](./screenshots/county/263_download_csv.png)

## Ballot Card Review

Once the ballot cards have been retrieved, click the "Next" button to
start reviewing.

The RLA Tool allows Audit Board members to report the markings on each
individual ballot card.  Before recording voter intent, please
double-check that the Ballot Type on the paper ballot card matches the
Ballot Type listed on the screen.

![Ballot Type](./screenshots/county/265_ballot_type.png)

If a ballot card is not found click the "Ballot Card Not Found" button.

![Ballot Card Not Found](./screenshots/county/264_ballot_not_found.png)

Otherwise record on the screen all final voter intent marked on
the paper ballot card. 

![Recording Voter Intent](./screenshots/county/264_Voter_Intent.png)

The list of candidates includes qualified write-in candidates.

In case of an overvote, mark each of the (too many) choices the voter
intended; as the user will see on the next screen, the RLA Tool will recognize
the overvote.  

![Marking an Overvote](./screenshots/county/264_overvote.png)

If the Audit Board cannot reach consensus, mark the "No Consensus" box
on the screen. There is a text box for any comments the audit board
might wish to add in this circumstance.

![No Consensus](./screenshots/county/264_no_consensus.png)

After entering the interpretation of the markings from any one ballot
card, the Audit Board clicks the "Review" button.

![Review Button](./screenshots/county/265_ballot_interpretations_entered.png)

On the Review screen, check that the information shown
reflects the Audit Board's interpretation of the ballot. 

![Review Screen](./screenshots/county/266_review_screen.png)

Note that in
case of an overvote, the review screen will say "Overvote for this
contest".

![Overvote Review](./screenshots/county/267_overvote_review.png)

If the review screen does not match the Audit Board's interpretation,
click the "Back" button and correct the interpretation.  If the
information on the review screen is correct, click "Submit & Next
Ballot".

This submission is
final. There is no way to revise a ballot interpretation once it has
been submitted from the Review Screen.

![Submitting](./screenshots/county/267_submit_interpretation.png)

The screen for the next ballot card will then be displayed. Review of
ballot cards will continue until the Audit Board has reviewed all the
ballot cards assigned to that County for that round. 

## End of Round

After the last
ballot card has been reviewed, the end-of-round page will appear.
with boxes for the Audit Board to certify the round
by entering their names as indicated. The order of the names
does not matter. Clicking the "Submit" button on this page ends the County's work for the 
audit round. 

![Certification](./screenshots/county/268_certification.png)

After certification, until a new round starts, the end of round page appears.

![End of Round](./screenshots/county/268_end_of_round.png)

## Signing out the Audit Board for Breaks

If the Audit Board needs to take a break, go to lunch, head home for
the night, or pause their work for any reason, then the Audit Board
must use the Sign Out button on the Audit Board Page.

![Audit Board Sign Out](./screenshots/county/275_sign_out_of_audit_board.png)

Whenever the Audit Board is signed out, the Audit Board page will display 
the sign-in form. 

# Ending the Audit

When the County has completed the entire audit, including all rounds
of ballot card review required by the State, the County Home Page
will appear. The page will state that "the audit is complete".

![End of Audit](./screenshots/county/270_end_of_audit.png)

# Audit Reports

The "Download" button on the County Home page will download an Intermediate Audit Report if 
the audit is still in progress and a Final Audit Report if the audit is complete.


<!-- ## Hand Counts ##
-->









