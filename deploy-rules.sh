#!/bin/bash

S3_PATH=s3://dev-services/vaclogic/ice.zip

rm -rf /tmp/ice
mkdir -p /tmp/ice
cp -r ice3/opencds-ice-service/src/main/resources/* /tmp/ice
cd /tmp
zip -r ice.zip ice

aws s3 cp /tmp/ice.zip $S3_PATH --acl public-read
