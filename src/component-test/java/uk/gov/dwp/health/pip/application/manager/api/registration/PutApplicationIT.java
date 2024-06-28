package uk.gov.dwp.health.pip.application.manager.api.registration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static uk.gov.dwp.health.pip.application.manager.utils.UrlBuilderUtil.buildPostApplicationUrl;
import static uk.gov.dwp.health.pip.application.manager.utils.UrlBuilderUtil.buildPutRegistrationUrl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import uk.gov.dwp.health.pip.application.manager.api.ApiTest;
import uk.gov.dwp.health.pip.application.manager.config.MongoClientConnection;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.constant.ApplicationState;
import uk.gov.dwp.health.pip.application.manager.entity.State;
import uk.gov.dwp.health.pip.application.manager.requestmodels.registration.Registration;
import uk.gov.dwp.health.pip.application.manager.requestmodels.registration.UpdateRegistration;
import uk.gov.dwp.health.pip.application.manager.responsemodels.CreatedApplication;
import uk.gov.dwp.health.pip.application.manager.responsemodels.Error;
import uk.gov.dwp.health.pip.application.manager.utils.RandomStringUtil;

public class PutApplicationIT extends ApiTest {
  String url;

  @BeforeEach
  public void createApplication() {
    String claimantId = RandomStringUtil.generate(24);
    Registration applicationBody = Registration.builder().claimantId(claimantId).build();
    CreatedApplication createdApplication =
        extractPostRequest(buildPostApplicationUrl(), applicationBody, CreatedApplication.class);
    url = buildPutRegistrationUrl(createdApplication.getApplicationId());
  }

  @Test
  public void shouldReturn200StatusCode() {
    UpdateRegistration updatedApplicationBody = UpdateRegistration.builder().build();

    int actualStatusCode = putRequest(url, updatedApplicationBody).statusCode();

    assertThat(actualStatusCode).isEqualTo(200);
  }

  @Test
  public void shouldReturn400StatusCodeAndCorrectResponseBodyWhenRequestBodyIsInvalid() {
    int actualStatusCode = putRequest(url, "{\"test\":\"test\"}").statusCode();
    Error error = extractPutRequest(url, "{\"test\":\"test\"}", Error.class);

    assertThat(actualStatusCode).isEqualTo(400);
    assertThat(error.getMessage()).isEqualTo("Request validation failed on input");
  }

  @Test
  public void shouldReturn403StatusCodeWhenApplicationStatusNotRegistered() {
    MongoTemplate mongoTemplate = MongoClientConnection.getMongoTemplate();

    Application application1 =
        uk.gov.dwp.health.pip.application.manager.entity.Application.builder()
            .id("5ed0d430716609122be7a4d7")
            .claimantId("300000000000000000000409")
            .state(State.builder().current(ApplicationState.SUBMITTED.name()).build())
            .build();
    mongoTemplate.save(application1, "application");
    UpdateRegistration updatedApplicationBody = UpdateRegistration.builder().build();

    int actualResponseCode =
        putRequest(buildPutRegistrationUrl(application1.getId()), updatedApplicationBody)
            .statusCode();

    assertThat(actualResponseCode).isEqualTo(403);
  }

  @Test
  public void shouldReturn404StatusCodeWhenApplicationDoesNotExist() {
    UpdateRegistration updatedApplicationBody = UpdateRegistration.builder().build();
    String url = buildPutRegistrationUrl("728baf70ef65f020674a7e1e");

    int actualStatusCode = putRequest(url, updatedApplicationBody).statusCode();

    assertThat(actualStatusCode).isEqualTo(404);
  }
}
