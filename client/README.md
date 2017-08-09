# CORLA Browser Client

## Site map

Though implemented as a single-page browser application, we can use
URL paths to organize the activities provided by the application.
There are two types of users: Secretary of State officials (SSO's) and
county officials (CO's). Different users may see different pages at a
any given URL path, and have distinct site roots. We thus describe
each user type's site map separately.

Unauthenticated users will always be redirected to `/login`. If a
logged-in SOS or County user navigates to `/`, they will be redirected
to `/sos` or `/county`, as appropriate.


### Unauthenticated

#### `/login`

Authenticate as a user of either type. Should include a link to the
public audit dashboard, which is not part of the CORLA browser client.


### SoS Official

| Path | Page |
| ---- | ---- |
| `/sos` | SOS Home |
| `/sos/county` | County Overview |
| `/sos/county/{countyId}`| County Detail |
| `/sos/contest` | Contest Overview |
| `/sos/contest/{contestId}` | Contest Detail |
| `/sos/audit` | Audit |
| `/sos/audit/ballots` | Ballot Selection |
| `/sos/audit/seed` | Seed |


### County Official

| Path | Page |
| ---- | ---- |
| `/county` | County Home
| `/county/contest` | Contest Overview |
| `/county/contest/{contestId}` | Contest Detail |
| `/county/audit` | Run Audit |

### SOS and County Officials

| Path | Page |
| ---- | ---- |
| `/help` | Help Home |
| `/help/glossary` | Glossary |
| `/help/manual` | User Manual |
