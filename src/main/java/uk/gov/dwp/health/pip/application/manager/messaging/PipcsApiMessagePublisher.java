package uk.gov.dwp.health.pip.application.manager.messaging;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.integration.message.events.EventManager;
import uk.gov.dwp.health.pip.application.manager.event.model.request.PipGatewayRequestEventSchemaV1;
import uk.gov.dwp.health.pip.application.manager.exception.MessagingEventException;
import uk.gov.dwp.health.pip.application.manager.messaging.properties.OutboundEventProperties;

import java.time.Clock;

@Slf4j
@Service
@RequiredArgsConstructor
public class PipcsApiMessagePublisher {

  private final EventManager eventManager;
  private final ObjectMapper objectMapper;
  private final Clock clock;
  private final OutboundEventProperties outboundEventProperties;

  public void publishMessage(PipGatewayRequestEventSchemaV1 event) {
    try {
      log.info("PIPCS-API request message published");
      log.debug("PIPCS-API request message published {}", event.toString());
      eventManager.send(
          new PipcsApiEvent(
              outboundEventProperties.getTopicExchange(),
              outboundEventProperties.getRoutingKey(),
              objectMapper.convertValue(event, new TypeReference<>() {}),
              clock.instant(),
              outboundEventProperties.getVersion()));
    } catch (Exception ex) {
      log.info("PIP-API publish message failed {}", ex.getMessage());
      throw new MessagingEventException(ex.getMessage());
    }
  }
}
