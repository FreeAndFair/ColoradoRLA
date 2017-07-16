\pagebreak

Glossary
========

See also the working documents at [VVSG-Interoperability Voting Glossary](http://collaborate.nist.gov/voting/bin/view/Voting/Glossary)
and the glossary in: ["Risk-Limiting Post-Election Audits: Why and How"](https://www.stat.berkeley.edu/~stark/Preprints/RLAwhitepaper12.pdf)

* **business interruption** - Any event that disrupts Contractor’s
ability to complete the Work for a period of time, and may include,
but is not limited to a Disaster, power outage, strike, loss of
necessary personnel or computer virus.

* **closeout period** - The period beginning on the earlier of 90 days
prior to the end of the last Extension Term or notice by the State of
its decision to not exercise its option for an Extension Term, and
ending on the day that the Department has accepted the final
deliverable for the Closeout Period, as determined in the
Department-approved and updated Closeout Plan, and has determined that
the closeout is complete.

* **deliverable** - Any tangible or intangible object produced by
Contractor as a result of the work that is intended to be delivered to
the State, regardless of whether the object is specifically described
or called out as a “Deliverable” or not.

* **disaster** - An event that makes it impossible for Contractor to
perform the Work out of its regular facility or facilities, and may
include, but is not limited to, natural disasters, fire or terrorist
attacks.

* **key personnel** - The position or positions that are specifically
designated as such in this Contract.

* **operational start date** - When the State authorizes Contractor to
begin fulfilling its obligations under the Contract.

* **other personnel** - Individuals and Subcontractors, in addition to Key
Personnel, assigned to positions to complete tasks associated with the
Work.

* **start-up period** - The period starting on the Effective Date and
ending on the Operational Start Date.

* **ballot manifest** - A document that describes how ballots are
organized and stored, and relates a Cast Vote Record to the
physical location in which the tabulated ballot is stored. The
ballot manifest specifies the physical
location of a ballot to allow staff to find the
specific ballot represented by a given CVR. A ballot manifest will
contain the following information: county ID, tabulator ID, batch ID,
the number of ballots in each batch, and the
storage location where the batch is secured following tabulation. A
sample ballot manifest is provided at [`manifest-dq.csv`](samples/manifest-dq.csv)

* **cast vote record (CVR)** - An electronic record
indicating how the marks on a ballot were interpreted as votes.
May be created by a scanner or DRE, or manually during an audit.
Sample CVRs in Dominion's format are in
`test/dominion-2017-CVR_Export_20170310104116.csv`.
See also [VVSG-Interoperability CVR Subgroup](http://collaborate.nist.gov/voting/bin/view/Voting/BallotDefinition).

* **contest** - A partisan or nonpartisan candidate race, or a
ballot measure, that appears on the ballot for an election in a
county.  Ex: Jane Doe for Colorado Secretary of State.

* **coordinated election** - Coordinated Elections occur on the first
Tuesday of November in odd-numbered years.  If the Secretary of State
certifies at least one statewide ballot measure to the counties, every
county will conduct the Coordinated Election, and the vast majority of
counties will include additional local ballot content in the election.
If the Secretary of State does not certify at least one statewide
ballot measure to the counties, then only those counties to which
local political subdivisions certify ballot content will conduct a
Coordinated Election in that year.

* **county administrator** - The designated representative(s) of each
county clerk and recorder who possesses RLA administrative user
privileges sufficient to upload a cast vote record and ballot manifest
for the county.

* **contest name** - The title of a contest.

* **election day** - The final day on which voters can cast a ballot in a
State Primary Election, Presidential Primary Election, Coordinated
Election, or General Election.

* **offeror** - A vendor that submits a responsible bid for this
Documented Quote.

* **pseudo-random number generator** - A random number generator
application that is further explained at
http://statistics.berkeley.edu/~stark/Java/Html/sha256Rand.htm
Test data is available at https://github.com/cjerdonek/rivest-sampler-tests

* **random seed** - A random seed (or seed state, or just seed) is data, such
as a number, vector or string, used to initialize a pseudorandom number generator.

* **responsible bid** - A bid from a vendor that can responsibly
(i.e. is reasonably able and qualified) do the work stated in the
solicitation.

* **risk-limiting audit (RLA)** - A procedure for manually checking a
sample of ballots (or voter-verifiable records) that is guaranteed to have
a large, pre-specified chance of correcting the reported outcome if the
reported outcome is wrong. (An outcome is wrong if it disagrees with the
outcome that a full hand count would show.) One paper describing
risk-limiting audits is located at
https://www.stat.berkeley.edu/~stark/Preprints/gentle12.pdf.

* **state administrator** - The designated representative(s) of the
Colorado Department of State, who possesses RLA administrative user
privileges to perform administrative tasks.

* **tabulation** - Interpretation of expressions of voter intent and aggregation
of those into election results.

* **tabulated ballots** - Paper ballots that have been scanned on a
ballot scanning device, and the voter’s markings on which have been
interpreted by the voting system as valid votes,
undervotes, or overvotes.  Tabulated ballots may be duplicates of
original ballots.

* **two-factor authentication** - Defined as two out of the three
following requirements:
  * Something you have (Examples: token code, grid card) 
  * Something you know (Example: passwords)
  * Something you are (Example: biometrics)

* **ENR system** - Election Night Reporting system, used to publish
election results starting on election night, and continuing through
the end of certification.

* **reported outcome** - The set of contest winners published by the ENR
system.

* **calculated outcome** - The set of contest winners according to the
CVRs that are being audited.

* **wrong outcome** - When the reported outcome for a given contest does not match the
outcome that a full hand count of the paper ballots would show.
This can happen due to equipment failures, adjudication errors,
and other reasons.

* **overstatement of the margin** TBD

* **understatement of the margin** TBD

* **evidence-based elections** - An approach to achieving election integrity
in which each election provides affirmative evidence that the reported
outcomes actually reflect how people voted. This is done via
software-independent voting systems, compliance audits and risk-limiting
audits.  An alternative to certifying voting equipment and hoping that it
functions properly in real elections.  See also *resilient canvass
framework*.  See
[Evidence-Based Elections - P.B. Stark and D.A. Wagner](https://www.stat.berkeley.edu/~stark/Preprints/evidenceVote12.pdf)

* **resilient canvass framework** - A fault-tolerant approach to conducting
elections that gives strong evidence that the reported outcome is correct,
or reports that the evidence is not convincing.  See also *evidence-based elections*.

* **compliance audit** - An audit which checks that the audit trail is
sufficiently complete and accurate to tell who won. Generally includes poll
book accounting, ballot accounting, chain of custody checks, security
checks, signature verification audits, voter registration record auditing,
etc.  Related terms include election canvass, ballot reconciliation.
See https://www.stat.berkeley.edu/~stark/Preprints/evidenceVote12.pdf

* **audit board** - A group of electors in each county nominated by the
major party chairpersons, which carries out an audit, with the assistance
of the designated election official, members of his or her staff, and other
duly appointed election judges.

* **audit** TBD May include ballot tabulation audits, compliance audits, ...

* **ballot tabulation audits** TBD. including risk-limiting audits,
opportunistic audits, bayesian audits, fixed-percentage audits, etc.

* **opportunistic audit** - An auditing technique designed to efficiently
generate evidence for additional contests in a ballot-level audit. A
significant part of the effort in doing a risk-limiting audit involves
physically retrieving the ballots selected for audit.  While doing the
manual tabulation and entering the data for the contests on that ballot
which are subject to strict risk limits, it is possible to
"opportunistically" do the same thing for other contests that are observed
on the same ballot, producing evidence about them for little additional
effort.  These are called "opportunistic contests".  If an opportunistic
contest achieves a risk limit, it can be "settled", and when it appears
on subsequent ballots during the audit, it need not be tabulated.
TBD: discuss need to consider possibility of sampling bias when
evaluating and reporting, considerations for possible escalation, etc.

* **mandatory contest** - A contest which is subject to a risk limit and is
factored in to the sampling calculations.

* **opportunistic contest** - A contest to be audited opportunistically.

* **active contest** TBD involving not having achieved risk limit

* **settled contest** TBD involving having achieved risk limit.  Note need
to ensure that calculations take into account the way the samples were
selected, in case any samples were taken in a stratified manner or taken
non-uniformly in order to target non-county-wide contests

* **uncontested contest** TBD

* **bayesian audits** TBD

* **voting method** TBD

* **electoral system** TBD

* **ballot** TBD including ballot id, imprinted ballot

* **margin** TBD

* **hash function** TBD, mentioning specifically SHA-256

* **RLA software** TBD

* **ballot storage bin** TBD

* **batch** TBD

* **batch size** TBD

* **chain-of-custody** TBD

* **county** TBD

* **scanner** TBD

* **imprinted ballot** - A ballot on which a unique ballot identifier has
been imprinted in order to facilitate a ballot-level audit. The unique
ballot identifier might for instance include a unique batch identifier and
the sequence of the ballot within the batch, or it might be a new
identifier which is also included in the CVR.
Imprinting should be done after the ballot is cast and with care taken to
avoid causing anonymity problems.

* **ballot order** TBD

* **Secretary of State (SOS)** TBD

* **Department of State (DOS)** TBD

* **audit report** TBD

* **SOS audit form** TBD

* **ballot certification** TBD

* **UOCAVA voter** TBD

* **UOCAVA ballot** TBD

* **mail ballot** TBD

* **election canvass** TBD

* **canvas board** TBD

* **post-election (historical, random) audit** TBD

* **county clerk** TBD

* **sample size** TBD including **initial sample size**

* **equipment** TBD

* **VVPAT** - A Voter-Verifiable Paper Trail consists of an audit trail
of Voter-Verifiable Paper Records (VVPRs). Elections which produce a
VVPAT allow an audit to gather evidence which the voter had an
opportunity to verify.  The VVPRs may appear in a continuous roll
of paper used to provide auditability for a DRE.

* **VVPR** - A Voter-Verifiable Paper Record, also known as a
Voter-Verifiable Paper Ballot (VVPB).  'Voter-verified' refers to the
fact that the voter is given the opportunity to verify that the choices
indicated on the paper record correspond to the choices that the voter
has made in casting the ballot. Risk-limiting audits require VVPRs.

* **non-voter-verifiable ballot** (NVVB) - A ballot for which there is
no auditable VVPR.  For example a ballot sent via an online ballot
return system or email, for which the voter has not returned a matching
voter verifiable paper ballot. AKA digital ballot.

* **phantom ballot** A ballot which is listed in the manifest, but is
not present as a physical ballot.  Phantom ballots can represent
discrepancies between the manifest and the actual paper ballot batches.
A manifest with a batch of purely phantom ballots can also be used
to represent the maximum number of possibly late-tabluation ballots.

* **late-tabulation ballot** - A ballot which is tabulated after the CVR
report and manifest are generated, but before the canvass is finished.

* **duplicated ballot** TBD

* **original ballot** TBD

* **DRE** TBD

* **overvote** TBD

* **stray mark** TBD

* **damage** TBD

* **undervote** TBD

* **risk limit** - The pre-specified minimum chance of requiring a full
hand count if the outcome of a full hand count would differ from the
reported tabulation outcome.

* **voting system** TBD

* **Dr. Philip Stark** TBD

* **Dr. Mark Lindeman** TBD

* **Dr. Ron Rivest** TBD

* **Colorado House Bill 09-1335** TBD

* **EAC** TBD

* **Clear Ballot Group** TBD

* **Clear Ballot ClearCount** TBD

* **OpenCount** TBD

* **Dominion** TBD

* **Dominion Democracy Suite** TBD

* **dashboard** TBD

* **developer dashboard** TBD

* **state-wide dashboard** TBD

* **county dashboard** TBD

* **audit progress** TBD

* **discrepency** TBD

* **random** TBD

* **contest margin** TBD

* **access control** TBD

* **role** TBD

* **fault tolerance** TBD

* **user interface (UI)** TBD

* **user experience (UX)** TBD

* **data synchronization** TBD

* **Colorado Department of State (CDOS)** TBD

* **abstract state machine** (AST) - TBD including [Abstract state machine](https://en.wikipedia.org/wiki/Abstract_state_machines)
