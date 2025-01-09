package uk.gov.dwp.health.pip.application.manager.api.registration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static uk.gov.dwp.health.pip.application.manager.utils.UrlBuilderUtil.buildGetApplicationStatusUrl;
import static uk.gov.dwp.health.pip.application.manager.utils.UrlBuilderUtil.buildGetRegistrationByIdUrl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import uk.gov.dwp.health.pip.application.manager.api.ApiTest;
import uk.gov.dwp.health.pip.application.manager.config.MongoClientConnection;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.entity.FormData;
import uk.gov.dwp.health.pip.application.manager.entity.History;
import uk.gov.dwp.health.pip.application.manager.entity.State;
import uk.gov.dwp.health.pip.application.manager.entity.enums.Language;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v1.dto.ApplicationCoordinatorStatusDto;
import uk.gov.dwp.health.pip.application.manager.requestmodels.registration.UpdateRegistration;
import uk.gov.dwp.health.pip.application.manager.utils.RandomStringUtil;

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
    assertThat(responseApplication.getSubmissionDate()).isEqualTo("2022-03-27");
  }

  @Test
  void shouldReturn200StatusCodeAndResponseForApplicationStatus() {
    String claimantId = RandomStringUtil.generate(24);
    var application = createApplication("66d84838b472041a0fbdbef4", claimantId);
    var url = buildGetApplicationStatusUrl(claimantId);

    int actualResponseCode = getRequest(url).statusCode();
    ApplicationCoordinatorStatusDto responseApplication =
        extractGetRequest(url, ApplicationCoordinatorStatusDto.class);
    assertThat(actualResponseCode).isEqualTo(200);
    assertThat(responseApplication.getApplicationStatus())
        .isEqualTo(ApplicationCoordinatorStatusDto.ApplicationStatusEnum.REFERRED_TO_ASSESSMENT);
  }

  private Application createApplication(String applicationId, String claimantId) {

    MongoTemplate mongoTemplate = MongoClientConnection.getMongoTemplate();

    UpdateRegistration updatedApplicationBody = UpdateRegistration.builder().build();

    Application application =
            Application.builder()
                    .id(applicationId) // create id with mocked return for HAD
                    .claimantId(claimantId)
                    .pipcsRegistrationState(State.builder().current("SUBMITTED").build())
                    .registrationData(
                            FormData.builder()
                                    .data(updatedApplicationBody.getFormData())
                                    .meta(updatedApplicationBody.getMeta())
                                    .build())
                    .dateRegistrationSubmitted(LocalDate.of(2025, Month.MARCH, 27))
                    .effectiveFrom(LocalDate.of(2025, Month.MARCH, 27))
                    .effectiveTo(LocalDate.of(2025, Month.MARCH, 27).plusDays(90))
                    .language(Language.EN)
                    .build();
    return mongoTemplate.save(application, "application");
  }
}
