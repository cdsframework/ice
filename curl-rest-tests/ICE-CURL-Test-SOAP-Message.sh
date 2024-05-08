#!/bin/bash

curl --header "content-type: text/soap+xml; charset=utf-8" --data @ICE-SOAP-EnvelopePlusCurrentMessage.xml http://localhost:32775/opencds-decision-support-service/evaluate
