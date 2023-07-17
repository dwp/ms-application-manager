package uk.gov.dwp.health.pip.application.manager.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.integration.message.consumers.HealthMessageConsumer;
import uk.gov.dwp.health.pip.application.manager.event.model.response.PipGatewayRespondEventSchemaV1;
import uk.gov.dwp.health.pip.application.manager.messaging.properties.InboundEventProperties;
import uk.gov.dwp.health.pip.application.manager.service.RegistrationResponseUpdater;

import java.util.Map;

@Service
@Getter
@Slf4j
public class PipcsApiMessageConsumer implements HealthMessageConsumer<Map<String, Object>> {

  private final ObjectMapper objectMapper;
  private final String queueName;
  private final String routingKey;
  private final RegistrationResponseUpdater registrationResponseUpdater;

  public PipcsApiMessageConsumer(
      InboundEventProperties inboundEventProperties,
      ObjectMapper objectMapper,
      RegistrationResponseUpdater registrationResponseUpdater) {
    this.objectMapper = objectMapper;
    this.queueName = inboundEventProperties.getQueueNameRegistrationResponse();
    this.routingKey = inboundEventProperties.getRoutingKeyRegistrationResponse();
    this.registrationResponseUpdater = registrationResponseUpdater;
  }

  @Override
  public void handleMessage(MessageHeaders messageHeaders, Map<String, Object> payload) {
    log.info("Received a registration response message");
    PipGatewayRespondEventSchemaV1 registrationResponse =
        objectMapper.convertValue(payload, PipGatewayRespondEventSchemaV1.class);
    log.debug("Received a registration response message {}", registrationResponse.toString());
    registrationResponseUpdater.updateApplicationWithRegistrationResponse(registrationResponse);
  }
}
