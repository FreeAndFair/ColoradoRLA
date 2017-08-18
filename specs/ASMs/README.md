ASMs
====

This directory contains specifications, using the standard abbreviated
visual notation for Abstract State Machines (ASMs), of client/UI and
server ASMs for the Colorado RLA Tool.

The specification of all ASMs is found in [the formal specification
of the RLA Tool](https://github.com/FreeAndFair/ColoradoRLA/blob/master/specs/pvs/corla_model.pdf).

Client/UI ASMs
--------------

Client ASMs document the states that the UI can pass through, and the
legal transitions between those states. Note that a number of states
in these ASMs have to do with potential error states during, e.g., CVR
file uploads and similar.

There are four Client/UI ASMs: one for each kind of dashboard
(Department of State, County, and Audit Board) and one that summarizes
how those three ASMs are composed together into the overall state
machine for the RLA Tool web client.

![The Department of State Dashboard ASM](DOS_Dashboard_ASM.pdf)

![The County Dashboard ASM](County_Dashboard_ASM.pdf)

![The Audit Board Dashboard ASM](Audit_Board_Dashboard_ASM.pdf)

![The RLA Tool ASM](RLA_Tool_ASM.pdf)

The implementation of these ASMs is found in
the
[Client of the RLA tool](https://github.com/FreeAndFair/ColoradoRLA/tree/master/client/src/component).

Server ASMs
-----------

*The visual rendering of server ASMs will be included in either the
phase-2 or phase-3 deliverable.*

The implementation server ASMs is found in the
[us.freeandfair.corla.asm package](https://github.com/FreeAndFair/ColoradoRLA/tree/master/server/eclipse-project/src/main/java/us/freeandfair/corla/asm).
