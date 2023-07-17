package uk.gov.dwp.health.pip.application.manager.config;

import com.amazonaws.regions.Regions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.dwp.health.crypto.CryptoConfig;
import uk.gov.dwp.health.crypto.CryptoDataManager;
import uk.gov.dwp.health.crypto.exception.CryptoException;
import uk.gov.dwp.health.pip.application.manager.config.properties.CryptoConfigProperties;
import uk.gov.dwp.health.pip.application.manager.exception.CryptoConfigException;

import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Configuration
public class KmsCryptoConfig {

  private final CryptoConfigProperties properties;

  @Autowired
  public KmsCryptoConfig(final CryptoConfigProperties properties) {
    this.properties = properties;
  }

  @Bean
  public CryptoDataManager cryptoDataManager() {
    try {
      var config = createConfigurationOverride();
      config.setDataKeyId(this.properties.getMessageDataKeyId());
      return new CryptoDataManager(config);
    } catch (IOException
        | NoSuchAlgorithmException
        | InvalidKeyException
        | CryptoException
        | NoSuchPaddingException
        | IllegalBlockSizeException e) {
      final String msg =
          String.format("Failed to config DataCryptoManager for Messaging %s", e.getMessage());
      log.error(msg);
      throw new CryptoConfigException(msg);
    }
  }

  private CryptoConfig createConfigurationOverride()
      throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException,
          IllegalBlockSizeException {
    var config = new CryptoConfig();
    if (this.properties.getKmsOverride() != null && !this.properties.getKmsOverride().isBlank()) {
      config.setKmsEndpointOverride(this.properties.getKmsOverride());
    }
    if (this.properties.getRegion() != null && !this.properties.getRegion().isBlank()) {
      config.setRegion(Regions.valueOf(this.properties.getRegion()));
    }
    config.setCacheKmsDataKeys(this.properties.isKmsKeyCache());
    return config;
  }
}
