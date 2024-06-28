package uk.gov.dwp.health.pip.application.manager.service.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.Contact110;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.PersonalDetailsSchema120;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v4.dto.ContactDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v4.dto.PersonalDetailsDto;

@Component
@RequiredArgsConstructor
class PersonalDetailsMapperV4 {
  
  private final AddressMapperV4 addressMapperV4;
  private final AlternateFormatMapperV4 alternateFormatMapperV4;
  
  PersonalDetailsDto toDto(PersonalDetailsSchema120 personalDetails) {
    var address = personalDetails.getAddress();
    var alternativeAddress = personalDetails.getAlternativeAddress();
    var contact = personalDetails.getContact();
    var alternateFormat = personalDetails.getAlternateFormat();
    
    return new PersonalDetailsDto()
      .firstName(personalDetails.getFirstname())
      .surname(personalDetails.getSurname())
      .nationalInsuranceNumber(personalDetails.getNino())
      .dateOfBirth(personalDetails.getDob())
      .address(addressMapperV4.getAddress(address))
      .alternativeAddress(addressMapperV4.getAddress(alternativeAddress))
      .contact(getContact(contact))
      .alternateFormat(alternateFormatMapperV4.toDto(alternateFormat));
  }
  
  private ContactDto getContact(Contact110 contact) {
    return new ContactDto()
      .mobileNumber(contact.getMobileNumber())
      .alternativeNumber(contact.getAlternativeNumber())
      .smsUpdates(toYesNo(contact.getSmsUpdates()))
      .textPhone(contact.getTextphone());
  }
  
  private String toYesNo(Boolean bool) {
    if (bool == Boolean.TRUE) {
      return "Yes";
    } else if (bool == Boolean.FALSE) {
      return "No";
    }
    return null;
  }
}
