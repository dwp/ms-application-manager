package uk.gov.dwp.health.pip.application.manager.api.healthdisability;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.health.pip.application.manager.api.ApiTest;
import uk.gov.dwp.health.pip.application.manager.requestmodels.healthdisability.FormData;
import uk.gov.dwp.health.pip.application.manager.requestmodels.healthdisability.UpdateHealthDisability;
import uk.gov.dwp.health.pip.application.manager.requestmodels.registration.Registration;
import uk.gov.dwp.health.pip.application.manager.requestmodels.registration.UpdateRegistration;
import uk.gov.dwp.health.pip.application.manager.responsemodels.CreatedApplication;
import uk.gov.dwp.health.pip.application.manager.utils.RandomStringUtil;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.awaitility.Awaitility.await;
import static uk.gov.dwp.health.pip.application.manager.utils.UrlBuilderUtil.buildPostApplicationUrl;
import static uk.gov.dwp.health.pip.application.manager.utils.UrlBuilderUtil.buildPutHealthDisabilitySubmissionUrl;
import static uk.gov.dwp.health.pip.application.manager.utils.UrlBuilderUtil.buildPutHealthDisabilityUrl;
import static uk.gov.dwp.health.pip.application.manager.utils.UrlBuilderUtil.buildPutRegistrationSubmissionUrl;
import static uk.gov.dwp.health.pip.application.manager.utils.UrlBuilderUtil.buildPutRegistrationUrl;

public class PutApplicationSubmissionIT extends ApiTest {
  CreatedApplication createdApplication;
  String healthDisabilityURL;
  String healthDisabilitySubmissionURL;

  @BeforeEach
  public void createHealthDisabilityApplication() {
    createApplicationWithHealthDisabilityStatus();

    healthDisabilityURL = buildPutHealthDisabilityUrl(createdApplication.getApplicationId());
    healthDisabilitySubmissionURL =
        buildPutHealthDisabilitySubmissionUrl(
            createdApplication.getApplicationId(), createdApplication.getApplicationId());
    messageUtils.purgeWorkflowRequestQueue();
  }

  @Test
  public void shouldPublishMessageAndReturn202StatusCode() throws JSONException {
    var updateHealthDisability = UpdateHealthDisability.builder().build();
    putRequest(healthDisabilityURL, updateHealthDisability);

    int actualStatusCode =
        putRequest(healthDisabilitySubmissionURL, updateHealthDisability).statusCode();

    await()
        .atMost(1, TimeUnit.MINUTES)
        .until(() -> messageUtils.getWorkflowRequestMessageCount().equals("1"));

    var workflowRequestMessage = messageUtils.getWorkflowRequestMessage();
    var messageBody = new JSONObject(workflowRequestMessage.getBody());
    var messageAttributes = messageBody.getJSONObject("MessageAttributes");
    var message = new JSONObject(messageBody.getString("Message"));

    assertThat(messageAttributes.getJSONObject("x-dwp-routing-key").getString("Type"))
        .isEqualTo("String");
    assertThat(messageAttributes.getJSONObject("x-dwp-routing-key").getString("Value"))
        .isEqualTo("workflow");
    assertThat(message.getString("name")).isEqualTo("Alanzzz Smithzzz");
    assertThat(messageUtils.getWorkflowRequestMessageCount()).isEqualTo("0");
    assertThat(actualStatusCode).isEqualTo(202);
  }

  @Test
  public void shouldReturn403IfApplicationStatusNotHealthAndDisability() {
    createApplicationWithRegistrationStatus();
    UpdateHealthDisability updateHealthDisabilityRequest = UpdateHealthDisability.builder().build();

    int actualStatusCode =
        putRequest(
                buildPutHealthDisabilitySubmissionUrl(
                    createdApplication.getApplicationId(), createdApplication.getApplicationId()),
                updateHealthDisabilityRequest)
            .statusCode();

    assertThat(actualStatusCode).isEqualTo(403);
  }

  @Test
  public void shouldReturn404IfApplicationDoesNotExist() {
    String applicationId = RandomStringUtil.generate(24);

    UpdateHealthDisability updateHealthDisabilityRequest = UpdateHealthDisability.builder().build();
    putRequest(healthDisabilityURL, updateHealthDisabilityRequest);

    int actualStatusCode =
        putRequest(
                buildPutHealthDisabilitySubmissionUrl(applicationId, applicationId),
                updateHealthDisabilityRequest)
            .statusCode();

    assertThat(actualStatusCode).isEqualTo(404);
  }

  @Test
  public void shouldReturn422StatusCodeWhenPersonalDetailsInfoIsNotPresent() {
    FormData.Details details =
        FormData.Details.builder().forename(null).dob(null).surname(null).build();
    FormData formData = FormData.builder().details(details).build();
    UpdateHealthDisability updateHealthDisabilityRequest =
        UpdateHealthDisability.builder().formData(formData).build();
    putRequest(healthDisabilityURL, updateHealthDisabilityRequest);

    int actualStatusCode =
        putRequest(healthDisabilitySubmissionURL, updateHealthDisabilityRequest).statusCode();

    assertThat(actualStatusCode).isEqualTo(422);
  }

  private void createApplicationWithHealthDisabilityStatus() {
    // Create Application
    String claimantId = RandomStringUtil.generate(24);
    Registration registration = Registration.builder().claimantId(claimantId).build();
    createdApplication =
        extractPostRequest(buildPostApplicationUrl(), registration, CreatedApplication.class);

    // Update and submit Registration data
    UpdateRegistration updatedApplicationBody = UpdateRegistration.builder().build();
    putRequest(
        buildPutRegistrationUrl(createdApplication.getApplicationId()), updatedApplicationBody);
    putRequest(
        buildPutRegistrationSubmissionUrl(createdApplication.getApplicationId()),
        updatedApplicationBody);
  }

  private void createApplicationWithRegistrationStatus() {
    // Create Application
    String claimantId = RandomStringUtil.generate(24);
    Registration registration = Registration.builder().claimantId(claimantId).build();
    createdApplication =
        extractPostRequest(buildPostApplicationUrl(), registration, CreatedApplication.class);
  }
}
