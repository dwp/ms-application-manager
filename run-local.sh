#!/bin/bash

PIP_APPLICATION_ACTIVE_DURATION=90 \
EVENT_OUTBOUND_ROUTING_KEY=pipcs-registration \
EVENT_INBOUND_TOPIC_NAME=pip-apply-topic-FOR-DELETION \
EVENT_INBOUND_ROUTING_KEY_REGISTRATION_RESPONSE=registration-response-routing-key \
EVENT_INBOUND_QUEUE_NAME_REGISTRATION_RESPONSE=pip-registration-response \
AWS_ENCRYPTION_MESSAGE_DATA_KEY_ID=alias/test_event_request_id \
mvn spring-boot:run 
