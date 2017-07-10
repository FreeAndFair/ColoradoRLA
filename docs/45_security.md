\pagebreak

Security
========

This system will be hosted in CDOS data centers. In this document, we
will therefore only explicitly address issues not implicitly addressed
by this choice. Concretely, due to planned hosting in CDOS data
centers, all of the following items are implicitly addressed:

* System fault-tolerance, redundant hosting, and fail-over
* GeoIP blocking, IP whitelisting/blacklisting
* Web application firewalling
* Web application penetration testing and vulnerability scanning
* Distributed denial-of-service prevention
* Anti-malware scanning and other host protections, such as file and
  configuration integrity monitoring
* Centralized logging
* Network segmentation
* Systems administration and remote access
* We now address the remaining security requirements.

User management and controls
----------------------------

Upon user registration, users will be prompted to create a password
conforming to the requirements listed in [CO-RLA-DQ, 13]. For
authentication and authorization we will either integrate with the
existing CDOS User Management System or implement the following best
practices. 

Passwords will be salted and stretched using a secure key-derivation
function such as PBKDF2 or Argon2, which can be tuned to increase the
work factor required for password guess attempts. 

Two-factor authentication will be provided via the Time-Based One-Time
Password (TOTP) algorithm or another two-factor scheme negotiated with
the client.

Policies around password expiry, access attempt controls, and session
duration limits will be enforced by the application in accordance with
[CO-RLA-DQ, 13] or more recent NIST standards, as negotiated with the
client.

User provisioning and password management will be performed by Free &
Fair, coordinated with the state.

System operations, security, and privacy
----------------------------------------

Free & Fair engineers are experts in secure software engineering
practices, and regular clients include the Department of Defense and
the United States intelligence community. All software will undergo a
documented security review before production release, and will be
specifically audited against the OWASP Top 10. In particular, the RLA
application will satisfy the Colorado State OIT Secure Applications
Coding Standard as described in [TS-CISO-006].

Systems hardening and protection
--------------------------------

Free & Fair-delivered systems will be hardened according to best
practices. Any sensitive data, including personally-identifiable
information (PII), will be stored encrypted. Application-level logging
will be implemented using syslog and standard Java logging mechanisms,
which will then be aggregated by CDOS-provided centralized logging,
using NTP for time synchronization. We will build the system with the
security design principles we have used for years for Department of
Defense and U.S. intelligence community clients.
