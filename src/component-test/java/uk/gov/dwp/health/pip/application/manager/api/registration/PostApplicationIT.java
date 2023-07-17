package uk.gov.dwp.health.pip.application.manager.api.registration;

import org.junit.jupiter.api.Test;
import uk.gov.dwp.health.pip.application.manager.api.ApiTest;
import uk.gov.dwp.health.pip.application.manager.requestmodels.registration.Registration;
import uk.gov.dwp.health.pip.application.manager.responsemodels.CreatedApplication;
import uk.gov.dwp.health.pip.application.manager.responsemodels.Error;
import uk.gov.dwp.health.pip.application.manager.utils.RandomStringUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.dwp.health.pip.application.manager.utils.UrlBuilderUtil.buildPostApplicationUrl;

public class PostApplicationIT extends ApiTest {
  String url = buildPostApplicationUrl();

  @Test
  public void shouldReturn201StatusCode() {
    Registration registration =
        Registration.builder().claimantId(RandomStringUtil.generate(24)).build();

    int actualStatusCode = postRequest(url, registration).statusCode();

    assertThat(actualStatusCode).isEqualTo(201);
  }

  @Test
  public void shouldReturnCorrectResponseBodyOnSuccessfulRequest() {
    Registration registration =
            Registration.builder().claimantId(RandomStringUtil.generate(24)).build();

    CreatedApplication createdApplication =
            extractPostRequest(url, registration, CreatedApplication.class);

    assertThat(createdApplication.getApplicationId()).matches("^[a-zA-Z0-9]{24}$");
    assertThat(createdApplication.getApplicationStatus()).isEqualTo("REGISTRATION");
  }

  @Test
  public void shouldReturn400StatusCodeAndCorrectResponseBodyWhenAnInvalidPayloadIsSent() {
    int actualStatusCode = postRequest(url, "}").statusCode();
    Error error = extractPostRequest(url, "}", Error.class);

    assertThat(actualStatusCode).isEqualTo(400);
    assertThat(error.getMessage()).isEqualTo("Request validation failed on input");
  }

  @Test
  public void shouldReturn400StatusCodeAndCorrectResponseBodyWhenThereIsAnInvalidClaimantId() {
    Registration registration = Registration.builder().claimantId("5ed0d4").build();

    int actualStatusCode = postRequest(url, registration).statusCode();
    Error error = extractPostRequest(url, registration, Error.class);

    assertThat(actualStatusCode).isEqualTo(400);
    assertThat(error.getMessage()).isEqualTo("Request validation failed on input");
  }

  @Test
  public void shouldReturn400StatusCodeAndCorrectResponseBodyWhenThereIsAnInvalidBenefitType() {
    String claimantId = RandomStringUtil.generate(24);
    Registration registration =
        Registration.builder().claimantId(claimantId).benefitType("ESA").build();

    int actualStatusCode = postRequest(url, registration).statusCode();
    Error error = extractPostRequest(url, registration, Error.class);

    assertThat(actualStatusCode).isEqualTo(400);
    assertThat(error.getMessage()).isEqualTo("Request validation failed on input");
  }

  @Test
  public void shouldReturn400StatusCodeAndCorrectResponseBodyWhenThereIsAnInvalidLanguage() {
    Registration registration =
        Registration.builder().claimantId(RandomStringUtil.generate(24)).language("DE").build();

    int actualStatusCode = postRequest(url, registration).statusCode();
    Error error = extractPostRequest(url, registration, Error.class);

    assertThat(actualStatusCode).isEqualTo(400);
    assertThat(error.getMessage()).isEqualTo("Request validation failed on input");
  }

  @Test
  public void shouldReturn409StatusCodeWhenThereIsAConflictingClaimantId() {
    Registration registration =
            Registration.builder().claimantId(RandomStringUtil.generate(24)).build();
    postRequest(url, registration);

    int actualStatusCode = postRequest(url, registration).statusCode();

    assertThat(actualStatusCode).isEqualTo(409);
  }
}
