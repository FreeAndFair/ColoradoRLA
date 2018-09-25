FROM postgres:9.2
LABEL maintainer="Democracy Works, Inc. <dev@democracy.works>"

COPY docker/postgresql/init-corla-db.sh /docker-entrypoint-initdb.d/init-corla-db.sh
COPY test/corla-test-credentials.psql /root/corla-test-credentials.psql
