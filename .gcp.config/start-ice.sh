#!/bin/bash

if [[ -v DEBUG ]]; then
    echo "DEBUG is set to: $DEBUG"
    if [ "$DEBUG" == "Y" ]; then
        echo "Turning debug on..."
        sed -i 's/info/debug/' /usr/local/tomcat/webapps/opencds-decision-support-service/WEB-INF/classes/log4j.xml
    fi
fi

PRE_EVALUATE_UUID=$(cat /proc/sys/kernel/random/uuid)
echo "Setting system property preEvaluateUuid to: $PRE_EVALUATE_UUID"
export JAVA_OPTS="$JAVA_OPTS -DpreEvaluateUuid=$PRE_EVALUATE_UUID"

if [[ -v PRE_EVALUATE_HOOK_TYPE ]]
then
    echo "Setting system property preEvaluateHookType to: $PRE_EVALUATE_HOOK_TYPE"
    export JAVA_OPTS="$JAVA_OPTS -DpreEvaluateHookType=$PRE_EVALUATE_HOOK_TYPE"
fi

if [[ -v PRE_EVALUATE_HOOK_URI ]]
then
    echo "Setting system property preEvaluateHookUri to: $PRE_EVALUATE_HOOK_URI"
    export JAVA_OPTS="$JAVA_OPTS -DpreEvaluateHookUri=$PRE_EVALUATE_HOOK_URI"
fi

if [[ -v TZ ]]; then
    echo "TZ is set to: $TZ"
    export JAVA_OPTS="$JAVA_OPTS -Duser.timezone=$TZ"
fi

if [[ -v KM_THREADS ]]
then
    echo "Setting km.threads in opencds.properties to: $KM_THREADS"
    sed -i "s/km.threads=.*/km.threads=$KM_THREADS/" .opencds/opencds.properties
fi

if [[ -v OUTPUT_EARLIEST_OVERDUE_DATES ]]
then
    echo "Setting output_earliest_and_overdue_dates in ice.properties to: $OUTPUT_EARLIEST_OVERDUE_DATES"
    sed -i "s/output_earliest_and_overdue_dates=.*/output_earliest_and_overdue_dates=$OUTPUT_EARLIEST_OVERDUE_DATES/" /usr/local/tomcat/webapps/opencds-decision-support-service/WEB-INF/classes/ice.properties
fi

if [[ -v ENABLE_DOSE_OVERRIDE_FEATURE ]]
then
    echo "Setting enable_dose_override_feature in ice.properties to: $ENABLE_DOSE_OVERRIDE_FEATURE"
    sed -i "s/enable_dose_override_feature=.*/enable_dose_override_feature=$ENABLE_DOSE_OVERRIDE_FEATURE/" /usr/local/tomcat/webapps/opencds-decision-support-service/WEB-INF/classes/ice.properties
fi

if [[ -v OUTPUT_SUPPLEMENTAL_TEXT ]]
then
    echo "Setting output_supplemental_text in ice.properties to: $OUTPUT_SUPPLEMENTAL_TEXT"
    sed -i "s/output_supplemental_text=.*/output_supplemental_text=$OUTPUT_SUPPLEMENTAL_TEXT/" /usr/local/tomcat/webapps/opencds-decision-support-service/WEB-INF/classes/ice.properties
fi

if [[ -v ENABLE_UNSUPPORTED_VACCINE_GROUPS ]]
then
    echo "Setting enable_unsupported_vaccines_group in ice.properties to: $ENABLE_UNSUPPORTED_VACCINE_GROUPS"
    sed -i "s/enable_unsupported_vaccines_group=.*/enable_unsupported_vaccines_group=$ENABLE_UNSUPPORTED_VACCINE_GROUPS/" /usr/local/tomcat/webapps/opencds-decision-support-service/WEB-INF/classes/ice.properties
fi

if [[ -v VACCINE_GROUP_EXCLUSIONS ]]
then
    echo "Setting output_supplemental_text in ice.properties to: $VACCINE_GROUP_EXCLUSIONS"
    sed -i "s/vaccine_group_exclusions=.*/vaccine_group_exclusions=$VACCINE_GROUP_EXCLUSIONS/" /usr/local/tomcat/webapps/opencds-decision-support-service/WEB-INF/classes/ice.properties
fi

if [[ -v DISABLE_COVID19_SEP2023_DOSE_NUMBER_RESET ]]
then
    echo "Setting disable_covid19_sep2023_dose_number_reset in ice.properties to: $DISABLE_COVID19_SEP2023_DOSE_NUMBER_RESET"
    sed -i "s/disable_covid19_sep2023_dose_number_reset=.*/disable_covid19_sep2023_dose_number_reset=$DISABLE_COVID19_SEP2023_DOSE_NUMBER_RESET/" /usr/local/tomcat/webapps/opencds-decision-support-service/WEB-INF/classes/ice.properties
fi

if [[ -v REMOTE_CONFIG_ENABLED ]]; then
    echo "REMOTE_CONFIG_ENABLED is set to: $REMOTE_CONFIG_ENABLED"
    if [ "$REMOTE_CONFIG_ENABLED" == "Y" ]; then
        echo "Turning remote configuration on - make sure you have the user and password set via the REMOTE_CONFIG_USER and REMOTE_CONFIG_PASSWORD environment variables."
        sed -i "s/<enabled>.*<\/enabled>/<enabled>true<\/enabled>/" .opencds/sec.xml
    fi
fi

if [[ -v REMOTE_CONFIG_USER ]]; then
    echo "Setting username in sec.xml to: $REMOTE_CONFIG_USER"
    sed -i "s/<username>.*<\/username>/<username>$REMOTE_CONFIG_USER<\/username>/" .opencds/sec.xml
    unset REMOTE_CONFIG_USER
fi

if [[ -v REMOTE_CONFIG_PASSWORD ]]; then
    echo "Setting password in sec.xml to the configured value."
    sed -i "s/<password>.*<\/password>/<password>$REMOTE_CONFIG_PASSWORD<\/password>/" .opencds/sec.xml
    unset REMOTE_CONFIG_PASSWORD
fi

if [[ -v RULE_TASK_FIRELIMIT ]]
then
    echo "Setting system property org.jbpm.rule.task.firelimit to: $RULE_TASK_FIRELIMIT"
    export JAVA_OPTS="$JAVA_OPTS -Dorg.jbpm.rule.task.firelimit=$RULE_TASK_FIRELIMIT"
fi

echo JAVA_OPTS=$JAVA_OPTS

. $@
