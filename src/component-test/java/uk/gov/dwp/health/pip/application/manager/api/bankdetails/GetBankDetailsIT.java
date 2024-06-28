package uk.gov.dwp.health.pip.application.manager.api.registration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.health.pip.application.manager.api.ApiTest;
import uk.gov.dwp.health.pip.application.manager.requestmodels.registration.Registration;
import uk.gov.dwp.health.pip.application.manager.responsemodels.BankDetailsDTO;
import uk.gov.dwp.health.pip.application.manager.utils.RandomStringUtil;
import uk.gov.dwp.health.pip.application.manager.responsemodels.CreatedApplication;
import uk.gov.dwp.health.pip.application.manager.requestmodels.registration.UpdateRegistration;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.dwp.health.pip.application.manager.utils.UrlBuilderUtil.buildGetBankDetailsUrl;
import static uk.gov.dwp.health.pip.application.manager.utils.UrlBuilderUtil.buildPostApplicationUrl;
import static uk.gov.dwp.health.pip.application.manager.utils.UrlBuilderUtil.buildPutRegistrationSubmissionUrl;
import static uk.gov.dwp.health.pip.application.manager.utils.UrlBuilderUtil.buildPutRegistrationUrl;

public class GetBankDetailsIT extends ApiTest {

  String url;

  @BeforeEach
  public void createApplicationData() {
    Registration registration =
        Registration.builder().claimantId(RandomStringUtil.generate(24)).build();
    CreatedApplication createdApplication =
        extractPostRequest(buildPostApplicationUrl(), registration, CreatedApplication.class);
    createApplicationWithBankDetails(createdApplication.getApplicationId());
    url = buildGetBankDetailsUrl(createdApplication.getApplicationId());
  }

  @Test
  public void shouldReturn200StatusCodeAndCorrectResponseBody() {
    int actualResponseCode = getRequest(url).statusCode();
    BankDetailsDTO bankDetailsDTO = extractGetRequest(url, BankDetailsDTO.class);
    assertThat(actualResponseCode).isEqualTo(200);
    assertThat(bankDetailsDTO.getName()).matches("^.+$");
    assertThat(bankDetailsDTO.getBankDetails().getSortCode()).matches("^[0-9]{6}$");
    assertThat(bankDetailsDTO.getBankDetails().getAccountNumber()).matches("^[0-9]{8}$");
  }

  @Test
  public void shouldReturn400StatusCodeWhenTheBankDetailsAreInvalid() {
    int actualResponseCode = getRequest(buildGetBankDetailsUrl("%^&*()$")).statusCode();

    assertThat(actualResponseCode).isEqualTo(400);
  }

  @Test
  public void shouldReturn404StatusCodeWhenTheBankDetailsAreNotFound() {
    int actualResponseCode =
        getRequest(buildGetBankDetailsUrl(RandomStringUtil.generate(24))).statusCode();

    assertThat(actualResponseCode).isEqualTo(404);
  }

  private void createApplicationWithBankDetails(String applicationId) {
    UpdateRegistration updatedApplicationBody = UpdateRegistration.builder().build();
    putRequest(
        buildPutRegistrationUrl(applicationId), updatedApplicationBody);
    putRequest(
        buildPutRegistrationSubmissionUrl(applicationId),
        updatedApplicationBody);
  }
}