package uk.gov.dwp.health.pip.application.manager.api.registration;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.dwp.health.pip.application.manager.utils.UrlBuilderUtil.buildGetRegistrationUrl;
import static uk.gov.dwp.health.pip.application.manager.utils.UrlBuilderUtil.buildPostApplicationUrl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import uk.gov.dwp.health.pip.application.manager.api.ApiTest;
import uk.gov.dwp.health.pip.application.manager.config.MongoClientConnection;
import uk.gov.dwp.health.pip.application.manager.constant.ApplicationState;
import uk.gov.dwp.health.pip.application.manager.entity.State;
import uk.gov.dwp.health.pip.application.manager.requestmodels.registration.Registration;
import uk.gov.dwp.health.pip.application.manager.responsemodels.Application;
import uk.gov.dwp.health.pip.application.manager.utils.RandomStringUtil;

public class GetApplicationIT extends ApiTest {
  String url;

  @BeforeEach
  public void createApplicationData() {
    Registration registration =
        Registration.builder().claimantId(RandomStringUtil.generate(24)).build();
    postRequest(buildPostApplicationUrl(), registration);
    url = buildGetRegistrationUrl(registration.getClaimantId());
  }

  @Test
  public void shouldReturn200StatusCodeAndCorrectResponseBody() {
    int actualResponseCode = getRequest(url).statusCode();
    Application applicationRegistration = extractGetRequest(url, Application.class);

    assertThat(actualResponseCode).isEqualTo(200);
    assertThat(applicationRegistration.getApplicationId()).matches("^[a-zA-Z0-9]{24}$");
  }

  @Test
  public void shouldReturn400StatusCodeWhenTheClaimantIdIsInvalid() {
    int actualResponseCode = getRequest(buildGetRegistrationUrl("%^&*()$")).statusCode();

    assertThat(actualResponseCode).isEqualTo(400);
  }

  @Test
  public void shouldReturn404StatusCodeWhenTheClaimantIdIsNotFound() {
    int actualResponseCode =
        getRequest(buildGetRegistrationUrl(RandomStringUtil.generate(24))).statusCode();

    assertThat(actualResponseCode).isEqualTo(404);
  }

  @Test
  public void shouldReturn409StatusCodeWhenTwoApplicationsFoundForSameClaimantId() {
    MongoTemplate mongoTemplate = MongoClientConnection.getMongoTemplate();
    var application1 =
        uk.gov.dwp.health.pip.application.manager.entity.Application.builder()
            .id("5ed0d430716609122be7a4d1")
            .claimantId("300000000000000000000409")
            .build();
    var application2 =
        uk.gov.dwp.health.pip.application.manager.entity.Application.builder()
            .id("5ed0d430716609122be7a4d2")
            .claimantId("300000000000000000000409")
            .build();
    mongoTemplate.save(application1, "application");
    mongoTemplate.save(application2, "application");

    int actualResponseCode =
        getRequest(buildGetRegistrationUrl("300000000000000000000409")).statusCode();

    assertThat(actualResponseCode).isEqualTo(409);
  }
}
