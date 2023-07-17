package uk.gov.dwp.health.pip.application.manager.api.v1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import uk.gov.dwp.health.pip.application.manager.entity.BankDetailsValidityList;
import uk.gov.dwp.health.pip.application.manager.openapi.bankdetails.v1.V1Api;
import uk.gov.dwp.health.pip.application.manager.openapi.bankdetails.v1.dto.AccountDetails;
import uk.gov.dwp.health.pip.application.manager.service.BankDetailsValidator;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class BankDetailsValidationAdapterV1 implements V1Api {

  private final BankDetailsValidator bankDetailsValidator;

  @Override
  public ResponseEntity<List<String>> validate(final AccountDetails account) {
    final BankDetailsValidityList bankDetailsValidity = bankDetailsValidator.validate(
        account.getAccountNumber(),
        account.getSortCode(),
        account.getRollNumber()
    );
    final List<String> resultsAsStrings = bankDetailsValidity.getResultsAsStrings();
    log.info("validated bank details {}", resultsAsStrings);
    return new ResponseEntity<>(resultsAsStrings, HttpStatus.OK);
  }
}
