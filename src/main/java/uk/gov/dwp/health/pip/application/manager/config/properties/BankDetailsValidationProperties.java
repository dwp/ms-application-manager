package uk.gov.dwp.health.pip.application.manager.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "pip.bank.validation")
public class BankDetailsValidationProperties {

  private String consumerId;

  private String baseUrl;

  // TLS cfg

  private boolean tlsEnabled = false;

  private String trustStore;

  private String trustStorePassword;

  private String keyStore;

  private String keyStorePassword;

  // Proxy cfg

  private boolean proxyEnabled = false;

  private String proxyHost;

  private int proxyPort;

}
