package uk.gov.dwp.health.pip.application.manager.config.properties;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
class CryptoTestConfigPropertiesTest {

  @Test
  void testKmsDataKeyPropIsRequired() {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    CryptoConfigProperties cut = new CryptoConfigProperties();
    cut.setMessageDataKeyId("");
    cut.setKmsOverride("");
    assertThat(validator.validate(cut).size()).isEqualTo(1);
    assertThat(cut.isKmsKeyCache()).isFalse();
  }

  @Test
  @DisplayName("test all getter and setter")
  void testAllGetterAndSetter() {
    CryptoConfigProperties cut = new CryptoConfigProperties();
    cut.setMessageDataKeyId("mock-data-key");
    cut.setKmsOverride("mock-kms-override");
    cut.setKmsKeyCache(true);
    assertThat(cut.getMessageDataKeyId()).isEqualTo("mock-data-key");
    assertThat(cut.getKmsOverride()).isEqualTo("mock-kms-override");
    assertThat(cut.isKmsKeyCache()).isTrue();
  }
}
