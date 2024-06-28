package uk.gov.dwp.health.pip.application.manager.config.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "pip.coordinator")
@Validated
public class ApplicationCoordinatorProperties {

  @NotBlank(message = "Base uri should not be blank")
  private String baseUrl;

}

