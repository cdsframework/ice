FROM tomcat:10-jre17-temurin-noble AS build

# Metadata
LABEL organization="HLN Consulting, LLC"
LABEL maintainer="Sam Nicolary<sdn@hln.com>"

ENV DEBUG="N"
ENV KM_THREADS="8"
ENV OUTPUT_EARLIEST_OVERDUE_DATES="Y"
ENV ENABLE_DOSE_OVERRIDE_FEATURE="Y"
ENV OUTPUT_SUPPLEMENTAL_TEXT="Y"
ENV ENABLE_UNSUPPORTED_VACCINE_GROUPS="N"
ENV VACCINE_GROUP_EXCLUSIONS=""
ENV REMOTE_CONFIG_ENABLED="N"
ENV REMOTE_CONFIG_USER="remote_admin"
ENV REMOTE_CONFIG_PASSWORD="password"

# Tomcat Server
EXPOSE 8080

RUN apt-get -y update && \
    apt-get -y dist-upgrade && \
    adduser --system --home /home/appuser --disabled-login appuser && \
    rm -rf /usr/local/tomcat/webapps/docs /usr/local/tomcat/webapps/ROOT /usr/local/tomcat/webapps/examples /usr/local/tomcat/webapps/host-manager /usr/local/tomcat/webapps/manager && \
    mkdir -p /home/appuser/.opencds

WORKDIR /home/appuser

# copy config files
COPY opencds-ice-service/target/opencds-ice-service /usr/local/tomcat/webapps/opencds-decision-support-service
COPY opencds-ice-service/src/main/resources /usr/local/tomcat/webapps/opencds-decision-support-service/opencds-ice-service/src/main/resources
COPY .gcp.config/opencds.properties .opencds
COPY .gcp.config/sec.xml .opencds
COPY .gcp.config/log4j2.xml /usr/local/tomcat/webapps/opencds-decision-support-service/WEB-INF/classes
COPY .gcp.config/ice.properties /usr/local/tomcat/webapps/opencds-decision-support-service/WEB-INF/classes
COPY .gcp.config/start-ice.sh ./
COPY .gcp.config/server.xml /usr/local/tomcat/conf

RUN chown -R appuser: /home/appuser /usr/local/tomcat

USER appuser

# Run Tomcat server
CMD ["./start-ice.sh", "catalina.sh", "run"]
