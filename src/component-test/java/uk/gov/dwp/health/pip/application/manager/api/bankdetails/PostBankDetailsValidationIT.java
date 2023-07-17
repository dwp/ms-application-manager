package uk.gov.dwp.health.pip.application.manager.api.bankdetails;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.health.pip.application.manager.api.ApiTest;
import uk.gov.dwp.health.pip.application.manager.requestmodels.bankdetails.BankDetails;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static uk.gov.dwp.health.pip.application.manager.utils.UrlBuilderUtil.buildPostBankDetailsUrl;

public class PostBankDetailsValidationIT extends ApiTest {
  private final String url = buildPostBankDetailsUrl();

  @Test
  public void shouldReturn200StatusCodeAndCorrectResponseBody() {
    final String accountNumber = "12341234";

    final Response response = callBankDetailsValidationEndpoint(accountNumber);
    int actualResponseCode = response.statusCode();

    assertThat(actualResponseCode).isEqualTo(200);
    assertThat(response.asString()).isEqualTo("[\"VALID\"]");
  }

  @Test
  public void shouldReturnInvalidAccountCombinationError() {
    // account numbers mapped to invalid account combination error stubs
    List<String> accountNumbers =
        Arrays.asList(
            "20000002",
            "20000003",
            "20000006",
            "20000007",
            "20000008",
            "20000011",
            "20000012",
            "20000013",
            "20000014",
            "22000007",
            "20000028",
            "20000064",
            "20000081",
            "20000081",
            "20000106");

    for (String accountNumber : accountNumbers) {
      final Response response = callBankDetailsValidationEndpoint(accountNumber);
      int actualResponseCode = response.statusCode();

      assertThat(actualResponseCode).isEqualTo(200);
      assertThat(response.asString()).isEqualTo("[\"INVALID_ACCOUNT_COMBINATION\"]");
    }
  }

  @Test
  public void shouldReturnInvalidRollNumberFormatError() {
    // account numbers mapped to invalid roll number error stubs
    List<String> accountNumbers =
        Arrays.asList("21000090", "22000091", "22000092", "20000093", "20000094");

    for (String accountNumber : accountNumbers) {
      final Response response = callBankDetailsValidationEndpoint(accountNumber);
      int actualResponseCode = response.statusCode();

      assertThat(actualResponseCode).isEqualTo(200);
      assertThat(response.asString()).isEqualTo("[\"INVALID_ROLL_NUMBER\"]");
    }
  }

  @Test
  public void shouldReturnRollNumberFormatError() {
    // account numbers mapped to roll number format error stubs
    List<String> accountNumbers = Arrays.asList("20000090", "20000091", "20000092");

    for (String accountNumber : accountNumbers) {
      final Response response = callBankDetailsValidationEndpoint(accountNumber);
      int actualResponseCode = response.statusCode();

      assertThat(actualResponseCode).isEqualTo(200);
      assertThat(response.asString()).isEqualTo("[\"ROLL_NUMBER_FORMAT\"]");
    }
  }

  @Test
  public void shouldReturnRollNumberRequiredError() {
    // account numbers mapped to roll number required stubs
    String accountNumber = "22000065";

    final Response response = callBankDetailsValidationEndpoint(accountNumber);
    int actualResponseCode = response.statusCode();

    assertThat(actualResponseCode).isEqualTo(200);
    assertThat(response.asString()).isEqualTo("[\"ROLL_NUMBER_REQUIRED\"]");
  }

  @Test
  public void shouldReturnInvalidAccountNumberError() {
    // account numbers mapped to account number error stubs
    List<String> accountNumbers = Arrays.asList("20000001", "20000004", "40000000", "40000001");

    for (String accountNumber : accountNumbers) {
      final Response response = callBankDetailsValidationEndpoint(accountNumber);
      int actualResponseCode = response.statusCode();

      assertThat(actualResponseCode).isEqualTo(200);
      assertThat(response.asString())
          .isEqualTo("[\"INVALID_SORT_CODE_ACCOUNT_NUMBER_DATA_FORMAT\"]");
    }
  }

  @Test
  public void shouldReturnServerDownError() {
    // account numbers mapped to server error stubs
    List<String> accountNumbers = Arrays.asList("50000000", "50000003");

    for (String accountNumber : accountNumbers) {
      final Response response = callBankDetailsValidationEndpoint(accountNumber);
      int actualResponseCode = response.statusCode();

      assertThat(actualResponseCode).isEqualTo(200);
      assertThat(response.asString()).isEqualTo("[\"SERVICE_DOWN\"]");
    }
  }

  @Test
  public void shouldReturn200ForWarningsThatCanBeIgnored() {
    // account numbers mapped to warnings that can be ignored stubs
    List<String> accountNumbers =
        Arrays.asList(
            "22000001",
            "22000004",
            "22000005",
            "22000006",
            "22000011",
            "22000026",
            "22000067",
            "22000078",
            "22000095",
            "22000100",
            "22000101",
            "22000102",
            "22000103",
            "22000104",
            "22000105");

    for (String accountNumber : accountNumbers) {
      final Response response = callBankDetailsValidationEndpoint(accountNumber);
      int actualResponseCode = response.statusCode();

      assertThat(actualResponseCode).isEqualTo(200);
      assertThat(response.asString()).isEqualTo("[\"VALID\"]");
    }
  }

  @Test
  // TODO these should be distinct errors not multiple occurrences of the same one
  public void shouldReturnMultipleErrors() {
    // account numbers mapped to multiple errors stub
    String accountNumber = "23000000";

    final Response response = callBankDetailsValidationEndpoint(accountNumber);
    int actualResponseCode = response.statusCode();

    assertThat(actualResponseCode).isEqualTo(200);
    assertThat(response.asString()).isEqualTo("[\"ROLL_NUMBER_FORMAT\"]");
  }

  private Response callBankDetailsValidationEndpoint(final String accountNumber) {
    BankDetails bankDetails = BankDetails.builder().accountNumber(accountNumber).build();
    return postRequest(url, bankDetails);
  }
}
