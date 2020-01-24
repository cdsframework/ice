#!/bin/bash

export ICE_BASE_URI=http://localhost:8080

RED='\033[1;31m'
GREEN='\033[1;32m'
BLUE='\033[1;34m'
NC='\033[0m'

run_test() {
    TEST=$1
    . ./$TEST > /dev/null
    if [ $? -eq 0 ]
    then
        RESULT="${GREEN}passed"
    else
        RESULT="${RED}failed"
    fi
    echo -e "${BLUE}${TEST}: ${RESULT}${NC}"
}

tz=$(curl -s --location --request GET "${ICE_BASE_URI}/opencds-decision-support-service/api/resources/tz")

echo -e "${BLUE}Server TZ: ${GREEN}${tz}${NC}"

for test in `ls . | grep -v run-all-tests.sh`
do
    run_test $test
done
