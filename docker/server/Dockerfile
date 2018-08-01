FROM maven:3 as maven

COPY server/eclipse-project /srv/corla/server/eclipse-project
RUN cd /srv/corla/server/eclipse-project && mvn package

FROM openjdk:8
LABEL maintainer="Democracy Works, Inc. <dev@democracy.works>"

ARG VERSION

## TODO: How can we architect this such that passing -Dthe.prop=value works as
## expected?
COPY docker/server/docker.properties /srv/corla/docker.properties
COPY --from=maven /srv/corla/server/eclipse-project/target/colorado_rla-${VERSION}-shaded.jar \
     /srv/corla/corla.jar

CMD ["java", \
     "-jar", "/srv/corla/corla.jar", \
     "/srv/corla/docker.properties"]
