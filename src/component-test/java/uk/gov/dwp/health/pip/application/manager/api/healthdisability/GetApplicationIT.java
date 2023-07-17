package uk.gov.dwp.health.pip.application.manager.api.healthdisability;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import uk.gov.dwp.health.pip.application.manager.api.ApiTest;
import uk.gov.dwp.health.pip.application.manager.config.MongoClientConnection;
import uk.gov.dwp.health.pip.application.manager.constant.ApplicationState;
import uk.gov.dwp.health.pip.application.manager.entity.State;
import uk.gov.dwp.health.pip.application.manager.requestmodels.registration.Registration;
import uk.gov.dwp.health.pip.application.manager.requestmodels.registration.UpdateRegistration;
import uk.gov.dwp.health.pip.application.manager.responsemodels.Application;
import uk.gov.dwp.health.pip.application.manager.responsemodels.CreatedApplication;
import uk.gov.dwp.health.pip.application.manager.utils.RandomStringUtil;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static uk.gov.dwp.health.pip.application.manager.utils.UrlBuilderUtil.buildGetHealthDisabilityUrl;
import static uk.gov.dwp.health.pip.application.manager.utils.UrlBuilderUtil.buildPostApplicationUrl;
import static uk.gov.dwp.health.pip.application.manager.utils.UrlBuilderUtil.buildPutRegistrationSubmissionUrl;
import static uk.gov.dwp.health.pip.application.manager.utils.UrlBuilderUtil.buildPutRegistrationUrl;

public class GetApplicationIT extends ApiTest {

  CreatedApplication createdApplication;
  Registration applicationRequest;
  String url;

  @BeforeEach
  public void createHealthDisabilityApplication() {
    final MongoTemplate mongoTemplate = MongoClientConnection.getMongoTemplate();
    mongoTemplate.dropCollection(Application.class);
    createApplicationWithHealthDisabilityStatus();
    url = buildGetHealthDisabilityUrl(applicationRequest.getClaimantId());
  }

  @Test
  public void shouldReturn200StatusCodeAndCorrectResponseBody() {
    int actualResponseCode = getRequest(url).statusCode();
    Application application =
            extractGetRequest(url, Application.class);

    assertThat(actualResponseCode).isEqualTo(200);
    assertThat(application.getApplicationId()).matches("^[a-zA-Z0-9]{24}$");
    assertThat(application.getApplicationStatus()).isEqualTo("HEALTH_AND_DISABILITY");
  }

  @Test
  public void shouldReturn400StatusCodeWhenTheClaimantIdIsInvalid() {
    int actualResponseCode = getRequest(buildGetHealthDisabilityUrl("%^&*()$")).statusCode();

    assertThat(actualResponseCode).isEqualTo(400);
  }

  @Test
  public void shouldReturn404StatusCodeWhenTheClaimantIdIsNotFound() {
    int actualResponseCode = getRequest(buildGetHealthDisabilityUrl(RandomStringUtil.generate(24))).statusCode();

    assertThat(actualResponseCode).isEqualTo(404);
  }

  @Test
  public void shouldReturn409StatusCodeWhenTwoApplicationsFoundForSameClaimantId() {
    MongoTemplate mongoTemplate = MongoClientConnection.getMongoTemplate();
    var application1 =
            uk.gov.dwp.health.pip.application.manager.entity.Application.builder()
                    .claimantId("300000000000000000000409")
                    .state(State.builder().current(ApplicationState.HEALTH_AND_DISABILITY.name()).build())
                    .build();
    var application2 =
            uk.gov.dwp.health.pip.application.manager.entity.Application.builder()
                    .claimantId("300000000000000000000409")
                    .state(State.builder().current(ApplicationState.HEALTH_AND_DISABILITY.name()).build())
                    .build();
    mongoTemplate.save(application1, "application");
    mongoTemplate.save(application2, "application");

    int actualResponseCode = getRequest(buildGetHealthDisabilityUrl("300000000000000000000409")).statusCode();

    assertThat(actualResponseCode).isEqualTo(409);
  }

  private void createApplicationWithHealthDisabilityStatus() {
    applicationRequest = Registration.builder().claimantId(RandomStringUtil.generate(24)).build();
    createdApplication = extractPostRequest(buildPostApplicationUrl(), applicationRequest, CreatedApplication.class);

    UpdateRegistration updatedApplicationBody = UpdateRegistration.builder().build();
    putRequest(buildPutRegistrationUrl(createdApplication.getApplicationId()), updatedApplicationBody);
    putRequest(buildPutRegistrationSubmissionUrl(createdApplication.getApplicationId()), updatedApplicationBody);
  }
}
