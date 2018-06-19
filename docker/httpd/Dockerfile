FROM httpd:2.4
LABEL maintainer="Democracy Works, Inc. <dev@democracy.works>"

COPY docker/httpd/httpd.conf /usr/local/apache2/conf/
COPY docker/httpd/corla.conf /usr/local/apache2/conf/extra/
COPY client/dist /srv/corla/client/dist
