package uk.gov.dwp.health.pip.application.manager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.pip.application.manager.exception.RegistrationDataNotValid;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.RegistrationSchema100;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.RegistrationSchema110;

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

  RegistrationSchema100 marshallRegistrationData(Object registrationData) {
    RegistrationSchema100 registrationSchema = getRegistrationSchema(registrationData);

    validate(registrationSchema);

    return registrationSchema;
  }

  RegistrationSchema110 marshallRegistrationData110(Object registrationData) {
    RegistrationSchema110 registrationSchema = getRegistrationSchema110(registrationData);

    validate(registrationSchema);

    return registrationSchema;
  }

  private RegistrationSchema110 getRegistrationSchema110(Object registrationData) {
    return getRegistrationSchema(registrationData, RegistrationSchema110.class);
  }

  private RegistrationSchema100 getRegistrationSchema(Object registrationData) {
    return getRegistrationSchema(registrationData, RegistrationSchema100.class);
  }

  private <T> T getRegistrationSchema(Object registrationData, Class<T> registrationSchemaVersion) {
    try {
      byte[] bytes = objectMapper.writeValueAsBytes(registrationData);
      return objectMapper.readValue(bytes, registrationSchemaVersion);
    } catch (IOException e) {
      log.debug(
          "Registration data not valid. Problem when marshalling to objects. {}", e.getMessage());
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
          constraintViolations.toString());
      throw new RegistrationDataNotValid(
          "Registration data not valid. Constraint violations present.");
    }
  }
}
