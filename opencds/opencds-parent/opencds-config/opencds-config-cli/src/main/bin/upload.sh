#!/bin/bash

CP=.
for j in `/bin/ls lib/*.jar` ; do
    CP=$CP:$j
done;

java -cp $CP -Dlog4j.configuration=resources/log4j.properties org.opencds.config.cli.RestCli --upload "$@"
