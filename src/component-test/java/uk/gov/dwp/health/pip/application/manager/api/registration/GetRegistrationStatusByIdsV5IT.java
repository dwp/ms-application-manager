package uk.gov.dwp.health.pip.application.manager.api.registration;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.dwp.health.pip.application.manager.utils.UrlBuilderUtil.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import uk.gov.dwp.health.pip.application.manager.api.ApiTest;
import uk.gov.dwp.health.pip.application.manager.config.MongoClientConnection;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.entity.FormData;
import uk.gov.dwp.health.pip.application.manager.entity.History;
import uk.gov.dwp.health.pip.application.manager.entity.State;
import uk.gov.dwp.health.pip.application.manager.entity.enums.Language;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v5.dto.V5ApplicationStatus;
import uk.gov.dwp.health.pip.application.manager.requestmodels.registration.Registration;
import uk.gov.dwp.health.pip.application.manager.requestmodels.registration.UpdateRegistration;
import uk.gov.dwp.health.pip.application.manager.responsemodels.CreatedApplication;
import uk.gov.dwp.health.pip.application.manager.utils.RandomStringUtil;

class GetRegistrationStatusByIdsV5IT extends ApiTest {

  String postUrl = buildPostApplicationUrl();

  @Test
  void shouldReturn200StatusCodeWhenFormDataValidWithState() {
    Application application = createApplicationWithHealthDisabilityStatus();
    var url = buildGetRegistrationStatusByIdV5Url(application.getId());

    int actualResponseCode = getRequest(url).statusCode();
    V5ApplicationStatus response = extractGetRequest(url, V5ApplicationStatus.class);
    assertThat(actualResponseCode).isEqualTo(200);
    assertThat(response.getNino()).isNotNull().isEqualTo("RN000009C");
    assertThat(response.getApplicationId()).isNotNull().isEqualTo(application.getId());
    assertThat(response.getClaimantId()).isNotNull().isEqualTo(application.getClaimantId());
    assertThat(response.getCurrentState()).isNotNull()
        .isEqualTo(V5ApplicationStatus.CurrentStateEnum.HEALTH_AND_DISABILITY);
    assertThat(response.getHistory().size()).isEqualTo(2);
  }

  @Test
  void shouldReturn200StatusCodeWhenNoFormData() {
    String claimantId = RandomStringUtils.randomAlphabetic(24).toLowerCase();
    Registration registration = Registration.builder().claimantId(claimantId).build();
    CreatedApplication createdApplication = extractPostRequest(
        postUrl, registration, CreatedApplication.class);

    var url = buildGetRegistrationStatusByIdV5Url(createdApplication.getApplicationId());
    int actualResponseCode = getRequest(url).statusCode();
    V5ApplicationStatus response = extractGetRequest(url, V5ApplicationStatus.class);
    assertThat(actualResponseCode).isEqualTo(200);
    assertThat(response.getNino()).isNull();
    assertThat(response.getApplicationId()).isNotNull()
        .isEqualTo(createdApplication.getApplicationId());
    assertThat(response.getClaimantId()).isNotNull().isEqualTo(claimantId);
  }

  @Test
  void shouldReturn400StatusCodeBadRequest() {
    assertThat(getRequest(buildGetRegistrationStatusByIdV5Url("!@123"))
        .statusCode()).isEqualTo(400);
  }

  @Test
  void shouldReturn404StatusCodeNotFound() {
    assertThat(getRequest(buildGetRegistrationStatusByIdV5Url(
        RandomStringUtils.randomAlphanumeric(24).toLowerCase()))
        .statusCode()).isEqualTo(404);
  }

  private Application createApplicationWithHealthDisabilityStatus() {

    MongoTemplate mongoTemplate = MongoClientConnection.getMongoTemplate();

    UpdateRegistration updatedApplicationBody = UpdateRegistration.builder().build();

    Application application =
        Application.builder()
            .id("5ed0d430716609122be7a4d7") // create id with mocked return for HAD
            .claimantId(RandomStringUtil.generate(24))
            .pipcsRegistrationState(State.builder().current("SUBMITTED").build())
                .state(State.builder()
                        .current("HEALTH_AND_DISABILITY")
                        .history(List.of(
                                History.builder()
                                        .state("REGISTRATION")
                                        .timeStamp(Instant.now())
                                        .build(),
                                History.builder()
                                        .state("HEALTH_AND_DISABILITY")
                                        .timeStamp(Instant.now())
                                        .build()
                        ))
                        .build())
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
