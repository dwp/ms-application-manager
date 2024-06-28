package uk.gov.dwp.health.pip.application.manager.service.mapper;

import org.springframework.stereotype.Component;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.AdditionalSupportSchema100;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v4.dto.AdditionalSupportDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v4.dto.HelperDto;

@Component
class AdditionalSupportMapperV4 {
  
  AdditionalSupportDto toDto(AdditionalSupportSchema100 additionalSupport) {
    return new AdditionalSupportDto()
      .helpCommunicating(additionalSupport.getHelpCommunicating())
      .helpUnderstandingLetters(additionalSupport.getHelpUnderstandingLetters())
      .helper(getHelper(additionalSupport));
  }
  
  private HelperDto getHelper(AdditionalSupportSchema100 additionalSupport) {
    if (additionalSupport.getHelperDetails() == null) {
      return null;
    }
    return new HelperDto()
      .firstName(additionalSupport.getHelperDetails().getFirstname())
      .surname(additionalSupport.getHelperDetails().getSurname());
  }
}
