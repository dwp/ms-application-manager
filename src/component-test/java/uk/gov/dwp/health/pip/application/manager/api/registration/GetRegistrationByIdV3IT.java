package uk.gov.dwp.health.pip.application.manager.api.registration;

import org.junit.jupiter.api.Test;
import uk.gov.dwp.health.pip.application.manager.api.ApiTest;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v3.dto.AboutYourHealthDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v3.dto.AdditionalSupportDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v3.dto.HealthProfessionalDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v3.dto.HistoryDto.StateEnum;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v3.dto.PersonalDetailsDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v3.dto.RegistrationDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v3.dto.RegistrationDto.LanguageEnum;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v3.dto.ResidenceAndPresenceDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v3.dto.StateDto.CurrentStateEnum;
import uk.gov.dwp.health.pip.application.manager.requestmodels.registration.Registration;
import uk.gov.dwp.health.pip.application.manager.requestmodels.registration.UpdateRegistration;
import uk.gov.dwp.health.pip.application.manager.responsemodels.CreatedApplication;
import uk.gov.dwp.health.pip.application.manager.utils.RandomStringUtil;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.dwp.health.pip.application.manager.utils.UrlBuilderUtil.buildGetRegistrationByIdV3Url;
import static uk.gov.dwp.health.pip.application.manager.utils.UrlBuilderUtil.buildPostApplicationUrl;
import static uk.gov.dwp.health.pip.application.manager.utils.UrlBuilderUtil.buildPutRegistrationSubmissionUrl;
import static uk.gov.dwp.health.pip.application.manager.utils.UrlBuilderUtil.buildPutRegistrationUrl;

class GetRegistrationByIdV3IT extends ApiTest {

  private CreatedApplication createdApplication;
  private final String line1 = "123";
  private final String line2 = "Headrow";
  private final String town = "Leeds";
  private final String county = "West Yorkshire";
  private final String postCode = "LS1 1AB";
  private final String country = "England";
  private final String phoneNo = "07777777777";

  @Test
  void shouldReturn200StatusCodeAndCorrectResponseBody() {
    createApplicationWithHealthDisabilityStatus();

    var url = buildGetRegistrationByIdV3Url(createdApplication.getApplicationId());
    int actualResponseCode = getRequest(url).statusCode();
    RegistrationDto registrationDto = extractGetRequest(url, RegistrationDto.class);

    assertThat(actualResponseCode).isEqualTo(200);
    assertThat(registrationDto.getSubmissionDate()).isEqualTo(LocalDate.now().toString());
    assertThat(registrationDto.getEffectiveFrom()).isEqualTo(LocalDate.now().toString());
    assertThat(registrationDto.getEffectiveTo()).isEqualTo(LocalDate.now().plusDays(90).toString());
    assertThat(registrationDto.getLanguage()).isEqualTo(LanguageEnum.EN);
    assertThat(registrationDto.getStateDto().getCurrentState())
        .isEqualTo(CurrentStateEnum.HEALTH_AND_DISABILITY);
    assertThat(registrationDto.getStateDto().getHistory()).hasSize(2);
    assertThat(registrationDto.getStateDto().getHistory().get(0).getState())
        .isEqualTo(StateEnum.REGISTRATION);
    assertThat(registrationDto.getStateDto().getHistory().get(1).getState())
        .isEqualTo(StateEnum.HEALTH_AND_DISABILITY);

    verifyPersonalDetails(registrationDto.getPersonalDetails());
    verifyAboutYourHealth(registrationDto.getAboutYourHealth());
    verifyResidenceAndPresence(registrationDto.getResidenceAndPresence());
    verifyAdditionalSupport(registrationDto.getAdditionalSupport());
  }

  @Test
  void shouldReturn400StatusCodeBadRequest() {
    assertThat(getRequest(buildGetRegistrationByIdV3Url("!@123")).statusCode()).isEqualTo(400);
  }

  private void verifyPersonalDetails(PersonalDetailsDto personalDetailsDto) {
    assertThat(personalDetailsDto.getSurname()).isEqualTo("Smithzzz");
    assertThat(personalDetailsDto.getFirstName()).isEqualTo("Alanzzz");
    assertThat(personalDetailsDto.getDateOfBirth()).isEqualTo("2000-01-01");
    assertThat(personalDetailsDto.getNationalInsuranceNumber()).isEqualTo("RN000009C");
    var addressDto = personalDetailsDto.getAddress();
    assertThat(addressDto.getLine1()).isEqualTo(line1);
    assertThat(addressDto.getLine2()).isEqualTo(line2);
    assertThat(addressDto.getLine3()).isNull();
    assertThat(addressDto.getTown()).isEqualTo(town);
    assertThat(addressDto.getCounty()).isEqualTo(county);
    assertThat(addressDto.getPostcode()).isEqualTo(postCode);
    assertThat(addressDto.getCountry()).isEqualTo(country);

    var alternativeAddress = personalDetailsDto.getAlternativeAddress();
    assertThat(alternativeAddress.getLine1()).isEqualTo(line1);
    assertThat(alternativeAddress.getLine2()).isEqualTo(line2);
    assertThat(addressDto.getLine3()).isNull();
    assertThat(alternativeAddress.getTown()).isEqualTo(town);
    assertThat(alternativeAddress.getCounty()).isEqualTo(county);
    assertThat(alternativeAddress.getPostcode()).isEqualTo(postCode);
    assertThat(alternativeAddress.getCountry()).isEqualTo(country);

    var contactDto = personalDetailsDto.getContact();
    assertThat(contactDto.getMobileNumber()).isEqualTo(phoneNo);
    assertThat(contactDto.getAlternativeNumber()).isEqualTo(phoneNo);
    assertThat(contactDto.getTextPhone()).isEqualTo(phoneNo);
    assertThat(contactDto.getSmsUpdates()).isEqualTo("Yes");
  }

