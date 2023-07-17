#!/bin/bash

awslocal sqs create-queue --queue-name pip-registration-response

awslocal sqs create-queue --queue-name workflow-request-queue
SUBSCRIPTION_ARN=$(awslocal sns subscribe --protocol sqs --topic-arn arn:aws:sns:us-east-1:000000000000:workflow-topic --notification-endpoint arn:aws:sqs:us-east-1:000000000000:workflow-request-queue --query 'SubscriptionArn' --output text)
awslocal sns set-subscription-attributes --subscription-arn "$SUBSCRIPTION_ARN" --attribute-name FilterPolicy --attribute-value "{\"x-dwp-routing-key\": [ \"workflow\" ] }"
awslocal sns get-subscription-attributes --subscription-arn "$SUBSCRIPTION_ARN"
