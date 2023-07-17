package uk.gov.dwp.health.pip.application.manager.service;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Tag("unit")
class NinoValidatorTest {

  @Test
  void when_nino_valid() {
    assertThat(NinoValidator.validate("RN000004A")).isTrue();
  }

  @Test
  void when_nino_not_valid() {
    assertThatThrownBy(() -> NinoValidator.validate("bad-nino"))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
