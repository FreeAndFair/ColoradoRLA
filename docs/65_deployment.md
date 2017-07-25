\pagebreak

Deployment Considerations 
=========================

The ColoradoRLA system is conceptually a 3-tier system (browser-based
TypeScript client, server-side Java application, database). However,
because it is relatively lightweight, it can be deployed in many
configurations ranging from a single machine hosting all 3 tiers to
multiple redundant servers hosting each tier. Because the system stores
state only in the database (and on the user's local machine, in the
browser-based client), load can be balanced across the client layer and
the Java application layer using basic techniques such as DNS
round-robin and load-balancing front-end servers. Balancing load across
the database layer, however, requires ensuring database consistency in
one of various ways.

High Availability 
-----------------

The ColoradoRLA system deployment for the Colorado Department of State
has a high availability requirement. Achieving high availability for the
client-side and server-side applications through the use of multiple
servers is straightforward. For the database, high availability requires
some form of replication that ensures data consistency.

We are using the Postgres DBMS for the database layer. Postgres supports
several different types of replication. For the CDOS deployment, our
initial recommendation is to deploy two Postgres servers (one primary
server and one replica) and use Postgres's integrated streaming
replication functionality, with synchronous commits enabled to achieve
group-safe and 1-safe replication. In this configuration, the only
possibility for data loss is if both the primary server and the replica
crash simultaneously, _and_ the database on the primary server is
corrupted at the same time. We could opt for a stronger durability
guarantee, achieving 2-safe replication (the only possibility for data
loss is if both servers crash simultaneously in a way that corrupts
both databases), but that would cause longer transaction times. See
https://www.postgresql.org/docs/current/static/warm-standby.html#SYNCHRONOUS-REPLICATION 
and http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.10.140 for 
more information.

