FROM tomcat:11-jre21-temurin-noble

EXPOSE 8080

RUN rm -rf /usr/local/tomcat/webapps/docs /usr/local/tomcat/webapps/ROOT /usr/local/tomcat/webapps/examples /usr/local/tomcat/webapps/host-manager /usr/local/tomcat/webapps/manager

COPY target/opencds-decision-support-service.war /usr/local/tomcat/webapps

