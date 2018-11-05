# FIXME: do pip setup.py sdist or other install from source
FROM python:2.7
LABEL maintainer="Democracy Works, Inc. <dev@democracy.works>"
WORKDIR /srv/reports
ARG tarball=rla_export.tar.gz
COPY docker.properties ./
COPY rla_export-2.0.7.tar.gz ${tarball}
RUN pip install --no-cache-dir ${tarball}
CMD ["rla_export", "-p", "docker.properties", "-v"]
