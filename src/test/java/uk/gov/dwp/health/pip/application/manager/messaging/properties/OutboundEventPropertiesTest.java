package uk.gov.dwp.health.pip.application.manager.messaging.properties;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
class OutboundEventPropertiesTest {

  private static OutboundEventProperties outboundEventProperties;

  @BeforeAll
  static void beforeAll() {
    outboundEventProperties = new OutboundEventProperties();
  }

  @Test
  void should_set_and_get_outbound_routing_key() {
    outboundEventProperties.setRoutingKey("test-routing-key");
    assertThat(outboundEventProperties.getRoutingKey()).isEqualTo("test-routing-key");
  }

  @Test
  void should_return_default_version_1() {
    assertThat(outboundEventProperties.getVersion()).isEqualTo("1.0");
  }

  @Test
  void should_override_version() {
    outboundEventProperties.setVersion("0.1");
    assertThat(outboundEventProperties.getVersion()).isEqualTo("0.1");
  }

  @Test
  void when_routing_key_missing_constraints_violation_greater_then_zero() {
    var validator = Validation.buildDefaultValidatorFactory().getValidator();
    var actual = validator.validate(outboundEventProperties);
    assertThat(actual.size()).isGreaterThan(0);
  }
}
