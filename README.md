# ms-application-manager

micro-service to support save and resume. The main purposes of this service are to support online HTML application
process for PIP2 questionnaire.

## Dependency

the API stores and queries user's application details from a Mongo database. To be able to successfully start the application,
the application must be able to connect to a Mongo instance at start up.

## Rest API

the api is built from the [openapi-spec.yaml](api-spec/openapi-spec-registration.yaml)

## Running the application

this is a standard SpringBoot application with all the configuration items held in `src/main/resources/application.yml`
and bundled into the project at build.

```bash
mvn clean verify
```

to build and vulnerability check

```bash
sh run-local.sh #this includes some sample environment variable values to get the app running

or

mvn spring-boot:run

or

java -jar target/ms-application-manager-<artifactId>.jar
```

## Running the Component Tests

### Running Locally 
Run the following shell script to spin up the service in a docker environment
```bash 
sh ./ms-application-manager.sh
```
Open another terminal window and run the following maven command to execute the tests locally
```bash 
mvn clean verify -Papi-component-tests
```

## Configuration elements

All configuration listed in `src/main/resources/application.yml` and follows the standard spring convention for yml file
notation.  
The custom setup configured with the following section and can be overridden (either on the command line or by
environment variables).

```yaml
app:
  application:
    active-duration: 93

encryption:
  kms-override: http://localhost:4549
  data-key: arn:address

feature:
  encryption:
    data:
      enabled: true
```

* `app.application.active-duration` = the number of days a application is valid for
* `encryption.kms-override` = override kms url e.g. http://localhost:4599
* `encryption.data-key` = aws KMS arn
* `feature.encryption.data.enabled` = enable data encryption/decryption onBeforeSave and onAfterLoad event.
* `pip.bank.validation.consumerId` = consumer ID for using bank validation service
* `pip.bank.validation.baseUrl` = base url of bank validation service

## Data analytics plugin

### dependency (data analytics)

data plugin captures mongo change from a specific database triggered by a micro-service instance

```xml

<dependency>
  <groupId>uk.gov.dwp.health</groupId>
  <artifactId>mongo-changestream-data-stater</artifactId>
  <version>${dwp-mongo-change-stream-starter.version}</version>
</dependency>
```

message broker publishes change to a designated queue

```xml 
  <dependency>
     <groupId>uk.gov.dwp.health.integration</groupId>
     <artifactId>message-broker-integration-autoconfigure</artifactId>
    <version>${dwp.message-broker.version}</version>
  </dependency>
```

### configuration variables (data analytics)

```yaml
- FEATURE_DATA_CHANGESTREAM_ENABLED=true
- UK_GOV_DWP_HEALTH_CHANGESTREAM_CHANNELS[0]_COLLECTION=application
- UK_GOV_DWP_HEALTH_CHANGESTREAM_CHANNELS[0]_ROUTING_KEY=pip.application.mgr.stream
- UK_GOV_DWP_HEALTH_CHANGESTREAM_CHANNELS[0]_DATABASE=pip-apply-application-mgr
- UK_GOV_DWP_HEALTH_CHANGESTREAM_CHANNELS[0]_SKIP_DECRYPTION=true
- UK_GOV_DWP_HEALTH_INTEGRATION_OUTBOUND_TOPIC_EXCHANGE=stream-topic
- UK_GOV_DWP_HEALTH_INTEGRATION_SNS_ENDPOINT_OVERRIDE=http://localstack:4566
- UK_GOV_DWP_HEALTH_INTEGRATION_SQS_ENDPOINT_OVERRIDE=http://localstack:4566
- UK_GOV_DWP_HEALTH_INTEGRATION_AWS_REGION=us-east-1
- UK_GOV_DWP_HEALTH_INTEGRATION_MESSAGING_TYPE=aws
```

## Docker

The docker image built on the distroless base image


## Registration states
PUBLISHED(0),// published waiting for pipcs-gw rely on response back
SUBMITTED(1),// response received - pipcs accepted
REJECTED(-1), // response received - situation where validation failed no retry
DECISION_MADE(-1), // response received - final state rejected
DISALLOW(-1);  // response received - final state rejected
