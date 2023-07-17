package uk.gov.dwp.health.pip.application.manager.messaging;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.dwp.health.integration.message.events.EventManager;
import uk.gov.dwp.health.pip.application.manager.event.model.request.PipGatewayRequestEventSchemaV1;
import uk.gov.dwp.health.pip.application.manager.exception.MessagingEventException;
import uk.gov.dwp.health.pip.application.manager.messaging.properties.OutboundEventProperties;

import java.time.Clock;
import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PipcsApiMessagePublisherTest {

  @InjectMocks private PipcsApiMessagePublisher pipcsApiEventPublisher;
  @Mock private Clock clock;
  @Mock private EventManager eventManager;
  @Mock private OutboundEventProperties outboundEventProperties;
  @Mock private ObjectMapper objectMapper;

  private static final Instant NOW = Instant.now();
  private static final String APP_ID = UUID.randomUUID().toString();

  @Test
  void when_fail_publish_event_messaging_event_message_event_exception_thrown() {
    doThrow(new RuntimeException("Details on failure of publishing message"))
        .when(eventManager)
        .send(any(PipcsApiEvent.class));

    var pipGatewayRequestEventSchemaV1 = new PipGatewayRequestEventSchemaV1();
    assertThatThrownBy(() -> pipcsApiEventPublisher.publishMessage(pipGatewayRequestEventSchemaV1))
        .isInstanceOf(MessagingEventException.class)
        .hasMessage("Details on failure of publishing message");
  }

  @Test
  void should_publish_registration_data_to_pipcs_api() {
    setupTest();
    var event = new PipGatewayRequestEventSchemaV1();
    event.setApplicationId(APP_ID);
    pipcsApiEventPublisher.publishMessage(event);

    var captor = ArgumentCaptor.forClass(PipcsApiEvent.class);
    verify(eventManager).send(captor.capture());
    assertAll(
        "assert PIPCSApi event",
        () -> {
          var actual = captor.getValue();
          assertThat(actual.getVersion()).isEqualTo("1.0");
          assertThat(actual.getTopic()).isEqualTo("test-topic-exchange");
          assertThat(actual.getRoutingKey()).isEqualTo("test-routing-key");
          assertThat(actual.getPayload()).isEqualTo(Collections.singletonMap("test", "test"));
          assertThat(actual.getMetaData().getTime()).isEqualTo(NOW.toString());
        });
  }

  private void setupTest() {
    when(outboundEventProperties.getTopicExchange()).thenReturn("test-topic-exchange");
    when(outboundEventProperties.getRoutingKey()).thenReturn("test-routing-key");
    when(outboundEventProperties.getVersion()).thenReturn("1.0");
    when(clock.instant()).thenReturn(NOW);
    when(objectMapper.convertValue(
            any(PipGatewayRequestEventSchemaV1.class), any(TypeReference.class)))
        .thenReturn(Collections.singletonMap("test", "test"));
  }
}
