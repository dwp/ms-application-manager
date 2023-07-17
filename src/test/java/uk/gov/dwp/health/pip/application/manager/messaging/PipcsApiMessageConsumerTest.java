package uk.gov.dwp.health.pip.application.manager.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.dwp.health.pip.application.manager.event.model.response.PipGatewayRespondEventSchemaV1;
import uk.gov.dwp.health.pip.application.manager.messaging.properties.InboundEventProperties;
import uk.gov.dwp.health.pip.application.manager.service.RegistrationResponseUpdater;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class PipcsApiMessageConsumerTest {

  private PipcsApiMessageConsumer pipcsApiMessageConsumer;

  @Mock private RegistrationResponseUpdater registrationResponseUpdater;

  @BeforeEach
  void beforeEach() {
    pipcsApiMessageConsumer =
        new PipcsApiMessageConsumer(
            new InboundEventProperties(), new ObjectMapper(), registrationResponseUpdater);
  }

  @Test
  void when_message_processed() {
    Map<String, Object> payload = new HashMap<>();
    payload.put("applicationId", "application-id-1");
    payload.put("applicationRef", "application-ref-1");
    payload.put("state", "state-1");
    payload.put("message", "message-1");
    payload.put("timestamp", "timestamp-1");

    pipcsApiMessageConsumer.handleMessage(null, payload);

    ArgumentCaptor<PipGatewayRespondEventSchemaV1> argumentCaptor =
        ArgumentCaptor.forClass(PipGatewayRespondEventSchemaV1.class);

    verify(registrationResponseUpdater, times(1))
        .updateApplicationWithRegistrationResponse(argumentCaptor.capture());

    PipGatewayRespondEventSchemaV1 responseObject = argumentCaptor.getValue();

    assertThat(responseObject.getApplicationId()).isEqualTo("application-id-1");
    assertThat(responseObject.getApplicationRef()).isEqualTo("application-ref-1");
    assertThat(responseObject.getState()).isEqualTo("state-1");
    assertThat(responseObject.getMessage()).isEqualTo("message-1");
    assertThat(responseObject.getTimestamp()).isEqualTo("timestamp-1");
  }

  @Test
  void when_bad_registration_response_sent_then_exception_is_thrown() {
    Map<String, Object> badRegistrationResponse = new HashMap<>();
    badRegistrationResponse.put("Bad", "Message");

    assertThatThrownBy(() -> pipcsApiMessageConsumer.handleMessage(null, badRegistrationResponse))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
