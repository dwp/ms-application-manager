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
import static uk.gov.dwp.health.pip.application.manager.utils.UrlBuilderUtil.*;

public class PutApplicationIT extends ApiTest {
  CreatedApplication createdApplication;
  Registration registrationRequest;
  String url;

  @BeforeEach
  public void createHealthDisabilityApplication() {
    createApplicationWithHealthDisabilityStatus();

    url = buildPutHealthDisabilityUrl(createdApplication.getApplicationId());
  }

  @Test
  public void shouldReturn200StatusCode() {
    UpdateHealthDisability updateHealthDisabilityRequest = UpdateHealthDisability.builder().build();

    int actualStatusCode = putRequest(url, updateHealthDisabilityRequest).statusCode();

    assertThat(actualStatusCode).isEqualTo(200);
  }

  @Test
  public void shouldReturn403IfApplicationStatusNotHealthAndDisability() {
    createApplicationWithRegistrationStatus();
    UpdateHealthDisability updateHealthDisabilityRequest = UpdateHealthDisability.builder().build();

    int actualStatusCode = putRequest(buildPutHealthDisabilityUrl(createdApplication.getApplicationId()), updateHealthDisabilityRequest).statusCode();

    assertThat(actualStatusCode).isEqualTo(403);
  }

  @Test
  public void shouldReturn404IfApplicationDoesNotExist() {
    UpdateHealthDisability updateHealthDisabilityRequest = UpdateHealthDisability.builder().build();

    int actualStatusCode = putRequest(buildPutHealthDisabilityUrl(RandomStringUtil.generate(24)), updateHealthDisabilityRequest).statusCode();

    assertThat(actualStatusCode).isEqualTo(404);
  }

  private void createApplicationWithHealthDisabilityStatus() {
    //Create Application
    registrationRequest = Registration.builder().claimantId(RandomStringUtil.generate(24)).build();
    createdApplication = extractPostRequest(buildPostApplicationUrl(), registrationRequest, CreatedApplication.class);

    //Update and submit Registration data
    UpdateRegistration updatedApplicationBody = UpdateRegistration.builder().build();
    putRequest(buildPutRegistrationUrl(createdApplication.getApplicationId()), updatedApplicationBody);
    putRequest(buildPutRegistrationSubmissionUrl(createdApplication.getApplicationId()), updatedApplicationBody);
  }

  private void createApplicationWithRegistrationStatus() {
    //Create Application
    String claimantId = RandomStringUtil.generate(24);
    registrationRequest = Registration.builder().claimantId(claimantId).build();
    createdApplication = extractPostRequest(buildPostApplicationUrl(), registrationRequest, CreatedApplication.class);
    //Update Registration data
    UpdateRegistration updatedApplicationBody = UpdateRegistration.builder().build();
    putRequest(buildPutRegistrationUrl(createdApplication.getApplicationId()), updatedApplicationBody);
  }
}
