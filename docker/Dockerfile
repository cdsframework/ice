FROM tomcat:9-jre8-alpine AS build

# Metadata
LABEL organization="HLN Consulting, LLC"
LABEL maintainer="Sam Nicolary<sdn@hln.com>"

ENV TZ="America/New_York"
ENV DEBUG="N"
ENV KM_THREADS="2"
ENV OUTPUT_EARLIEST_OVERDUE_DATES="Y"
ENV ENABLE_DOSE_OVERRIDE_FEATURE="Y"
ENV REMOTE_CONFIG_ENABLED="N"
ENV REMOTE_CONFIG_USER="remote_admin"
ENV REMOTE_CONFIG_PASSWORD="password"

# Tomcat Server
EXPOSE 8080
EXPOSE 8009

RUN adduser -u 1000 -D appuser && \
    apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/$TZ /etc/timezone && \
    rm -rf /usr/local/tomcat/webapps/docs /usr/local/tomcat/webapps/ROOT /usr/local/tomcat/webapps/examples /usr/local/tomcat/webapps/host-manager /usr/local/tomcat/webapps/manager && \
    mkdir -p /home/appuser/.opencds

WORKDIR /home/appuser

# copy config files
COPY opencds/opencds-parent/opencds-decision-support-service/target/opencds-decision-support-service-2.0.5 /usr/local/tomcat/webapps/opencds-decision-support-service
COPY ice3/opencds-ice-service/src /usr/local/tomcat/webapps/opencds-decision-support-service/opencds-ice-service/src
COPY ice3/opencds-ice-service/META-INF /usr/local/tomcat/webapps/opencds-decision-support-service/opencds-ice-service/META-INF
COPY docker/opencds.properties .opencds
COPY docker/sec.xml .opencds
COPY docker/log4j.xml /usr/local/tomcat/webapps/opencds-decision-support-service/WEB-INF/classes
COPY docker/ice.properties /usr/local/tomcat/webapps/opencds-decision-support-service/WEB-INF/classes
COPY docker/start-ice.sh ./

RUN chown -R appuser: /home/appuser /usr/local/tomcat && \
    rm /usr/local/tomcat/webapps/opencds-decision-support-service/WEB-INF/classes/log4j.properties

USER appuser

# Run Tomcat server
CMD ["./start-ice.sh", "catalina.sh", "run"]
