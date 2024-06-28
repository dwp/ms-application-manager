package uk.gov.dwp.health.pip.application.manager.service.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.RegistrationSchema140;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v3.dto.AboutYourHealthDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v3.dto.HealthProfessionalDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static support.FileUtils.getNewRegistrationDataFromFile;
import static support.FileUtils.getRegistrationDataFromFile;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Tag("unit")
class AboutYourHealthMapperV3Test {

  private AboutYourHealthMapperV3 aboutYourHealthMapperV3;
  private RegistrationSchema140 registrationSchema;

  @BeforeEach
  void beforeEach() {
    var formCommons = new FormCommons(new ObjectMapper());
    aboutYourHealthMapperV3 = new AboutYourHealthMapperV3(new AddressMapperV3(formCommons));
  }

  @Nested
  class AboutYourHealthToApiTest {

    @BeforeEach
    void beforeEach() throws IOException {
      registrationSchema = getNewRegistrationDataFromFile("mapping/newValidRegistrationData.json");
    }

    @Test
    void when_valid_data() {
      var aboutYourHealthDto =
          aboutYourHealthMapperV3.toDto(registrationSchema.getAboutYourHealth());

      verifyAboutYourHealth(aboutYourHealthDto);
    }

    private void verifyAboutYourHealth(AboutYourHealthDto aboutYourHealthDto) {
      var conditions = aboutYourHealthDto.getConditions();
      assertThat(conditions).hasSize(3);
      assertThat(conditions.get(0))
              .isEqualTo(
                      "RTKctXFpeDPGM8Y37FWoO8hYqt1TAj11NlT486pd9qtxItRhJ9eUYQ7eSOiFJlZHuaTBwqIL9KIutQ43a3VRZgAxcmykGlX4duA1mpvv9HlBSjUWhcfc1oCpJKBBZAJy0BwPCGcvB7AVr679rcAiLPJpdzAHz711QhwmtPMPSnuEBf5DaycxxqeXMRgPgNRUxYrSUQKO");
      assertThat(conditions.get(1))
              .isEqualTo(
                      "Az5rCDj6vsNPUzcT1TOFdtKEeeWzEMNkARpo2GzAtvHQLHKhj9JqwKx0KfVKYG9WIOmkszGUhhiTjwRULEc5E9IogknMGoVYYwrrSvaLSTMa4ay0V67lGXh1BkfK6x9EgRDQWSblWdZlQQdT4tfrpn");
      assertThat(conditions.get(2)).isEqualTo("AA");

      var careAccommodationDto = aboutYourHealthDto.getCareAccommodation();
      assertThat(careAccommodationDto.getAccommodationType().getValue()).isEqualTo("hospital");
      assertThat(careAccommodationDto.getAdmissionDate()).isEqualTo("2021-01-01");
      var addressDto = careAccommodationDto.getAddress();
      assertThat(addressDto.getLine1()).isEqualTo("accommodation-name");
      assertThat(addressDto.getLine2()).isEqualTo("sPb0fbsQW1oWxg4BFtUdEAODG2qJM5xGZiL");
      assertThat(addressDto.getLine3()).isEqualTo("sPb0fbsQW1oWxg4BFtUdEAODG2qJM5xGZiL");
      assertThat(addressDto.getTown()).isEqualTo("sPb0fbsQW1oWxg4BFtUdEAODG2qJM5xGZiL");
      assertThat(addressDto.getCounty()).isEqualTo("sPb0fbsQW1oWxg4BFtUdEAODG2qJM5xGZiL");
      assertThat(addressDto.getPostcode()).isEqualTo("AB1 1AB");
      assertThat(addressDto.getCountry()).isEqualTo("England");

      assertThat(aboutYourHealthDto.isHcpContactConsent()).isTrue();
      assertThat(aboutYourHealthDto.isHcpShareConsent()).isTrue();
    }
  }

  @Nested
  class HealthProfessionalToApiTest {

    @BeforeEach
    void beforeEach() throws IOException {
      registrationSchema =
          getRegistrationDataFromFile("mapping/healthProfessionalConsentGiven.json");
    }

    @Test
    void when_valid_data() {
      var aboutYourHealthDto =
          aboutYourHealthMapperV3.toDto(registrationSchema.getAboutYourHealth());

      verifyHealthProfessional(aboutYourHealthDto);
    }

    private void verifyHealthProfessional(AboutYourHealthDto aboutYourHealthDto) {
      var healthProfessionals = aboutYourHealthDto.getHealthProfessionals();
      assertThat(healthProfessionals).hasSize(3);

      assertThat(aboutYourHealthDto.getHealthProfessionals().get(0).getName()).isEqualTo("HP-DR-1");
      assertThat(aboutYourHealthDto.getHealthProfessionals().get(1).getName()).isEqualTo("HP-DR-2");
      assertThat(aboutYourHealthDto.getHealthProfessionals().get(2).getName()).isEqualTo("HP-DR-3");

      verifyHealthProfessionalDetails(aboutYourHealthDto.getHealthProfessionals().get(0));
      verifyHealthProfessionalDetails(aboutYourHealthDto.getHealthProfessionals().get(1));
      verifyHealthProfessionalDetails(aboutYourHealthDto.getHealthProfessionals().get(2));
    }

    private void verifyHealthProfessionalDetails(HealthProfessionalDto healthProfessionalDto) {
      assertThat(healthProfessionalDto.getProfession()).isEqualTo("Doctor");
      assertThat(healthProfessionalDto.getPhoneNumber()).isEqualTo("07777777777");
      assertThat(healthProfessionalDto.getLastContact()).isEqualTo("01/01/2021");
      var addressDto = healthProfessionalDto.getAddress();
      assertThat(addressDto.getLine1()).isEqualTo("sPb0fbsQW1oWxg4BFtUdEAODG2qJM5xGZiL");
      assertThat(addressDto.getLine2()).isEqualTo("sPb0fbsQW1oWxg4BFtUdEAODG2qJM5xGZiL");
      assertThat(addressDto.getLine3()).isEqualTo("sPb0fbsQW1oWxg4BFtUdEAODG2qJM5xGZiL");
      assertThat(addressDto.getTown()).isEqualTo("sPb0fbsQW1oWxg4BFtUdEAODG2qJM5xGZiL");
      assertThat(addressDto.getCounty()).isEqualTo("sPb0fbsQW1oWxg4BFtUdEAODG2qJM5xGZiL");
      assertThat(addressDto.getPostcode()).isEqualTo("AB1 1AB");
      assertThat(addressDto.getCountry()).isEqualTo("England");
    }
  }
}
