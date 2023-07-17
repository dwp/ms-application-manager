package uk.gov.dwp.health.pip.application.manager.service.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.pip.application.manager.exception.RegistrationDataNotValid;
import uk.gov.dwp.health.pip.pipcsapimodeller.validation.Validatable;

@Service
@Slf4j
class PipcsApiModelValidator {

  void validate(Validatable subject) {
    if (!subject.validate()) {
      log.debug(
          "Registration data not valid.  Problem when mapping to PIPCS.  Errors: {}",
          subject.errorsToString());
      throw new RegistrationDataNotValid(
          "Registration data not valid.  Problem when mapping to PIPCS.  Errors: "
              + subject.errorsToString());
    }
  }
}
