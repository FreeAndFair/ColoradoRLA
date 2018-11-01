FROM postgres:9.6
LABEL maintainer="Democracy Works, Inc. <dev@democracy.works>"

COPY docker/postgresql/init-corla-db.sh /docker-entrypoint-initdb.d/init-corla-db.sh
COPY docker/postgresql/postgresql.conf /etc/postgresql
COPY test/corla-test-credentials.psql /root/corla-test-credentials.psql

# Override default postgres config file
CMD ["postgres", "-c", "config_file=/etc/postgresql/postgresql.conf"]
