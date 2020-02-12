FROM tomcat:9-jre8-alpine AS build

# Metadata
LABEL organization="HLN Consulting, LLC"
LABEL maintainer="Sam Nicolary<sdn@hln.com>"

ENV DEBUG="N"
ENV KM_THREADS="8"
ENV OUTPUT_EARLIEST_OVERDUE_DATES="Y"
ENV ENABLE_DOSE_OVERRIDE_FEATURE="Y"
ENV OUTPUT_SUPPLEMENTAL_TEXT="Y"
ENV REMOTE_CONFIG_ENABLED="N"
ENV REMOTE_CONFIG_USER="remote_admin"
ENV REMOTE_CONFIG_PASSWORD="password"

# Tomcat Server
EXPOSE 8080
EXPOSE 8009

RUN adduser -u 1000 -D appuser && \
    rm -rf /usr/local/tomcat/webapps/docs /usr/local/tomcat/webapps/ROOT /usr/local/tomcat/webapps/examples /usr/local/tomcat/webapps/host-manager /usr/local/tomcat/webapps/manager && \
    mkdir -p /home/appuser/.opencds

WORKDIR /home/appuser

# copy config files
COPY opencds/opencds-parent/opencds-decision-support-service/target/opencds-decision-support-service-2.0.5 /usr/local/tomcat/webapps/opencds-decision-support-service
COPY ice3/opencds-ice-service/src/main/knowledge /usr/local/tomcat/webapps/opencds-decision-support-service/opencds-ice-service/src/main/knowledge
COPY ice3/opencds-ice-service/META-INF /usr/local/tomcat/webapps/opencds-decision-support-service/opencds-ice-service/META-INF
COPY docker/opencds.properties .opencds
COPY docker/sec.xml .opencds
COPY docker/log4j.xml /usr/local/tomcat/webapps/opencds-decision-support-service/WEB-INF/classes
COPY docker/ice.properties /usr/local/tomcat/webapps/opencds-decision-support-service/WEB-INF/classes
COPY docker/start-ice.sh ./

RUN chown -R appuser: /home/appuser /usr/local/tomcat && \
    rm -f /usr/local/tomcat/webapps/opencds-decision-support-service/WEB-INF/classes/log4j.properties && \
    rm -f /usr/local/tomcat/webapps/opencds-decision-support-service/opencds-ice-service/src/main/knowledge/knowledgeCommon/org.cdsframework^ICE^1.0.0/*.dsl && \
    rm -f /usr/local/tomcat/webapps/opencds-decision-support-service/opencds-ice-service/src/main/knowledge/knowledgeCommon/org.cdsframework^ICE^1.0.0/*.bpmn && \
    rm -f /usr/local/tomcat/webapps/opencds-decision-support-service/opencds-ice-service/src/main/knowledge/knowledgeCommon/org.cdsframework^ICE^1.0.0/*.drl && \
    rm -f /usr/local/tomcat/webapps/opencds-decision-support-service/opencds-ice-service/src/main/knowledge/knowledgeCommon/org.cdsframework^ICE^1.0.0/*.dslr && \
    rm -f /usr/local/tomcat/webapps/opencds-decision-support-service/opencds-ice-service/src/main/knowledge/knowledgeModules/gov.nyc.cir^ICE^1.0.0/*.drl && \
    rm -f /usr/local/tomcat/webapps/opencds-decision-support-service/opencds-ice-service/src/main/knowledge/knowledgeModules/gov.nyc.cir^ICE^1.0.0/*.dslr

USER appuser

# Run Tomcat server
CMD ["./start-ice.sh", "catalina.sh", "run"]
