package uk.gov.dwp.health.pip.application.manager.service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class NinoValidator {

  private NinoValidator() {}

  private static final String NINO_REGEX =
      "(^(?!BG)(?!GB)(?!NK)(?!KN)(?!TN)(?!NT)(?!ZZ)"
          + "[A-Z&&[^DFIQUV]][A-Z&&[^DFIOQUV]][0-9]{6}[A-D]$)";

  static boolean validate(String nationalInsuranceNumber) {
    String ninoWithoutSpaces = nationalInsuranceNumber.replaceAll("\\s", "").toUpperCase();
    if (!ninoWithoutSpaces.matches(NINO_REGEX)) {
      log.info("Nino Validation Failed");
      throw new IllegalArgumentException();
    }
    return true;
  }
}
