curl -f -s --location --request POST "${ICE_BASE_URI}/opencds-decision-support-service/api/resources/evaluate" \
--header 'Content-Type: application/json' \
--header 'Accept: application/json' \
--data-binary '@rest-test-json-evalue.dat'
