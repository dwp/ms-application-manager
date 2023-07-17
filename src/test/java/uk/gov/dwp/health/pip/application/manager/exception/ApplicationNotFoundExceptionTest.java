package uk.gov.dwp.health.pip.application.manager.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
class ApplicationNotFoundExceptionTest {

  @Test
  @DisplayName("Test claim not found exception")
  void testClaimNotFoundException() {
    ApplicationNotFoundException cut = new ApplicationNotFoundException("Claim not found");
    assertThat(cut.getMessage()).isEqualTo("Claim not found");
  }
}
