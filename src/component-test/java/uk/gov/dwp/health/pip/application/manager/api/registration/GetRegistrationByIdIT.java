package uk.gov.dwp.health.pip.application.manager.api.registration;

import org.junit.jupiter.api.Test;
import uk.gov.dwp.health.pip.application.manager.api.ApiTest;
import uk.gov.dwp.health.pip.application.manager.config.MongoClientConnection;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.entity.FormData;
import uk.gov.dwp.health.pip.application.manager.entity.State;

import java.time.LocalDate;
import java.time.Month;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static uk.gov.dwp.health.pip.application.manager.utils.UrlBuilderUtil.buildGetRegistrationByIdUrl;

class GetRegistrationByIdIT extends ApiTest {

  @Test
  void shouldReturn200StatusCodeAndCorrectResponseBody() {
    var mongoTemplate = MongoClientConnection.getMongoTemplate();

    var application =
        Application.builder()
            .state(State.builder().current("SUBMITTED").build())
            .registrationData(FormData.builder().data("form data").meta("meta").build())
            .dateRegistrationSubmitted(LocalDate.of(2022, Month.MARCH, 27))
            .build();
    var savedApplication = mongoTemplate.save(application, "application");

    var url = buildGetRegistrationByIdUrl(savedApplication.getId());
    int actualResponseCode = getRequest(url).statusCode();
    uk.gov.dwp.health.pip.application.manager.responsemodels.Application responseApplication =
        extractGetRequest(
            url, uk.gov.dwp.health.pip.application.manager.responsemodels.Application.class);

    assertThat(actualResponseCode).isEqualTo(200);
    assertThat(responseApplication.getApplicationId()).isEqualTo(savedApplication.getId());
    assertThat(responseApplication.getFormData()).isEqualTo("form data");
    assertThat(responseApplication.getMeta()).isEqualTo("meta");
    assertThat(responseApplication.getApplicationStatus()).isEqualTo("SUBMITTED");
    assertThat(responseApplication.getSubmissionDate()).isEqualTo("2022-03-27");
  }
}
