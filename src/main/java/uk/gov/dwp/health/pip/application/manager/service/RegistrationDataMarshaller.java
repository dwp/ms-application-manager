package uk.gov.dwp.health.pip.application.manager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.pip.application.manager.exception.RegistrationDataNotValid;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.RegistrationSchema120;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.util.Set;

@RequiredArgsConstructor
@Service
@Slf4j
public class RegistrationDataMarshaller {

  private final ObjectMapper objectMapper;

  RegistrationSchema120 marshallRegistrationData(Object registrationData) {
    RegistrationSchema120 registrationSchema = getRegistrationSchema120(registrationData);

    if (registrationSchema != null) {
      validate(registrationSchema);
    }

    return registrationSchema;
  }

  private RegistrationSchema120 getRegistrationSchema120(Object registrationData) {
    return getRegistrationSchema(registrationData, RegistrationSchema120.class);
  }

  private <T> T getRegistrationSchema(Object registrationData, Class<T> registrationSchemaVersion) {
    try {
      byte[] bytes = objectMapper.writeValueAsBytes(registrationData);
      return objectMapper.readValue(bytes, registrationSchemaVersion);
    } catch (IOException e) {
      log.debug(
          "Registration data not valid. Problem when marshalling to objects.", e
      );
      log.error(
          "Registration data not valid. Problem when marshalling to objects."
      );
      throw new RegistrationDataNotValid(
          "Registration data not valid. Problem when marshalling to objects.");
    }
  }

  private <T> void validate(T registrationSchema) {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    Set<ConstraintViolation<T>> constraintViolations =
        validator.validate(registrationSchema);

    if (!constraintViolations.isEmpty()) {
      log.error(
          "Registration data not valid. Constraint violations present. {}",
          constraintViolations);
      throw new RegistrationDataNotValid(
          "Registration data not valid. Constraint violations present.");
    }
  }
}
