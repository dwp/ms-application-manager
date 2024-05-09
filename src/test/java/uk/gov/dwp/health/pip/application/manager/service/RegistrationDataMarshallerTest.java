package uk.gov.dwp.health.pip.application.manager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.health.pip.application.manager.exception.RegistrationDataNotValid;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.RegistrationSchema130;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static support.FileUtils.readTestFile;

@Tag("unit")
class RegistrationDataMarshallerTest {

  private RegistrationDataMarshaller registrationDataMarshaller;

  @BeforeEach
  void beforeEach() {
    registrationDataMarshaller = new RegistrationDataMarshaller(new ObjectMapper());
  }

  @Test
  void when_valid() throws IOException {
    Map<String, Object> map = readTestFile("mapping/validRegistrationData.json");
    final RegistrationSchema130 registrationSchema = registrationDataMarshaller.marshallRegistrationData(map);
    assertThat(registrationSchema.getPersonalDetails().getSurname()).isEqualTo("Azzzle");
    assertThat(registrationSchema.getPersonalDetails().getBankDetails().getEnterBankDetails()).isNotNull();
    assertThat(registrationSchema.getPersonalDetails().getBankDetails().getAdditionalProperties().get("accountName")).isEqualTo("Bank of Yorkshire");
    assertThat(registrationSchema.getPersonalDetails().getBankDetails().getAdditionalProperties().get("accountNumber")).isEqualTo("12341234");
    assertThat(registrationSchema.getPersonalDetails().getBankDetails().getAdditionalProperties().get("sortCode")).isEqualTo("123123");
    assertThat(registrationSchema.getPersonalDetails().getBankDetails().getAdditionalProperties().get("rollNumber")).isEqualTo("123123123");
  }

  @Test
  void when_bank_details_missing() throws IOException {
    Map<String, Object> map = readTestFile("mapping/bankDetailsMissing.json");
    final RegistrationSchema130 registrationData = registrationDataMarshaller.marshallRegistrationData(map);
    assertThat(registrationData).isNotNull();
  }

  @Test
  void when_motability_details_missing() throws IOException {
    Map<String, Object> map = readTestFile("mapping/motabilityDetailsMissing.json");
    final RegistrationSchema130 registrationData = registrationDataMarshaller.marshallRegistrationData(map);
    assertThat(registrationData).isNotNull();
  }

  @Test
  void when_unable_to_marshall() {
    assertThatThrownBy(() -> registrationDataMarshaller.marshallRegistrationData("{bad, message}"))
        .isInstanceOf(RegistrationDataNotValid.class)
        .hasMessage("Registration data not valid. Problem when marshalling to objects.");
  }

  @Test
  void when_not_valid() throws IOException {
    Map<String, Object> map = readTestFile("mapping/invalidRegistrationData.json");
    assertThatThrownBy(() -> registrationDataMarshaller.marshallRegistrationData(map))
        .isInstanceOf(RegistrationDataNotValid.class)
        .hasMessage("Registration data not valid. Constraint violations present.");
  }
}
