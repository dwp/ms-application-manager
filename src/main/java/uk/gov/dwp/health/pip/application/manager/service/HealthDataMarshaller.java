package uk.gov.dwp.health.pip.application.manager.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.pip.application.manager.exception.HealthDisabilityDataNotValid;
import uk.gov.dwp.health.pip2.common.Pip2HealthDisabilityForm;
import uk.gov.dwp.health.pip2.common.marshaller.Pip2HealthDisabilityFormMarshaller;
import uk.gov.dwp.health.pip2.common.validation.Validatable;

@Service
@Slf4j
class HealthDataMarshaller {

  Pip2HealthDisabilityForm marshallHealthData(Object formData) {
    var pip2HealthDisabilityForm = getPip2HealthDisabilityForm(formData);

    validate(pip2HealthDisabilityForm);

    return pip2HealthDisabilityForm;
  }

  private Pip2HealthDisabilityForm getPip2HealthDisabilityForm(Object formData) {
    try {
      var pip2HealthDisabilityFormMarshaller = new Pip2HealthDisabilityFormMarshaller();
      return pip2HealthDisabilityFormMarshaller.toHealthDisabilityForm(formData);
    } catch (JsonProcessingException e) {
      log.debug(
          "Health and disability data not valid. Problem when marshalling to objects. {}",
          e.getMessage());
      throw new HealthDisabilityDataNotValid(
          "Health and disability data not valid. Problem when marshalling to objects.");
    }
  }

  private void validate(Validatable subject) {
    if (!subject.validate()) {
      log.debug(
          "Health and disability data failed validation.  Errors: {}", subject.errorsToString());
      throw new HealthDisabilityDataNotValid(
          "Health and disability data failed validation.  Errors: " + subject.errorsToString());
    }
  }
}
