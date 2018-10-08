FROM node:8 as node

COPY client /srv/corla/client
RUN cd /srv/corla/client && npm install && npm run dist

FROM httpd:2.4
LABEL maintainer="Democracy Works, Inc. <dev@democracy.works>"

COPY docker/httpd/httpd.conf /usr/local/apache2/conf/
COPY docker/httpd/corla.conf /usr/local/apache2/conf/extra/
COPY --from=node /srv/corla/client/dist /srv/corla/client/dist
