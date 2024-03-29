package uk.gov.dwp.health.pip.application.manager.api.healthdisability;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.health.pip.application.manager.api.ApiTest;
import uk.gov.dwp.health.pip.application.manager.requestmodels.healthdisability.UpdateHealthDisability;
import uk.gov.dwp.health.pip.application.manager.requestmodels.registration.Registration;
import uk.gov.dwp.health.pip.application.manager.requestmodels.registration.UpdateRegistration;
import uk.gov.dwp.health.pip.application.manager.responsemodels.CreatedApplication;
import uk.gov.dwp.health.pip.application.manager.utils.RandomStringUtil;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static uk.gov.dwp.health.pip.application.manager.utils.UrlBuilderUtil.buildPostApplicationUrl;
import static uk.gov.dwp.health.pip.application.manager.utils.UrlBuilderUtil.buildPutHealthDisabilitySubmissionV2Url;
import static uk.gov.dwp.health.pip.application.manager.utils.UrlBuilderUtil.buildPutHealthDisabilityUrl;
import static uk.gov.dwp.health.pip.application.manager.utils.UrlBuilderUtil.buildPutRegistrationSubmissionUrl;
import static uk.gov.dwp.health.pip.application.manager.utils.UrlBuilderUtil.buildPutRegistrationUrl;

class PutApplicationSubmissionV2IT extends ApiTest {
  CreatedApplication createdApplication;
  String healthDisabilityURL;
  String healthDisabilitySubmissionURL;

  @BeforeEach
  public void createHealthDisabilityApplication() {
    createApplicationWithHealthDisabilityStatus();

    healthDisabilityURL = buildPutHealthDisabilityUrl(createdApplication.getApplicationId());
    healthDisabilitySubmissionURL =
        buildPutHealthDisabilitySubmissionV2Url(
            createdApplication.getApplicationId(), createdApplication.getApplicationId());
  }

  @Test
  void shouldReturn202StatusCode() {
    var updateHealthDisability = UpdateHealthDisability.builder().build();
    putRequest(healthDisabilityURL, updateHealthDisability);

    int actualStatusCode =
        putRequest(healthDisabilitySubmissionURL, updateHealthDisability).statusCode();
    assertThat(actualStatusCode).isEqualTo(202);
  }

  @Test
  void shouldReturn404IfApplicationDoesNotExist() {
    String applicationId = RandomStringUtil.generate(24);

    UpdateHealthDisability updateHealthDisabilityRequest = UpdateHealthDisability.builder().build();
    putRequest(healthDisabilityURL, updateHealthDisabilityRequest);

    int actualStatusCode =
        putRequest(
                buildPutHealthDisabilitySubmissionV2Url(applicationId, applicationId),
                updateHealthDisabilityRequest)
            .statusCode();

    assertThat(actualStatusCode).isEqualTo(404);
  }

  private void createApplicationWithHealthDisabilityStatus() {
    String claimantId = RandomStringUtil.generate(24);
    Registration registration = Registration.builder().claimantId(claimantId).build();
    createdApplication =
        extractPostRequest(buildPostApplicationUrl(), registration, CreatedApplication.class);

    UpdateRegistration updatedApplicationBody = UpdateRegistration.builder().build();
    putRequest(
        buildPutRegistrationUrl(createdApplication.getApplicationId()), updatedApplicationBody);
    putRequest(
        buildPutRegistrationSubmissionUrl(createdApplication.getApplicationId()),
        updatedApplicationBody);
  }
}
