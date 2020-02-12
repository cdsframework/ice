#!/bin/bash

if [ -n $DEBUG ]; then
    echo "DEBUG is set to: $DEBUG"
    if [ "$DEBUG" == "Y" ]; then
        echo "Turning debug on..."
        sed -i 's/info/debug/' /usr/local/tomcat/webapps/opencds-decision-support-service/WEB-INF/classes/log4j.xml
    fi
fi

if [ -n $TZ ]; then
    echo "TZ is set to: $TZ"
    export JAVA_OPTS="$JAVA_OPS -Duser.timezone=$TZ"
fi

if [ -n $KM_THREADS ]
then
    echo "Setting km.threads in opencds.properties to: $KM_THREADS"
    sed -i "s/km.threads=.*/km.threads=$KM_THREADS/" .opencds/opencds.properties
fi

if [ -n $OUTPUT_EARLIEST_OVERDUE_DATES ]
then
    echo "Setting output_earliest_and_overdue_dates in ice.properties to: $OUTPUT_EARLIEST_OVERDUE_DATES"
    sed -i "s/output_earliest_and_overdue_dates=.*/output_earliest_and_overdue_dates=$OUTPUT_EARLIEST_OVERDUE_DATES/" /usr/local/tomcat/webapps/opencds-decision-support-service/WEB-INF/classes/ice.properties
fi

if [ -n $ENABLE_DOSE_OVERRIDE_FEATURE ]
then
    echo "Setting enable_dose_override_feature in ice.properties to: $ENABLE_DOSE_OVERRIDE_FEATURE"
    sed -i "s/enable_dose_override_feature=.*/enable_dose_override_feature=$ENABLE_DOSE_OVERRIDE_FEATURE/" /usr/local/tomcat/webapps/opencds-decision-support-service/WEB-INF/classes/ice.properties
fi

if [ -n $OUTPUT_SUPPLEMENTAL_TEXT ]
then
    echo "Setting output_supplemental_text in ice.properties to: $OUTPUT_SUPPLEMENTAL_TEXT"
    sed -i "s/output_supplemental_text=.*/output_supplemental_text=$OUTPUT_SUPPLEMENTAL_TEXT/" /usr/local/tomcat/webapps/opencds-decision-support-service/WEB-INF/classes/ice.properties
fi

if [ -n $REMOTE_CONFIG_ENABLED ]; then
    echo "REMOTE_CONFIG_ENABLED is set to: $REMOTE_CONFIG_ENABLED"
    if [ "$REMOTE_CONFIG_ENABLED" == "Y" ]; then
        echo "Turning remote configuration on - make sure you have the user and password set via the REMOTE_CONFIG_USER and REMOTE_CONFIG_PASSWORD environment variables."
        sed -i "s/<enabled>.*<\/enabled>/<enabled>true<\/enabled>/" .opencds/sec.xml
    fi
fi

if [ -n $REMOTE_CONFIG_USER ]; then
    echo "Setting username in sec.xml to: $REMOTE_CONFIG_USER"
    sed -i "s/<username>.*<\/username>/<username>$REMOTE_CONFIG_USER<\/username>/" .opencds/sec.xml
    unset REMOTE_CONFIG_USER
fi

if [ -n $REMOTE_CONFIG_PASSWORD ]; then
    echo "Setting password in sec.xml to the configured value."
    sed -i "s/<password>.*<\/password>/<password>$REMOTE_CONFIG_PASSWORD<\/password>/" .opencds/sec.xml
    unset REMOTE_CONFIG_PASSWORD
fi

. $@
