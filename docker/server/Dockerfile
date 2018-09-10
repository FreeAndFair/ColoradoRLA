FROM maven:3 as maven

COPY server/eclipse-project /srv/corla/server/eclipse-project

WORKDIR /srv/corla/server/eclipse-project

RUN mvn clean
RUN mvn package

FROM openjdk:8
LABEL maintainer="Democracy Works, Inc. <dev@democracy.works>"

## TODO: How can we architect this such that passing -Dthe.prop=value works as
## expected?
COPY docker/server/docker.properties /srv/corla/docker.properties
COPY --from=maven /srv/corla/server/eclipse-project/target/colorado_rla-*-shaded.jar \
     /srv/corla/corla.jar

CMD ["java", \
     "-jar", "/srv/corla/corla.jar", \
     "/srv/corla/docker.properties"]
