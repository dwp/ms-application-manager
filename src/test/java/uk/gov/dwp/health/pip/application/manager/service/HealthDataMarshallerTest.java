package uk.gov.dwp.health.pip.application.manager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.dwp.health.pip.application.manager.exception.HealthDisabilityDataNotValid;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static support.FileUtils.readTestFileAsObject;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class HealthDataMarshallerTest {

  private HealthDataMarshaller healthDataMarshaller;

  @BeforeEach
  void beforeEach() {
    healthDataMarshaller = new HealthDataMarshaller();
  }

  @Test
  void when_form_data_ok_then_submit() throws IOException {
    Object formData = readTestFileAsObject("health-and-disability/validHealthData.json");

    var pip2HealthDisabilityForm = healthDataMarshaller.marshallHealthData(formData);

    assertThat(pip2HealthDisabilityForm.getDetails().getNino()).isEqualTo("RN000003A");
  }

  @Test
  void when_problem_marshalling_to_objects_then_data_not_valid() throws IOException {
    Object formData = readTestFileAsObject("health-and-disability/badHealthData.json");

    assertThatThrownBy(() -> healthDataMarshaller.marshallHealthData(formData))
        .isInstanceOf(HealthDisabilityDataNotValid.class)
        .hasMessage("Health and disability data not valid. Problem when marshalling to objects.");
  }

  @Test
  void when_failed_validation_then_not_valid() throws IOException {
    Object formData = readTestFileAsObject("health-and-disability/invalidHealthData.json");

    assertThatThrownBy(() -> healthDataMarshaller.marshallHealthData(formData))
        .isInstanceOf(HealthDisabilityDataNotValid.class)
        .hasMessage(
            "Health and disability data failed validation.  Errors: [DLA - Eating and drinking "
                + "description is blank, whilst condition affecting the applicant]");
  }
}
