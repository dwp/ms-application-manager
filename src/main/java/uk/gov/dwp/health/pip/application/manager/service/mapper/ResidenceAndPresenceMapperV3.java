package uk.gov.dwp.health.pip.application.manager.service.mapper;

import org.springframework.stereotype.Component;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.ResidenceAndPresence;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v3.dto.ResidenceAndPresenceDto;

@Component
class ResidenceAndPresenceMapperV3 {

  ResidenceAndPresenceDto toDto(ResidenceAndPresence residenceAndPresence) {
    return new ResidenceAndPresenceDto()
        .nationality(residenceAndPresence.getNationality())
        .residentBeforeBrexit(getResidentBeforeBrexit(residenceAndPresence))
        .inUkTwoOutOfThreeYears(getInUkTwoOutOfThreeYears(residenceAndPresence))
        .receivingPensionsOrBenefitsFromEEA(
            residenceAndPresence.getReceivingPensionsOrBenefitsFromEEA())
        .payingInsuranceEEA(residenceAndPresence.getPayingInsuranceEEA());
  }

  private String getResidentBeforeBrexit(ResidenceAndPresence residenceAndPresence) {
    return residenceAndPresence.getResidentBeforeBrexit() == null
        ? null
        : residenceAndPresence.getResidentBeforeBrexit().value();
  }

  private String getInUkTwoOutOfThreeYears(ResidenceAndPresence residenceAndPresence) {
    return residenceAndPresence.getInUkTwoOutOfThreeYears() == null
        ? null
        : residenceAndPresence.getInUkTwoOutOfThreeYears().value();
  }
}
