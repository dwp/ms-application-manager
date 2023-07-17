package uk.gov.dwp.health.pip.application.manager.messaging.properties;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Tag("unit")
class OutboundWorkflowEventPropertiesTest {

  @Test
  void when_properties_initialised() {
    var properties = new OutboundWorkflowEventProperties();
    properties.setTopicExchange("test-topic");
    properties.setRoutingKey("test-routing-key");
    assertThat(properties.getTopicExchange()).isEqualTo("test-topic");
    assertThat(properties.getRoutingKey()).isEqualTo("test-routing-key");
  }
}
