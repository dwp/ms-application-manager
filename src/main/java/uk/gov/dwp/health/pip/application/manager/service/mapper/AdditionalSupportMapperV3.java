package uk.gov.dwp.health.pip.application.manager.service.mapper;

import org.springframework.stereotype.Component;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.AdditionalSupport;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v3.dto.AdditionalSupportDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v3.dto.HelperDto;

@Component
class AdditionalSupportMapperV3 {

  AdditionalSupportDto toDto(AdditionalSupport additionalSupport) {
    return new AdditionalSupportDto()
        .helpCommunicating(additionalSupport.getHelpCommunicating())
        .helpUnderstandingLetters(additionalSupport.getHelpUnderstandingLetters())
        .helper(getHelper(additionalSupport));
  }

  private HelperDto getHelper(AdditionalSupport additionalSupport) {
    if (additionalSupport.getHelperDetails() == null) {
      return null;
    }
    return new HelperDto()
        .firstName(additionalSupport.getHelperDetails().getFirstname())
        .surname(additionalSupport.getHelperDetails().getSurname());
  }
}
