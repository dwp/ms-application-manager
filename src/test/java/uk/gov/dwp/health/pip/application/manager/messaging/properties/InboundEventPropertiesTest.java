package uk.gov.dwp.health.pip.application.manager.messaging.properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Tag("unit")
class InboundEventPropertiesTest {

  private InboundEventProperties inboundEventProperties;

  @BeforeEach
  void beforeEach() {
    inboundEventProperties = new InboundEventProperties();
  }

  @Test
  void should_set_and_get_inbound_topic_name() {
    inboundEventProperties.setTopicName("test-topics");
    assertThat(inboundEventProperties.getTopicName()).isEqualTo("test-topics");
  }

  @Test
  void should_set_and_get_inbound_routing_key() {
    inboundEventProperties.setRoutingKeyRegistrationResponse("test-routing-key");
    assertThat(inboundEventProperties.getRoutingKeyRegistrationResponse()).isEqualTo("test-routing-key");
  }
}
