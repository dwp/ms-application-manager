package uk.gov.dwp.health.pip.application.manager.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.dwp.health.crypto.CryptoDataManager;
import uk.gov.dwp.health.pip.application.manager.config.properties.CryptoConfigProperties;
import uk.gov.dwp.health.pip.application.manager.exception.CryptoConfigException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class KmsCryptoConfigTest {

  @InjectMocks private KmsCryptoConfig cut;
  @Mock private CryptoConfigProperties cryptoConfigProperties;

  @Test
  @DisplayName("test create crypto data manager for messaging ")
  void testCreateCryptoDataManagerForMessaging() {
    when(cryptoConfigProperties.getMessageDataKeyId()).thenReturn("event-mock-key-id");
    when(cryptoConfigProperties.isKmsKeyCache()).thenReturn(true);
    when(cryptoConfigProperties.getKmsOverride()).thenReturn("");
    when(cryptoConfigProperties.getRegion()).thenReturn("");
    assertThat(cut.cryptoDataManager()).isNotNull().isExactlyInstanceOf(CryptoDataManager.class);
    verify(cryptoConfigProperties, times(2)).getKmsOverride();
    verify(cryptoConfigProperties, times(2)).getRegion();
  }

  @Test
  @DisplayName("test create crypto data manager for messaging with overrides")
  void testCreateCryptoDataManagerForMessagingWithOverride() {
    when(cryptoConfigProperties.getMessageDataKeyId()).thenReturn("event-mock-key-id");
    when(cryptoConfigProperties.isKmsKeyCache()).thenReturn(true);
    when(cryptoConfigProperties.getKmsOverride()).thenReturn("http://localhost");
    when(cryptoConfigProperties.getRegion()).thenReturn("EU_WEST_2");
    assertThat(cut.cryptoDataManager()).isNotNull().isExactlyInstanceOf(CryptoDataManager.class);
    verify(cryptoConfigProperties, times(3)).getKmsOverride();
    verify(cryptoConfigProperties, times(3)).getRegion();
  }

  @Test
  @DisplayName("test create crypto data manager throws CryptoConfigurationException")
  void testCreateCryptoDataManagerThrowsCryptoConfigurationException() {
    var thrown = assertThrows(CryptoConfigException.class, () -> cut.cryptoDataManager());
    assertThat(thrown.getMessage()).contains("Failed to config DataCryptoManager for Messaging");
  }
}
