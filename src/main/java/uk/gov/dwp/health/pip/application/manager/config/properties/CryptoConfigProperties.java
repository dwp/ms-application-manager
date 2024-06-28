package uk.gov.dwp.health.pip.application.manager.config.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

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
