package uk.gov.dwp.health.pip.application.manager.service.mapper;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.dwp.health.pip.application.manager.exception.RegistrationDataNotValid;
import uk.gov.dwp.health.pip.pipcsapimodeller.validation.Validatable;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class PipcsApiModelValidatorTest {

  @InjectMocks private PipcsApiModelValidator pipcsApiModelValidator;
  private Validatable validatable = new Validatable() {
    @Override
    public String errorsToString() {
      return "errors-to-string";
    }
    
    @Override
    public boolean validate() {
      return false;
    }
  };

  @Test
  void when_not_valid() {
    assertThatThrownBy(() -> pipcsApiModelValidator.validate(validatable))
        .isInstanceOf(RegistrationDataNotValid.class)
        .hasMessage(
            "Registration data not valid.  Problem when mapping to PIPCS.  Errors: "
                + "errors-to-string");
  }
}
