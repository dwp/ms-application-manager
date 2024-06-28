package uk.gov.dwp.health.pip.application.manager.messaging.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "event.inbound")
@Validated
public class InboundEventProperties {

  @NotNull(message = "pip-apply message topic can not be null")
  @NotBlank(message = "pip-apply message topic can not be blank")
  private String topicName;

  @NotNull(message = "Registration response message routing key can not be null")
  @NotBlank(message = "Registration response message routing key can not be blank")
  private String routingKeyRegistrationResponse;

  @NotNull(message = "Registration response message queue name can not be null")
  @NotBlank(message = "Registration response message queue name can not be blank")
  private String queueNameRegistrationResponse;
}
