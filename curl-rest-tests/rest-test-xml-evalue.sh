curl -f -s --location --request POST "${ICE_BASE_URI}/opencds-decision-support-service/api/resources/evaluate" \
--header 'Content-Type: application/xml' \
--header 'Accept: application/xml' \
--header 'accept-encoding: gzip, deflate' \
--data-binary '@rest-test-xml-evalue.dat'
