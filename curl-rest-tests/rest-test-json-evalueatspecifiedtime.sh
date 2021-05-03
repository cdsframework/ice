curl -f -s --location --request POST "${ICE_BASE_URI}/opencds-decision-support-service/api/resources/evaluateAtSpecifiedTime" \
--header 'Content-Type: application/json' \
--header 'Accept: application/json' \
--data-binary '@rest-test-json-evalueatspecifiedtime.dat'
