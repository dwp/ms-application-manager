package uk.gov.dwp.health.pip.application.manager.constant;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import uk.gov.dwp.health.pip.application.manager.exception.RegistrationDataNotValid;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("unit")
class RegistrationStateTest {


  @ParameterizedTest
  @ValueSource(strings = {"Validation failed",
      "Pending",
      "Submitting",
      "Disallowed",
      "Withdrawn",
      "Retries exhausted",
      "Submitted"})
  void should_return_enum_by_label(String label) {
    var actual = RegistrationState.getRegistrationStateByLabel(label);
    assertThat(actual).isInstanceOf(RegistrationState.class);
  }

  @ParameterizedTest
  @ValueSource(strings = {"UNKNOWN_STATE"})
  @NullAndEmptySource
  void should_throw_registration_data_not_valid_exception(String label) {
    assertThatThrownBy(() -> RegistrationState.getRegistrationStateByLabel(label))
        .isInstanceOf(RegistrationDataNotValid.class)
        .hasMessage("Registration state is not recognised");
  }
}
