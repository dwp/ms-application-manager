package uk.gov.dwp.health.pip.application.manager.api.registration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import uk.gov.dwp.health.pip.application.manager.api.ApiTest;
import uk.gov.dwp.health.pip.application.manager.config.MongoClientConnection;
import uk.gov.dwp.health.pip.application.manager.constant.ApplicationState;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.entity.State;
import uk.gov.dwp.health.pip.application.manager.requestmodels.registration.FormData;
import uk.gov.dwp.health.pip.application.manager.requestmodels.registration.FormData.PersonalDetails;
import uk.gov.dwp.health.pip.application.manager.requestmodels.registration.Registration;
import uk.gov.dwp.health.pip.application.manager.requestmodels.registration.UpdateRegistration;
import uk.gov.dwp.health.pip.application.manager.responsemodels.CreatedApplication;
import uk.gov.dwp.health.pip.application.manager.utils.RandomStringUtil;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static uk.gov.dwp.health.pip.application.manager.utils.UrlBuilderUtil.*;

public class PutApplicationSubmissionIT extends ApiTest {
  CreatedApplication application;
  String registrationURL;
  String submissionURL;

  @BeforeEach
  public void createApplication() {
    Registration registration =
        Registration.builder().claimantId(RandomStringUtil.generate(24)).build();
    application =
        extractPostRequest(buildPostApplicationUrl(), registration, CreatedApplication.class);

    registrationURL = buildPutRegistrationUrl(application.getApplicationId());
    submissionURL = buildPutRegistrationSubmissionUrl(application.getApplicationId());
  }

  @Test
  public void shouldReturn202StatusCodeWhenSubmissionIsValid() {
    UpdateRegistration updatedRegistration = UpdateRegistration.builder().build();
    putRequest(registrationURL, updatedRegistration);

    int actualResponseCode = putRequest(submissionURL, updatedRegistration).statusCode();

    assertThat(actualResponseCode).isEqualTo(202);
  }

  @Test
  public void shouldReturn422StatusCodeWhenAdditionalSupportInfoIsNotPresent() {
    FormData.AdditionalSupport additionalSupport =
        FormData.AdditionalSupport.builder()
            .helpCommunicating(null)
            .helpUnderstandingLetters(null)
            .build();
    FormData formData = FormData.builder().additionalSupport(additionalSupport).build();
    UpdateRegistration updatedRegistration =
        UpdateRegistration.builder().formData(formData).build();
    putRequest(registrationURL, updatedRegistration);

    int actualResponseCode = putRequest(submissionURL, updatedRegistration).statusCode();

    assertThat(actualResponseCode).isEqualTo(422);
  }

  @Test
  public void shouldReturn422StatusCodeWhenBankDetailsNotPresent() {
    PersonalDetails personalDetails = PersonalDetails.builder().bankDetails(null).build();
    FormData formData = FormData.builder().personalDetails(personalDetails).build();
    UpdateRegistration updatedRegistration =
        UpdateRegistration.builder().formData(formData).build();
    putRequest(registrationURL, updatedRegistration);

    int actualResponseCode = putRequest(submissionURL, updatedRegistration).statusCode();

    assertThat(actualResponseCode).isEqualTo(422);
  }

  @Test
  public void shouldReturn422StatusCodeWhenMotabilityDetailsNotPresent() {
    FormData formData = FormData.builder().motabilityScheme(null).build();
    UpdateRegistration updatedRegistration =
        UpdateRegistration.builder().formData(formData).build();
    putRequest(registrationURL, updatedRegistration);

    int actualResponseCode = putRequest(submissionURL, updatedRegistration).statusCode();

    assertThat(actualResponseCode).isEqualTo(422);
  }

  @Test
  public void shouldReturn202StatusCodeWhenAdditionalSupportHelperInfoIsNotPresent() {
    FormData.AdditionalSupport additionalSupport =
        FormData.AdditionalSupport.builder().helperDetails(null).build();
    FormData formData = FormData.builder().additionalSupport(additionalSupport).build();
    UpdateRegistration updatedRegistration =
        UpdateRegistration.builder().formData(formData).build();
    putRequest(registrationURL, updatedRegistration);

    int actualResponseCode = putRequest(submissionURL, updatedRegistration).statusCode();

    assertThat(actualResponseCode).isEqualTo(202);
  }

  @Test
  public void shouldReturn422StatusCodeWhenPersonalDetailsInfoIsNotPresent() {
    FormData.PersonalDetails personalDetails =
        FormData.PersonalDetails.builder()
            .nino(null)
            .dob(null)
            .firstname(null)
            .surname(null)
            .build();
    FormData formData = FormData.builder().personalDetails(personalDetails).build();
    UpdateRegistration updatedRegistration =
        UpdateRegistration.builder().formData(formData).build();
    putRequest(registrationURL, updatedRegistration);

    int actualResponseCode = putRequest(submissionURL, updatedRegistration).statusCode();

    assertThat(actualResponseCode).isEqualTo(422);
  }

  @Test
  public void shouldReturn202StatusCodeWhenAlternativeAddressIsNotPresent() {
    FormData.PersonalDetails personalDetails =
        FormData.PersonalDetails.builder().alternativeAddress(null).build();
    FormData formData = FormData.builder().personalDetails(personalDetails).build();
    UpdateRegistration updatedRegistration =
        UpdateRegistration.builder().formData(formData).build();
    putRequest(registrationURL, updatedRegistration);

    int actualResponseCode = putRequest(submissionURL, updatedRegistration).statusCode();

    assertThat(actualResponseCode).isEqualTo(202);
  }

  @Test
  public void shouldReturn403StatusCodeWhenApplicationNotInRegistrationState() {
    MongoTemplate mongoTemplate = MongoClientConnection.getMongoTemplate();
    var application =
        Application.builder()
            .id("600000000000000000000403")
            .state(State.builder().current(ApplicationState.SUBMITTED.name()).build())
            .build();
    mongoTemplate.save(application, "application");
    String url = buildPutRegistrationSubmissionUrl("600000000000000000000403");

    int actualResponseCode = putRequest(url, application).statusCode();

    assertThat(actualResponseCode).isEqualTo(403);
  }

  @Test
  public void shouldReturn404StatusCodeWhenApplicationDoesNotExist() {
    UpdateRegistration updatedRegistration = UpdateRegistration.builder().build();

    int actualResponseCode =
        putRequest(
                buildPutRegistrationSubmissionUrl(RandomStringUtil.generate(24)),
                updatedRegistration)
            .statusCode();

    assertThat(actualResponseCode).isEqualTo(404);
  }
}
