#!/bin/bash

CP=.
for j in `/bin/ls lib/*.jar` ; do
	CP=$CP:$j
done;

java -cp $CP org.opencds.config.migrate.ConfigMigrator "$@"
