package uk.gov.dwp.health.pip.application.manager.config.properties;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;
import javax.validation.Validator;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
class ApplicationPropertiesTest {

  private static Validator validator;
  private ApplicationProperties underTest;

  @BeforeAll
  static void setupSpec() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @BeforeEach
  void setup() {
    underTest = new ApplicationProperties();
  }

  @Test
  void testNoValueValidationConstraintOnFields() {
    assertThat(validator.validate(underTest).size()).isEqualTo(1);
  }

  @Test
  void testMinValidationConstraintOnFields() {
    underTest.setActiveDuration(0);
    assertThat(validator.validate(underTest).size()).isEqualTo(1);
  }

  @Test
  void testGetSetActiveDuration() {
    underTest.setActiveDuration(93);
    assertThat(underTest.getActiveDuration()).isEqualTo(93);
  }

  @Test
  @DisplayName("test get set registration form version")
  void testGetSetRegistrationFormVersion() {
    underTest.setRegistrationFormSchemaVersion("2");
    assertThat(underTest.getRegistrationFormSchemaVersion()).isEqualTo("2");
  }

  @Test
  @DisplayName("test get set health disability form version")
  void testGetSetHealthDisabilityFormVersion() {
    underTest.setHealthDisabilityFormSchemaVersion("1");
    assertThat(underTest.getHealthDisabilityFormSchemaVersion()).isEqualTo("1");
  }

  @Test
  @DisplayName("test default registration default value")
  void testDefaultRegistrationDefaultValue() {
    assertThat(underTest.getRegistrationFormSchemaVersion()).isEqualTo("1.2.0");
  }

  @Test
  @DisplayName("test default health disability form version")
  void testDefaultHealthDisabilityFormVersion() {
    assertThat(underTest.getHealthDisabilityFormSchemaVersion()).isEqualTo("1.0.0");
  }
}
