package uk.gov.dwp.health.pip.application.manager.messaging;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Tag("unit")
class PipcsApiEventTest {

  @Test
  void should_create_pipcs_api_event_message() {
    var now = Instant.now();
    var version = "1.0";
    var routingKey = "test-routing-key";
    var topicExchange = "topic-exchange";
    Map<String, Object> payload = Collections.singletonMap("data", "value");
    var pipcsApiEvent = new PipcsApiEvent(topicExchange, routingKey, payload, now, version);
    assertAll(
        "assert pipcs api event",
        () -> {
          assertThat(pipcsApiEvent.getTopic()).isEqualTo(topicExchange);
          assertThat(pipcsApiEvent.getRoutingKey()).isEqualTo(routingKey);
          assertThat(pipcsApiEvent.getVersion()).isEqualTo(version);
          assertThat(pipcsApiEvent.getPayload()).isEqualTo(payload);
          var meta = pipcsApiEvent.getMetaData();
          assertThat(meta.getTime()).isEqualTo(now.toString());
        });
  }
}
