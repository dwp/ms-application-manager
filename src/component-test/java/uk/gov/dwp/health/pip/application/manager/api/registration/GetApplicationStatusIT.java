package uk.gov.dwp.health.pip.application.manager.api.registration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.health.pip.application.manager.api.ApiTest;
import uk.gov.dwp.health.pip.application.manager.requestmodels.registration.Registration;
import uk.gov.dwp.health.pip.application.manager.responsemodels.ApplicationStatus;
import uk.gov.dwp.health.pip.application.manager.utils.RandomStringUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.dwp.health.pip.application.manager.utils.UrlBuilderUtil.buildGetApplicationStatusUrl;
import static uk.gov.dwp.health.pip.application.manager.utils.UrlBuilderUtil.buildGetRegistrationUrl;
import static uk.gov.dwp.health.pip.application.manager.utils.UrlBuilderUtil.buildPostApplicationUrl;

public class GetApplicationStatusIT extends ApiTest {
  String url;

  @BeforeEach
  public void createApplicationData() {
    Registration registration = Registration.builder().claimantId(RandomStringUtil.generate(24)).build();
    postRequest(buildPostApplicationUrl(), registration);
    url = buildGetApplicationStatusUrl(registration.getClaimantId());
  }

  @Test
  public void shouldReturn200StatusCode() {
    int actualStatusCode = getRequest(url).statusCode();
    ApplicationStatus applicationStatus =
            extractGetRequest(url, ApplicationStatus.class);

    assertThat(actualStatusCode).isEqualTo(200);
    assertThat(applicationStatus.getApplicationId()).matches("^[a-zA-Z0-9]{24}$");
    assertThat(applicationStatus.getApplicationStatus()).isEqualTo("REGISTRATION");
  }
}
