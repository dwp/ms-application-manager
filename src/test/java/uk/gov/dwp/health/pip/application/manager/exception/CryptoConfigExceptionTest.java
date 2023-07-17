package uk.gov.dwp.health.pip.application.manager.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
class CryptoConfigExceptionTest {

  @Test
  @DisplayName("Test crypto config exception")
  void testCryptoConfigException() {
    CryptoConfigException cut = new CryptoConfigException("kms setup exception");
    assertThat(cut.getMessage()).isEqualTo("kms setup exception");
  }
}
