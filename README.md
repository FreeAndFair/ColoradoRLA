# Colorado Risk-Limiting Audit (RLA) Tool

[![Build Status](https://travis-ci.org/democracyworks/ColoradoRLA.svg?branch=master)](https://travis-ci.org/democracyworks/ColoradoRLA)

The Colorado RLA Tool is designed to help local and state election officials conduct efficient and effective risk-limiting audits of their elections. The initial code was developed by the Colorado Department of State through a contract with Free & Fair in 2017, and is now being developed and maintained by [Democracy Works](https://democracy.works), a 501(c)3 nonpartisan, nonprofit organization.

# Objectives

- State and county election officials are able to successfully conduct a statewide risk-limiting audit of their election

- Election administrators and citizen Audit Boards find the RLA Tool easy to use and helpful in conducting the audit

- Public observers have increased confidence in the electoral outcomes as a result of the risk-limiting audit

- The RLA tool is reliable, scalable, and performant

# Description

The RLA Tool is designed to facilitate a statistically valid audit of vote tabulation processes by comparing the votes marked on a random sample of original paper ballots with the electronically recorded votes for those same ballots.

The RLA Tool:
1) Calculates how many original paper ballots need to be audited for the targeted contest(s)

2) randomly selects which original paper ballots will be audited and creates lists to help local election officials find the necessary ballots in storage,

3) provides an interface for Audit Board teams to record the votes they see marked on the original paper ballot(s),

4) checks whether the audited votes and recorded votes for each ballot match, and determines at the end of the audit round whether the desired confidence interval has been achieved based on these results (if not, additional ballots are randomly selected and audited)

5) provides metrics and monitoring capabilities for election officials & public observers that indicate the progress and outcome of the audit.

## What is a risk-limiting audit?

A [risk-limiting audit](https://en.wikipedia.org/wiki/Risk-limiting_audit) is an audit of the results of an election which uses statistical methods to give high confidence that the winner(s) of the election were reported correctly.

In Colorado, citizen Audit Boards examine a random sample of original paper ballots from an election, comparing the votes marked on each original paper ballot with the electronic representation of votes recorded by the vote tabulation system. Under most circumstances, this method requires auditing far fewer ballots than a full hand recount or fixed-percentage audit, while also providing strong statistical evidence that the outcome of the election was correct.

# Docker Quick Start

Primarily used to spin up the system for development in a controlled way. This
is a work in progress but is usable.

## Requirements

- [`docker`](https://docs.docker.com/install/)
- [`docker-compose`](https://docs.docker.com/compose/)

## Setup

This step is optional the first time, but you need to run it when you have new
code changes you want to incorporate. You can pass specific services to
`docker-compose build` if you don’t want to rebuild everything.

```sh
docker-compose build
```

## Running

Assuming you have built images, you can bring up the system with those images:

```sh
docker-compose up
```

The application frontend will then be accessible at **`localhost:8080`**.

Once the system is running, the server will create the PostgreSQL schema. After
this, you most likely want to install test credentials, which are already inside
the PostgreSQL image:

```sh
docker-compose exec postgresql \
  /bin/bash -c \
  'psql -U corla -d corla < /root/corla-test-credentials.psql'
```

With the test credentials loaded, you should be able to log in as a state
administrator using `stateadmin1` as the username with any password, and as a
county administrator with `countyadmin1` as the username along with any
password. There are other usernames, especially for the counties (`countyadmin1`
maps to a specific county). You may be able to use this file as a hint for the
others:
`server/eclipse-project/src/main/resources/us/freeandfair/corla/county_ids.properties`

# Tests

Unit tests can be run from the command line:

```sh
mvn test
```

By default, integration tests requiring a database are excluded. To avoid
excluding those tests, you can override the excluded groups from the command
line:

```sh
mvn test -Dcorla.test.excludedGroups=""
```

# Installation and Use

A document describing how to download, install, and use this system is
found in [the docs directory](docs/15_installation.md).

# System Documentation

Documentation about this project and the Colorado RLA system includes:
* a [User Manual (docx)](docs/user_manual.docx)
  with an overview of the system,
* a [County Run Book (docx)](docs/county_runbook.docx) and
  [State Run Book (docx)](docs/sos_runbook.docx) for system users,
* a [description of our development process and methodology](docs/35_methodology.md),
* a [developer document](docs/25_developer.md) that contains our
  developer instructions, including the project history, technologies
  in use, dependencies, how to build the system, how we perform
  quality assurance, how we perform validation and verification, and
  what the build status of the project is,
* the [system requirements](docs/50_requirements.md),
* the [formal system specification](docs/55_specification.md),
* the [means by which we validate and verify the system](docs/40_v_and_v.md),
* a [glossary](docs/89_glossary.md) of the domain terminology used in
  the system,
* a full [bibliography](docs/99_bibliography.md) is available.
* a [document describing how we perform project management](docs/30_project_management.md),
* the [license](LICENSE.md) under which this software is made available,
  and
* all [contributors](#contributors) to the design and development of
  this system are listed below.

# Contributors

* [Democracy Works](https://democracy.works)
* [Free & Fair](https://http://freeandfair.us)
* [Colorado Department of State](https://www.sos.state.co.us/pubs/elections/auditCenter.html)
* [Colorado County Clerks Association](www.clerkandrecorder.org/)
* Special thanks also to Philip Stark, Ron Rivest, Mark Lindeman, and others in the State Audit Working Group and RLA Representative Group for their work to develop and refine risk-limiting audits
