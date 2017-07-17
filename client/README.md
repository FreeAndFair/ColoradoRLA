# CO RLA Browser Client

## Site map

Though implemented as a single-page browser application, we can use
URL paths to organize the activities provided by the application. The
below is a working draft of these activities, which we will update as
needed in response to finalization of the process specification and
data from user testing.

### Login
#### `/login`

Authenticate as a user of any type.

### Home
#### `/`

User dashboard, varying by type of user.

### Audit Summary
#### `/audit`

A high-level view of any audits defined, with links to either
configure or perform audit steps, as appropriate.

### Audit Data Upload
#### `/audit/upload`

Place for admin users to upload data for audits.

### Audit Seed
#### `/audit/seed`

View or set the seed for the audit.

### Audit report
#### `/audit/report`

Display final audit report, if available.

### Audit Round
#### `/audit/round`

View any existing audit rounds, or start a new one.

### Audit Ballot
#### `/audit/round/ballot`

Execute ballot confirmation for the current ballot.
