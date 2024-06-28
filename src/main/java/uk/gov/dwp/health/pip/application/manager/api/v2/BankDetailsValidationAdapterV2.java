package uk.gov.dwp.health.pip.application.manager.api.v2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import uk.gov.dwp.health.pip.application.manager.entity.BankDetailsValidityList;
import uk.gov.dwp.health.pip.application.manager.openapi.bankdetails.v2.V2Api;
import uk.gov.dwp.health.pip.application.manager.openapi.bankdetails.v2.dto.AccountDetails;
import uk.gov.dwp.health.pip.application.manager.service.BankDetailsValidatorV2;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class BankDetailsValidationAdapterV2 implements V2Api {

  private final BankDetailsValidatorV2 bankDetailsValidator;

  @Override
  public ResponseEntity<List<String>> validate(final String correlationId, final String consumerId,
      final AccountDetails account) {
    final BankDetailsValidityList bankDetailsValidity = bankDetailsValidator.validate(
        account.getAccountNumber(),
        account.getSortCode(),
        account.getRollNumber(),
        consumerId,
        correlationId);
    final List<String> resultsAsStrings = bankDetailsValidity.getResultsAsStrings();
    log.info("validated bank details {}", resultsAsStrings);
    return new ResponseEntity<>(resultsAsStrings, HttpStatus.OK);
  }
}
