package uk.gov.dwp.health.pip.application.manager.api.registration;

import org.junit.jupiter.api.Test;
import uk.gov.dwp.health.pip.application.manager.api.ApiTest;
import uk.gov.dwp.health.pip.application.manager.config.MongoClientConnection;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.entity.FormData;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v2.dto.AboutYourHealthDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v2.dto.AdditionalSupportDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v2.dto.HealthProfessionalDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v2.dto.PersonalDetailsDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v2.dto.RegistrationDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v2.dto.ResidenceAndPresenceDto;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static support.FileUtils.readTestFileAsObject;
import static uk.gov.dwp.health.pip.application.manager.utils.UrlBuilderUtil.buildGetRegistrationByIdV2Url;

class GetRegistrationByIdV2IT extends ApiTest {

  @Test
  void shouldReturn200StatusCodeAndCorrectResponseBody() throws IOException {
    var formDataAsObject = readTestFileAsObject("api-test/validRegistrationData.json");
    var application =
        Application.builder()
            .registrationData(FormData.builder().data(formDataAsObject).build())
            .dateRegistrationSubmitted(LocalDate.of(2022, Month.MARCH, 27))
            .build();
    var mongoTemplate = MongoClientConnection.getMongoTemplate();
    var savedApplication = mongoTemplate.save(application, "application");

    var url = buildGetRegistrationByIdV2Url(savedApplication.getId());
    int actualResponseCode = getRequest(url).statusCode();
    RegistrationDto registrationDto = extractGetRequest(url, RegistrationDto.class);

    assertThat(actualResponseCode).isEqualTo(200);
    assertThat(registrationDto.getSubmissionDate()).isEqualTo("2022-03-27");

    verifyPersonalDetails(registrationDto.getPersonalDetails());
    verifyAboutYourHealth(registrationDto.getAboutYourHealth());
    verifyResidenceAndPresence(registrationDto.getResidenceAndPresence());
    verifyAdditionalSupport(registrationDto.getAdditionalSupport());
  }

  private void verifyPersonalDetails(PersonalDetailsDto personalDetailsDto) {
    assertThat(personalDetailsDto.getSurname()).isEqualTo("Azzzle");
    assertThat(personalDetailsDto.getFirstName()).isEqualTo("Azzzam");
    assertThat(personalDetailsDto.getDateOfBirth()).isEqualTo("2000-01-01");
    assertThat(personalDetailsDto.getNationalInsuranceNumber()).isEqualTo("RN000020D");

    var addressDto = personalDetailsDto.getAddress();
    assertThat(addressDto.getLine1()).isEqualTo("NQC2JhPWurvWRP1ZL7Goz2LKBFDufFjeP3o");
    assertThat(addressDto.getLine2()).isEqualTo("NQC2JhPWurvWRP1ZL7Goz2LKBFDufFjeP3o");
    assertThat(addressDto.getLine3()).isEqualTo("NQC2JhPWurvWRP1ZL7Goz2LKBFDufFjeP3o");
    assertThat(addressDto.getTown()).isEqualTo("NQC2JhPWurvWRP1ZL7Goz2LKBFDufFjeP3o");
    assertThat(addressDto.getCounty()).isEqualTo("QvBQYuxiXXVytGCxzVllpgTJKhRQq");
    assertThat(addressDto.getPostcode()).isEqualTo("AB1 1AB");
    assertThat(addressDto.getCountry()).isEqualTo("England");

    var alternativeAddress = personalDetailsDto.getAlternativeAddress();
    assertThat(alternativeAddress.getLine1()).isEqualTo("YdvDhtAsLghPXAgtbprXPZkhnfLTBSX");
    assertThat(alternativeAddress.getLine2()).isEqualTo("NQC2JhPWurvWRP1ZL7Goz2LKBFDufFjeP3o");
    assertThat(alternativeAddress.getLine3()).isEqualTo("NQC2JhPWurvWRP1ZL7Goz2LKBFDufFjeP3o");
    assertThat(alternativeAddress.getTown()).isEqualTo("NQC2JhPWurvWRP1ZL7Goz2LKBFDufFjeP3o");
    assertThat(alternativeAddress.getCounty()).isEqualTo("QvBQYuxiXXVytGCxzVllpgTJKhRQq");
    assertThat(alternativeAddress.getPostcode()).isEqualTo("AB1 1AB");
    assertThat(alternativeAddress.getCountry()).isEqualTo("England");

    var contactDto = personalDetailsDto.getContact();
    assertThat(contactDto.getMobileNumber()).isEqualTo("07777777777");
    assertThat(contactDto.getAlternativeNumber()).isEqualTo("01139999999");
    assertThat(contactDto.getTextPhone()).isEqualTo("01139999999");
    assertThat(contactDto.getSmsUpdates()).isEqualTo("Yes");

    var alternateFormatDto = personalDetailsDto.getAlternateFormat();
    assertThat(alternateFormatDto.getFormatType().getValue()).isEqualTo("other");
    assertThat(alternateFormatDto.getOption().getValue()).isEqualTo("colouredPaper");
    assertThat(alternateFormatDto.getAdditionalInfo()).isEqualTo("alt-format-additional-info");
  }

