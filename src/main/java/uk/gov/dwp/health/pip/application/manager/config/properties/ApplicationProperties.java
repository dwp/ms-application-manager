package uk.gov.dwp.health.pip.application.manager.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;

@Configuration
@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "pip.application")
public class ApplicationProperties {

  @Min(value = 1, message = "Active duration must be grater or equals 1")
  private int activeDuration;

  private String registrationFormSchemaVersion = "1.2.0";

  private String healthDisabilityFormSchemaVersion = "1.0.0";
}
