package uk.gov.dwp.health.pip.application.manager.api.v1;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.dwp.health.pip.application.manager.entity.BankDetailsValidityList;
import uk.gov.dwp.health.pip.application.manager.entity.enums.BankDetailsValidity;
import uk.gov.dwp.health.pip.application.manager.openapi.bankdetails.v1.dto.AccountDetails;
import uk.gov.dwp.health.pip.application.manager.openapi.bankdetails.v1.dto.BankDetailsDto;
import uk.gov.dwp.health.pip.application.manager.service.BankDetailsGetter;
import uk.gov.dwp.health.pip.application.manager.service.BankDetailsValidator;

import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class BankDetailsValidationAdapterV1Test {

  public static final String ACCOUNT_NUMBER = "1";
  public static final String SORT_CODE = "2";
  public static final String ROLL_NUMBER = "3";
  public static final String INVALID_ACCOUNT_NUMBER = "5";

  @InjectMocks private BankDetailsValidationAdapterV1 bankDetailsValidationAdapterV2;
  @Mock private BankDetailsValidator bankDetailsValidator;
  @Mock private BankDetailsGetter bankDetailsGetter;

  @Test
  void validDetails() {
    final BankDetailsValidityList result = new BankDetailsValidityList();
    result.addResult(BankDetailsValidity.VALID);
    when(bankDetailsValidator.validate(ACCOUNT_NUMBER, SORT_CODE, ROLL_NUMBER)).thenReturn(result);

    final AccountDetails account = new AccountDetails();
    account.setAccountNumber(ACCOUNT_NUMBER);
    account.setSortCode(SORT_CODE);
    account.setRollNumber(ROLL_NUMBER);
    ResponseEntity<List<String>> responseEntity = bankDetailsValidationAdapterV2.validate(account);

    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseEntity.getBody()).isNotNull();
    assertThat(responseEntity.getBody().getClass()).isEqualTo(LinkedList.class);
    assertThat(responseEntity.getBody().size()).isEqualTo(1);
    assertThat(responseEntity.getBody().get(0)).isEqualTo("VALID");
  }

  @Test
  void invalidDetails() {
    final BankDetailsValidityList invalidAccountResult = new BankDetailsValidityList();
    invalidAccountResult.addResult(BankDetailsValidity.INVALID_ACCOUNT_COMBINATION);
    invalidAccountResult.addResult(BankDetailsValidity.INVALID_ROLL_NUMBER);
    when(bankDetailsValidator.validate(INVALID_ACCOUNT_NUMBER, SORT_CODE, ROLL_NUMBER))
        .thenReturn(invalidAccountResult);

    final AccountDetails invalidAccount = new AccountDetails();
    invalidAccount.setAccountNumber(INVALID_ACCOUNT_NUMBER);
    invalidAccount.setSortCode(SORT_CODE);
    invalidAccount.setRollNumber(ROLL_NUMBER);
    ResponseEntity<List<String>> invalidResponseEntity =
        bankDetailsValidationAdapterV2.validate(invalidAccount);

    assertThat(invalidResponseEntity.getBody().size()).isEqualTo(2);
    assertThat(invalidResponseEntity.getBody().get(0)).isEqualTo("INVALID_ACCOUNT_COMBINATION");
    assertThat(invalidResponseEntity.getBody().get(1)).isEqualTo("INVALID_ROLL_NUMBER");
  }

  @Test
  void when_bank_details_exist_returns_200() {
    BankDetailsDto bankDetailsDto = new BankDetailsDto();
    when(bankDetailsGetter.getBankDetailsByApplicationId("application-id-1"))
            .thenReturn(bankDetailsDto);

    ResponseEntity<BankDetailsDto> response = bankDetailsValidationAdapterV2.getBankDetailsByApplicationId("application-id-1");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo(bankDetailsDto);
  }
}
