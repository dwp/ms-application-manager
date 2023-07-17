package uk.gov.dwp.health.pip.application.manager.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import uk.gov.dwp.health.pip.application.manager.config.MongoClientConnection;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.openapi.v1.dto.ApplicationId;
import uk.gov.dwp.health.pip.application.manager.requestmodels.ApplicationMatcher;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.dwp.health.pip.application.manager.utils.UrlBuilderUtil.buildGetApplicationIdByNinoUrl;

class GetApplicationIdByNinoIT extends ApiTest {

  private MongoTemplate mongoTemplate;

  @BeforeEach
  void beforeEach() {
    mongoTemplate = MongoClientConnection.getMongoTemplate();
  }

  @Test
  void shouldReturn200StatusCodeWithApplicationId() {
    var application =
        Application.builder().id("100000000000000000000200").nino("RN000004A").build();
    mongoTemplate.save(application, "application");

    String url = buildGetApplicationIdByNinoUrl();
    var applicationMatcher = ApplicationMatcher.builder().nino("RN000004A").build();

    int statusCode = postRequest(url, applicationMatcher).getStatusCode();
    ApplicationId applicationId = extractPostRequest(url, applicationMatcher, ApplicationId.class);

    assertThat(statusCode).isEqualTo(200);
    assertThat(applicationId.getApplicationId()).matches("100000000000000000000200");
  }

  @Test
  void shouldReturn200StatusCodeWithoutApplicationId() {
    var application =
        Application.builder().id("200000000000000000000200").nino("RN000007A").build();
    mongoTemplate.save(application, "application");

    String url = buildGetApplicationIdByNinoUrl();
    var applicationMatcher = ApplicationMatcher.builder().nino("RN000008C").build();

    int statusCode = postRequest(url, applicationMatcher).getStatusCode();
    ApplicationId applicationId = extractPostRequest(url, applicationMatcher, ApplicationId.class);

    assertThat(statusCode).isEqualTo(200);
    assertThat(applicationId.getApplicationId()).isNull();
  }

  @Test
  void shouldReturn400StatusCodeForBadNino() {
    String url = buildGetApplicationIdByNinoUrl();
    var applicationMatcher = ApplicationMatcher.builder().nino("bad-nino").build();

    var response = postRequest(url, applicationMatcher);

    assertThat(response.getStatusCode()).isEqualTo(400);
    assertThat(response.getBody().asString()).contains("Request validation failed on input");
  }

  @Test
  void shouldReturn400StatusCodeForNoNino() {
    String url = buildGetApplicationIdByNinoUrl();
    var applicationMatcher = ApplicationMatcher.builder().nino("").build();

    var response = postRequest(url, applicationMatcher);

    assertThat(response.getStatusCode()).isEqualTo(400);
    assertThat(response.getBody().asString()).contains("Request validation failed on input");
  }
}
