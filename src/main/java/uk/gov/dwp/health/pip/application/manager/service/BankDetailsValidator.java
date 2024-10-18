package uk.gov.dwp.health.pip.application.manager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.MDC;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.pip.application.manager.config.properties.BankDetailsValidationProperties;
import uk.gov.dwp.health.pip.application.manager.entity.BankDetailsValidityList;
import uk.gov.dwp.health.pip.application.manager.entity.enums.BankDetailsValidity;
import uk.gov.dwp.health.pip.application.manager.external.bankdetails.ApiException;
import uk.gov.dwp.health.pip.application.manager.external.bankdetails.v3.DefaultApi;
import uk.gov.dwp.health.pip.application.manager.external.bankdetails.v3.dto.AccountDetails;
import uk.gov.dwp.health.pip.application.manager.external.bankdetails.v3.dto.AdditionalInformationDto;
import uk.gov.dwp.health.pip.application.manager.external.bankdetails.v3.dto.AdditionalInformationDto.SeverityEnum;
import uk.gov.dwp.health.pip.application.manager.external.bankdetails.v3.dto.ValidationResultDto;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static uk.gov.dwp.health.pip.application.manager.entity.enums.BankDetailsValidity.IGNORE;
import static uk.gov.dwp.health.pip.application.manager.entity.enums.BankDetailsValidity.INVALID_ACCOUNT_COMBINATION;
import static uk.gov.dwp.health.pip.application.manager.entity.enums.BankDetailsValidity.INVALID_ROLL_NUMBER;
import static uk.gov.dwp.health.pip.application.manager.entity.enums.BankDetailsValidity.INVALID_SORT_CODE_ACCOUNT_NUMBER_DATA_FORMAT;
import static uk.gov.dwp.health.pip.application.manager.entity.enums.BankDetailsValidity.ROLL_NUMBER_FORMAT;
import static uk.gov.dwp.health.pip.application.manager.entity.enums.BankDetailsValidity.ROLL_NUMBER_REQUIRED;
import static uk.gov.dwp.health.pip.application.manager.entity.enums.BankDetailsValidity.SERVICE_DOWN;
import static uk.gov.dwp.health.pip.application.manager.entity.enums.BankDetailsValidity.VALID;
import static uk.gov.dwp.health.pip.application.manager.external.bankdetails.v3.dto.AdditionalInformationDto.SeverityEnum.ERROR;
import static uk.gov.dwp.health.pip.application.manager.external.bankdetails.v3.dto.AdditionalInformationDto.SeverityEnum.WARNING;

@RequiredArgsConstructor
@Service
@Slf4j
public class BankDetailsValidator {

  private final BankDetailsValidationProperties properties;
  private final DefaultApi api;
  private Map<AdditionalInformationDto.SeverityEnum, Map<Integer, BankDetailsValidity>>
      severityToCodesMap = new EnumMap<>(SeverityEnum.class);

  public BankDetailsValidityList validate(
      final String accountNumber,
      final String sortCode,
      final String rollNumber
  ) {
    final AccountDetails accountDetails = new AccountDetails();
    accountDetails.setAccountNumber(accountNumber);
    accountDetails.setSortCode(sortCode);
    accountDetails.setRollNumber(rollNumber);
    final BankDetailsValidityList result = new BankDetailsValidityList();
    try {
      final String consumerId = properties.getConsumerId();
      final UUID bankWizardCorrelationId = UUID.randomUUID();
      log.info("setting correlation id to {}", bankWizardCorrelationId);

      final ValidationResultDto validationResultDto = api.validate1(
          bankWizardCorrelationId.toString(), consumerId, accountDetails
      );
      if (validationResultDto == null || validationResultDto.isValidDetails() == null) {
        log.warn("Unexpected response from bank wizard {}", validationResultDto);
        result.addResult(SERVICE_DOWN);
      } else {
        final List<AdditionalInformationDto> dtos =
            validationResultDto.getAdditionalInformation();
        for (final AdditionalInformationDto additionalInformationDto : dtos) {
          final SeverityEnum severity = additionalInformationDto.getSeverity();
          final String code = additionalInformationDto.getCode();
          addResult(result, severity, code);
        }
        if (result.getResults().length == 0) {
          result.addResult(VALID);
        }
      }
    } catch (final ApiException e) {
      final int responseStatusCode = e.getCode();
      // see https://dwpdigital.atlassian.net/wiki/spaces/BV/pages/20783530587/Response+Types
      final String responseBody = e.getResponseBody();
      if (responseStatusCode == 400) {
        log.info(responseBody);
        result.addResult(INVALID_SORT_CODE_ACCOUNT_NUMBER_DATA_FORMAT);
      } else {
        log.error(responseBody, e);
        result.addResult(SERVICE_DOWN);
      }
    }
    return result;
  }

  private void addResult(final BankDetailsValidityList result, final SeverityEnum severity,
                         final String code) {
    final BankDetailsValidity newResult = getResult(severity, code);
    if (newResult == null) {
      log.warn("Unexpected response from wizard code {}, severity {}", code, severity);
    } else {
      if (!newResult.equals(IGNORE)) {
        result.addResult(newResult);
      }
    }
  }

  private BankDetailsValidity getResult(final SeverityEnum severity, final String code) {
    final Map<Integer, BankDetailsValidity> codeMap = getSeverityToCodesMap().get(severity);
    return codeMap.get(Integer.parseInt(code));
  }

  public Map<SeverityEnum, Map<Integer, BankDetailsValidity>> getSeverityToCodesMap() {
    if (severityToCodesMap.isEmpty()) {
      initialiseSeverityToCodesMap();
    }
    return severityToCodesMap;
  }

  private void initialiseSeverityToCodesMap() {
    final Map<Integer, BankDetailsValidity> warningsMap = getCodesMap(WARNING);
    final Map<Integer, BankDetailsValidity> errorsMap = getCodesMap(ERROR);
    addCodesToMap(
        warningsMap,
        new int[]{1, 2, 4, 5, 6, 11, 26, 67, 78, 95, 100, 101, 102, 103, 104, 105},
        IGNORE
    );
    addCodesToMap(
        warningsMap,
        new int[]{3, 7, 8, 28, 64, 81, 82, 106},
        INVALID_ACCOUNT_COMBINATION
    );
    addCodesToMap(errorsMap, new int[]{6, 7, 11, 12, 13, 14}, INVALID_ACCOUNT_COMBINATION);
    addCodesToMap(warningsMap, new int[]{90, 91, 92, 93, 94}, INVALID_ROLL_NUMBER);
    addCodesToMap(errorsMap, new int[]{90, 91, 92}, ROLL_NUMBER_FORMAT);
    addCodesToMap(warningsMap, new int[]{65}, ROLL_NUMBER_REQUIRED);
    addCodesToMap(errorsMap, new int[]{1, 4}, INVALID_SORT_CODE_ACCOUNT_NUMBER_DATA_FORMAT);
  }

  private void addCodesToMap(
      final Map<Integer, BankDetailsValidity> codesMap,
      final int[] codes,
      final BankDetailsValidity bankDetailsValidity
  ) {
    for (final int code : codes) {
      codesMap.put(code, bankDetailsValidity);
    }
  }

  private Map<Integer, BankDetailsValidity> getCodesMap(
      final AdditionalInformationDto.SeverityEnum severity
  ) {
    final Map<Integer, BankDetailsValidity> codesMap = new HashMap<>();
    severityToCodesMap.put(severity, codesMap);
    return codesMap;
  }
}
