FROM tomcat:9-jre8-alpine AS build

# Metadata
LABEL organization="HLN Consulting, LLC"
LABEL maintainer="Sam Nicolary<sdn@hln.com>"

ENV TZ="America/New_York"

# Tomcat Server
EXPOSE 8080

RUN adduser -u 1000 -D appuser && \
    apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/$TZ /etc/timezone && \
    rm -rf /usr/local/tomcat/webapps/docs /usr/local/tomcat/webapps/ROOT /usr/local/tomcat/webapps/examples /usr/local/tomcat/webapps/host-manager /usr/local/tomcat/webapps/manager && \
    mkdir -p /home/appuser/.opencds

WORKDIR /home/appuser

# copy config files
COPY opencds/opencds-parent/opencds-decision-support-service/target/opencds-decision-support-service-2.0.5 /usr/local/tomcat/webapps/opencds-decision-support-service
COPY ice3/opencds-ice-service-data/src /usr/local/tomcat/webapps/opencds-decision-support-service/opencds-ice-service-data/src
COPY ice3/opencds-ice-service-data/META-INF /usr/local/tomcat/webapps/opencds-decision-support-service/opencds-ice-service-data/META-INF
COPY docker/opencds.properties .opencds
COPY docker/sec.xml .opencds
COPY docker/log4j.xml /usr/local/tomcat/webapps/opencds-decision-support-service/WEB-INF/classes
COPY docker/ice.properties /usr/local/tomcat/webapps/opencds-decision-support-service/WEB-INF/classes

RUN chown -R appuser: /home/appuser /usr/local/tomcat

USER appuser

# Run Tomcat server
CMD ["catalina.sh", "run"]
