#!/bin/bash

awslocal sns create-topic --name pip-app-analytics
awslocal sns create-topic --name pipcs-registration-topic
awslocal sns create-topic --name workflow-topic