  private void verifyAboutYourHealth(AboutYourHealthDto aboutYourHealthDto) {
    List<String> conditions = aboutYourHealthDto.getConditions();
    assertThat(conditions.size()).isEqualTo(3);
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

    var healthProfessionals = aboutYourHealthDto.getHealthProfessionals();
    assertThat(healthProfessionals.size()).isEqualTo(2);

    verifyHealthProfessionalDetails1(aboutYourHealthDto.getHealthProfessionals().get(0));
    verifyHealthProfessionalDetails2(aboutYourHealthDto.getHealthProfessionals().get(1));
  }

  private void verifyHealthProfessionalDetails1(HealthProfessionalDto healthProfessionalDto) {
    assertThat(healthProfessionalDto.getName()).isEqualTo("HP-DR-1");
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

  private void verifyHealthProfessionalDetails2(HealthProfessionalDto healthProfessionalDto) {
    assertThat(healthProfessionalDto.getName()).isEqualTo("HP-DR-2");
    assertThat(healthProfessionalDto.getProfession()).isEqualTo("Do");
    assertThat(healthProfessionalDto.getPhoneNumber()).isEqualTo("07777777777");
    assertThat(healthProfessionalDto.getLastContact()).isEqualTo("2 months ago");
    var addressDto = healthProfessionalDto.getAddress();
    assertThat(addressDto.getLine1()).isEqualTo("ABC");
    assertThat(addressDto.getLine2()).isEqualTo("ABC");
    assertThat(addressDto.getLine3()).isEqualTo("ABC");
    assertThat(addressDto.getTown()).isEqualTo("ABC");
    assertThat(addressDto.getCounty()).isEqualTo("ABC");
    assertThat(addressDto.getPostcode()).isEqualTo("AB1 1AB");
    assertThat(addressDto.getCountry()).isEqualTo("England");
  }

  private void verifyResidenceAndPresence(ResidenceAndPresenceDto residenceAndPresenceDto) {
    assertThat(residenceAndPresenceDto.getNationality()).isEqualTo("French");
    assertThat(residenceAndPresenceDto.getResidentBeforeBrexit()).isEqualTo("Yes");
    assertThat(residenceAndPresenceDto.getInUkTwoOutOfThreeYears()).isEqualTo("Yes");
    assertThat(residenceAndPresenceDto.isReceivingPensionsOrBenefitsFromEEA()).isTrue();
    assertThat(residenceAndPresenceDto.isPayingInsuranceEEA()).isFalse();
  }

  private void verifyAdditionalSupport(AdditionalSupportDto additionalSupportDto) {
    assertThat(additionalSupportDto.isHelpCommunicating()).isTrue();
    assertThat(additionalSupportDto.isHelpUnderstandingLetters()).isTrue();
    assertThat(additionalSupportDto.getHelper().getFirstName())
        .isEqualTo("T7vQ1SD6YL153OmQWxuzdoskGVLCToeTest");
    assertThat(additionalSupportDto.getHelper().getSurname())
        .isEqualTo("T7vQ1SD6YL153OmQWxuzdoskGVLCToeTest");
  }
}
