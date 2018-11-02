## Pinned to 3.5.3 to work around https://github.com/carlossg/docker-maven/issues/92
FROM maven:3.5.3 as maven

COPY server/eclipse-project /srv/corla/server/eclipse-project

WORKDIR /srv/corla/server/eclipse-project

RUN mvn clean
RUN mvn package

FROM openjdk:8
LABEL maintainer="Democracy Works, Inc. <dev@democracy.works>"

RUN wget https://www.yourkit.com/download/docker/YourKit-JavaProfiler-2018.04-docker.zip -P /tmp/ && \
  unzip /tmp/YourKit-JavaProfiler-2018.04-docker.zip -d /usr/local && \
  rm /tmp/YourKit-JavaProfiler-2018.04-docker.zip

## TODO: How can we architect this such that passing -Dthe.prop=value works as
## expected?
COPY docker/server/docker.properties /srv/corla/docker.properties
COPY --from=maven /srv/corla/server/eclipse-project/target/corla-server-*-shaded.jar \
     /srv/corla/corla.jar

EXPOSE 10001

CMD ["java", \
     "-agentpath:/usr/local/YourKit-JavaProfiler-2018.04/bin/linux-x86-64/libyjpagent.so=port=10001,listen=all", \
     "-jar", "/srv/corla/corla.jar", \
     "/srv/corla/docker.properties"]
