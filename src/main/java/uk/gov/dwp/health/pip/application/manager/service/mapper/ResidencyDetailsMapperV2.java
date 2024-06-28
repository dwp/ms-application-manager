package uk.gov.dwp.health.pip.application.manager.service.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.pip.application.manager.exception.RegistrationDataNotValid;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.RegistrationSchema140;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.ResidenceAndPresenceSchema100.ResidentBeforeBrexit;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.ResidenceAndPresenceSchema110;
import uk.gov.dwp.health.pip.pipcsapimodeller.registration.residency.ResidencyDetails;
import uk.gov.dwp.health.pip.pipcsapimodeller.registration.type.EeaNationality;
import uk.gov.dwp.health.pip.pipcsapimodeller.registration.type.Nationality;
import uk.gov.dwp.health.pip.pipcsapimodeller.registration.type.YesNoDontKnow;

@Service
@Slf4j
class ResidencyDetailsMapperV2 {
  
  ResidencyDetails mapResidencyDetails(RegistrationSchema140 registrationSchema) {
    var residenceAndPresence = registrationSchema.getResidenceAndPresence();
    var nationality = registrationSchema.getResidenceAndPresence().getNationality();
    var legacyNationalityValue = getLegacyNationalityValue(nationality);
    var isEEA = EeaNationality.isEeaNational(nationality);
    var isBritishOrIrish =
        nationality.equals(Nationality.BRITISH.toString())
        || nationality.equals(Nationality.IRISH.toString());
    var isBritishOrIrishOrEEA = isEEA || isBritishOrIrish;
    var inUkTwoOutThreeAnswer = toYesNoDontKnow(residenceAndPresence.getInUkTwoOutOfThreeYears());
    return ResidencyDetails.builder()
      .nationality(legacyNationalityValue)
      .onOrBefore(
        isEEA ? getOnOrBeforeBrexit(residenceAndPresence.getResidentBeforeBrexit()) : null)
      .inUkTwoOutOfThreeYears(inUkTwoOutThreeAnswer)
      .pensionsOrBenefitsFromEea(
        isBritishOrIrishOrEEA
          ? toYesNoDontKnow(residenceAndPresence.getReceivingPensionsOrBenefitsFromEEA())
          : null)
      .workInEea(
        isBritishOrIrishOrEEA
          ? toYesNoDontKnow(residenceAndPresence.getPayingInsuranceEEA())
          : null)
      .build();
  }
  
  private Nationality getLegacyNationalityValue(String nationality) {
    try {
      return Nationality.fromValue(nationality);
    } catch (IllegalArgumentException e) {
      log.debug(
          "Registration data not valid. Problem when mapping nationality to legacy. {}",
          e.getMessage());
      throw new RegistrationDataNotValid(
        "Registration data not valid. Problem when mapping nationality to legacy.");
    }
  }
  
  private YesNoDontKnow getOnOrBeforeBrexit(
      ResidenceAndPresenceSchema110.ResidentBeforeBrexit residentBeforeBrexit) {
    if (residentBeforeBrexit.equals(ResidenceAndPresenceSchema110.ResidentBeforeBrexit.YES)) {
      return YesNoDontKnow.YES;
    } else if (residentBeforeBrexit.equals(ResidenceAndPresenceSchema110.ResidentBeforeBrexit.NO)) {
      return YesNoDontKnow.NO;
    } else if (residentBeforeBrexit.equals(
        ResidenceAndPresenceSchema110.ResidentBeforeBrexit.DON_T_KNOW)) {
      return YesNoDontKnow.DONT_KNOW;
    }
    return null;
  }
  
  private YesNoDontKnow toYesNoDontKnow(
      ResidenceAndPresenceSchema110.InUkTwoOutOfThreeYears inUkTwoOutOfThreeYears) {
    if (inUkTwoOutOfThreeYears.equals(ResidenceAndPresenceSchema110.InUkTwoOutOfThreeYears.YES)) {
      return YesNoDontKnow.YES;
    }
    return YesNoDontKnow.NO;
  }
  
  private YesNoDontKnow toYesNoDontKnow(Boolean bool) {
    if (bool == Boolean.TRUE) {
      return YesNoDontKnow.YES;
    } else if (bool == Boolean.FALSE) {
      return YesNoDontKnow.NO;
    }
    return null;
  }
}
