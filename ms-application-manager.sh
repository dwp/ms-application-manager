#!/usr/bin/env bash

echo "Log into AWS ECR"
aws ecr get-login-password --region eu-west-2 | docker login --username AWS --password-stdin 431469937388.dkr.ecr.eu-west-2.amazonaws.com

echo "Getting image details from AWS"
export LOCALSTACK_IMAGE=localstack/localstack:0.12.20
export MONGO_IMAGE=mongo:5.0
GITLAB_IMAGE=$(aws ssm get-parameter --name /artifact/pip-apply/ms-application-manager/develop --region eu-west-2 --with-decryption --output json | jq --raw-output '.Parameter.Value')
export GITLAB_IMAGE
MOCK_IMAGE=$(aws ssm get-parameter --name /artifact/pip-apply/pip-apply-mocks/develop --region eu-west-2 --with-decryption --output json | jq --raw-output '.Parameter.Value')
export MOCK_IMAGE

echo "Starting containers"
docker-compose -f docker-compose.yml up --build --scale api-test=0
