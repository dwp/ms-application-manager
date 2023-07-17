package uk.gov.dwp.health.pip.application.manager.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
class ValidationExceptionTest {

  @Test
  @DisplayName("Test validation exception")
  void testValidationException() {
    ValidationException actual = new ValidationException("validation failed");
    assertThat(actual.getMessage()).isEqualTo("validation failed");
  }
}
