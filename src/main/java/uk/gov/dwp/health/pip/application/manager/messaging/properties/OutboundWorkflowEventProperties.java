package uk.gov.dwp.health.pip.application.manager.messaging.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "event.outbound.workflow")
public class OutboundWorkflowEventProperties {

  private String topicExchange;
  private String routingKey;
}
