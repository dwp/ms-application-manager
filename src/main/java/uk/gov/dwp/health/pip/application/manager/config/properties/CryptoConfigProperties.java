package uk.gov.dwp.health.pip.application.manager.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "aws.encryption")
@Validated
public class CryptoConfigProperties {

  private String kmsOverride;

  @NotBlank(message = "KMS data key for SNS/SQS required")
  @NotNull(message = "KMS data key for SNS/SQS required")
  private String messageDataKeyId;

  private boolean kmsKeyCache;

  private String region;
}
