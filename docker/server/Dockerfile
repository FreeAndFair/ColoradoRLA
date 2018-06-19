FROM openjdk:8
LABEL maintainer="Democracy Works, Inc. <dev@democracy.works>"

ARG VERSION=1.1.0

## TODO: How can we architect this such that passing -Dthe.prop=value works as
## expected?
COPY docker/server/docker.properties /srv/corla/docker.properties
COPY server/eclipse-project/target/colorado_rla-${VERSION}-shaded.jar \
     /srv/corla/corla.jar

CMD ["java", \
     "-jar", "/srv/corla/corla.jar", \
     "/srv/corla/docker.properties"]
