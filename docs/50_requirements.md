\pagebreak

Requirements
============

This document contains the requirements of the specification of the
ColoradoRLA system.

History
-------

* Outline and first draft, 5 July 2017 by Joe Kiniry.
* Second draft, 6 July 2017 by Joe Kiniry. Still no refined system
  requirements above and beyond those in the [Documented Quote](http://bcn.boulder.co.us/~neal/elections/corla/DQ_RLA_0500517_FE_KRT_VAAA_2017-1420.pdf) 
  issued by the Colorado Department of State (DQ).
* Third draft, in progress August 2017, by Stephanie Singer, based
  on the final Rule 25 promulgated by the Colorado Department of State

Mandatory Requirements
----------------------

### Mandatory Behavioral Requirements

* Ballot manifests and cast vote records (CVRs) will be uploaded to
  the server via HTTPS. 
  
* The status of uploaded data will be summarized in a state-wide
  dashboard, along with information on which counties have not yet
  uploaded their CVRs, and uploads that have formatting or content
  issues. The status of data, and results as audits are performed,
  will be provided for each contest to be audited.
  
* Random selections of ballots for performing a ballot-level
  comparison risk limiting audit will be automatically generated based
  on the provided random seed using the SHA-256-based pseudo-random
  number generator specified in the DQ, as well as the computed
  contest margins and other indicated parameters including any
  discrepancies found.
  
* A county view of the audit will display information on the progress
  of each audited contest in the county, with a summary of
  discrepancies.
  
* We will tailor view/edit permissions for each screen of information
  to users with appropriate authorizations as defined by the state.
  
* Public access to appropriate data and reports will be provided in
  standard file formats.

### Mandatory Non-Behavioral Requirements

We summarize our approaches to three critical non-behavioral
properties of this system below: how we achieve *fault tolerance*, how
we perform *synchronization*, and some reflections on the *dynamism of
the UI and user experience (UX)*.

#### Fault tolerance

We have built many fault tolerant distributed systems over the
years. For example, Dr. Kiniry was co-architect of Sprint’s Internet
Service Provider product (what later became a part of Earthlink)
in 1995. While the underlying platforms and technology have evolved
several times since then, the underlying principles remain the same. 

For this particular application, we propose a two server deployment,
preferably in separate locations which have independent power
subsystems and network backbones. Each server will have two power
supplies and two network cards, which should be on separate,
independent subnets. Servers will have hot-swappable SSDs in a RAID 5
configuration for local data redundancy and fault tolerance.

#### Synchronization

We can design a distributed synchronization protocol in either a
primary/secondary architecture (with support for dynamic failover in
the case of network or system unavailability) or in a peer-to-peer
configuration, where inbound requests can go to either server using
DNS load balancing. We can also use a distributed synchronization
mechanism already integrated into the client-selected backend database
system, if that is an appropriate choice. The decision of what
synchronization protocol to use will be made in consultation with the
client.

#### UI and UX Dynamism

Rather than simply create a plain-old-HTML front-end, our UI and UX
will use rich JavaScript UI libraries to create a browser-based user
experience that feels like a modern application one might find in any
of the mainstream online app stores. Our UX expert will work with the
client to ensure that the UI’s dynamism and presentation facilitates
OpenRLA’s critical users, election officials running audits.

Secondary Requirements
----------------------

* This system will be hosted in the CDOS data center, which will support
  failover, as described in the Documented Quote [CO-RLA-DQ, p. 16]. 

* Our system includes all necessary features for fault tolerance
  summarized earlier, including a redundant standby server with
  separate network cards and power supplies, dual CPUs, and hard
  drives in a RAID 5 configuration.

* We expect regular backups to be configured to use CDOS Disaster
  Recovery facility.

* Ensure business continuity of the system with at least four nines
  (99.99%) availability.
