FROM maven:3-eclipse-temurin-21 AS build

COPY . .

RUN mvn clean package

FROM tomcat:11-jre21-temurin-noble

# Metadata
LABEL organization="HLN Consulting, LLC"
LABEL maintainer="Sam Nicolary<sdn@hln.com>"

ENV DEBUG="N"
ENV KM_THREADS="8"
ENV OUTPUT_EARLIEST_OVERDUE_DATES="Y"
ENV ENABLE_DOSE_OVERRIDE_FEATURE="Y"
ENV OUTPUT_SUPPLEMENTAL_TEXT="Y"
ENV ENABLE_UNSUPPORTED_VACCINE_GROUPS="N"
# ENV VACCINE_GROUP_EXCLUSIONS=""
ENV REMOTE_CONFIG_ENABLED="N"
ENV REMOTE_CONFIG_USER="remote_admin"
ENV REMOTE_CONFIG_PASSWORD="password"

# Tomcat Server
EXPOSE 8080

RUN apt-get -y update && \
    apt install dos2unix && \
    adduser --system --home /home/appuser --disabled-login appuser && \
    rm -rf /usr/local/tomcat/webapps/docs /usr/local/tomcat/webapps/ROOT /usr/local/tomcat/webapps/examples /usr/local/tomcat/webapps/host-manager /usr/local/tomcat/webapps/manager && \
    mkdir -p /home/appuser/.opencds

WORKDIR /home/appuser

# copy config files
COPY --from=build target/opencds-ice-service /usr/local/tomcat/webapps/opencds-decision-support-service
COPY --from=build src/main/resources /usr/local/tomcat/webapps/opencds-decision-support-service/opencds-ice-service/src/main/resources
COPY --from=build .gcp.config/opencds.properties .opencds
COPY --from=build .gcp.config/sec.xml .opencds
COPY --from=build .gcp.config/log4j2.xml /usr/local/tomcat/webapps/opencds-decision-support-service/WEB-INF/classes
COPY --from=build .gcp.config/ice.properties /usr/local/tomcat/webapps/opencds-decision-support-service/WEB-INF/classes
COPY --from=build .gcp.config/start-ice.sh ./
COPY --from=build .gcp.config/server.xml /usr/local/tomcat/conf

RUN chown -R appuser: /home/appuser /usr/local/tomcat

RUN dos2unix ./start-ice.sh && chmod a+x ./start-ice.sh

USER appuser

# Run Tomcat server
CMD ["./start-ice.sh", "catalina.sh", "run"]
