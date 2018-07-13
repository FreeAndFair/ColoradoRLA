#!/bin/sh

set -e

psql -v ON_ERROR_STOP=1 \
     --username "$POSTGRES_USER" \
     --dbname "$POSTGRES_DB" \
<<EOSQL
CREATE USER corla WITH PASSWORD 'corla';
CREATE DATABASE corla;
GRANT ALL PRIVILEGES ON DATABASE corla TO corla;
EOSQL
