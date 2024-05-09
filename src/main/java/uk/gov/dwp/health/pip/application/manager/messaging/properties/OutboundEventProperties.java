package uk.gov.dwp.health.pip.application.manager.messaging.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "event.outbound")
@Validated
public class OutboundEventProperties {

  private String topicExchange;

  @NotBlank
  @NotNull(message = "Outbound message routing message ")
  private String routingKey;

  private String version = "1.0";
}