  private void verifyAboutYourHealth(AboutYourHealthDto aboutYourHealthDto) {
    List<String> conditions = aboutYourHealthDto.getConditions();
    assertThat(conditions).hasSize(1);
    assertThat(conditions.get(0)).isEqualTo("Illness.");
    var careAccommodationDto = aboutYourHealthDto.getCareAccommodation();
    assertThat(careAccommodationDto.getAccommodationType().getValue()).isEqualTo("other");
    assertThat(careAccommodationDto.getAdmissionDate()).isEqualTo("2022-05-01");

    var addressDto = careAccommodationDto.getAddress();
    assertThat(addressDto.getLine1()).isEqualTo("Hostel Accommodation");
    assertThat(addressDto.getLine2()).isEqualTo(line1);
    assertThat(addressDto.getLine3()).isEqualTo(line2);
    assertThat(addressDto.getTown()).isEqualTo(town);
    assertThat(addressDto.getCounty()).isEqualTo(county);
    assertThat(addressDto.getPostcode()).isEqualTo(postCode);
    assertThat(addressDto.getCountry()).isEqualTo(country);
    assertThat(aboutYourHealthDto.isHcpContactConsent()).isTrue();
    assertThat(aboutYourHealthDto.isHcpShareConsent()).isTrue();

    var healthProfessionals = aboutYourHealthDto.getHealthProfessionals();
    assertThat(healthProfessionals).hasSize(2);
    verifyHealthProfessionalDetails1(aboutYourHealthDto.getHealthProfessionals().get(0));
    verifyHealthProfessionalDetails2(aboutYourHealthDto.getHealthProfessionals().get(1));
  }

  private void verifyHealthProfessionalDetails1(HealthProfessionalDto healthProfessionalDto) {
    assertThat(healthProfessionalDto.getName()).isEqualTo("Dr Alazzz");
    assertThat(healthProfessionalDto.getProfession()).isEqualTo("Doctor");
    assertThat(healthProfessionalDto.getPhoneNumber()).isEqualTo(phoneNo);
    assertThat(healthProfessionalDto.getLastContact())
        .isEqualTo("Last spoken to them on the last Fri of last month.");
    var addressDto = healthProfessionalDto.getAddress();
    assertThat(addressDto.getLine1()).isEqualTo(line1);
    assertThat(addressDto.getLine2()).isEqualTo(line2);
    assertThat(addressDto.getTown()).isEqualTo(town);
    assertThat(addressDto.getCounty()).isEqualTo(county);
    assertThat(addressDto.getPostcode()).isEqualTo(postCode);
    assertThat(addressDto.getCountry()).isEqualTo(country);
  }

  private void verifyHealthProfessionalDetails2(HealthProfessionalDto healthProfessionalDto) {
    assertThat(healthProfessionalDto.getName()).isEqualTo("Dr Stevvve");
    assertThat(healthProfessionalDto.getProfession()).isEqualTo("Doctor");
    assertThat(healthProfessionalDto.getPhoneNumber()).isEqualTo(phoneNo);
    assertThat(healthProfessionalDto.getLastContact())
        .isEqualTo("Last spoken to them on the last Fri of last month.");
    var addressDto = healthProfessionalDto.getAddress();
    assertThat(addressDto.getLine1()).isEqualTo(line1);
    assertThat(addressDto.getLine2()).isEqualTo(line2);
    assertThat(addressDto.getTown()).isEqualTo(town);
    assertThat(addressDto.getCounty()).isEqualTo(county);
    assertThat(addressDto.getPostcode()).isEqualTo(postCode);
    assertThat(addressDto.getCountry()).isEqualTo(country);
  }

  private void verifyResidenceAndPresence(ResidenceAndPresenceDto residenceAndPresenceDto) {
    assertThat(residenceAndPresenceDto.getNationality()).isEqualTo("Filipino");
    assertThat(residenceAndPresenceDto.getResidentBeforeBrexit()).isEqualTo("Yes");
    assertThat(residenceAndPresenceDto.getInUkTwoOutOfThreeYears()).isEqualTo("Yes");
    assertThat(residenceAndPresenceDto.isReceivingPensionsOrBenefitsFromEEA()).isTrue();
    assertThat(residenceAndPresenceDto.isPayingInsuranceEEA()).isTrue();
  }

  private void verifyAdditionalSupport(AdditionalSupportDto additionalSupportDto) {
    assertThat(additionalSupportDto.isHelpCommunicating()).isTrue();
    assertThat(additionalSupportDto.isHelpUnderstandingLetters()).isTrue();
    assertThat(additionalSupportDto.getHelper().getFirstName()).isEqualTo("Florenzzze");
    assertThat(additionalSupportDto.getHelper().getSurname()).isEqualTo("Nightingalezzz");
  }

  private void createApplicationWithHealthDisabilityStatus() {
    Registration applicationRequest =
        Registration.builder().claimantId(RandomStringUtil.generate(24)).build();
    createdApplication =
        extractPostRequest(buildPostApplicationUrl(), applicationRequest, CreatedApplication.class);

    UpdateRegistration updatedApplicationBody = UpdateRegistration.builder().build();
    putRequest(
        buildPutRegistrationUrl(createdApplication.getApplicationId()), updatedApplicationBody);
    putRequest(
        buildPutRegistrationSubmissionUrl(createdApplication.getApplicationId()),
        updatedApplicationBody);
  }
}
