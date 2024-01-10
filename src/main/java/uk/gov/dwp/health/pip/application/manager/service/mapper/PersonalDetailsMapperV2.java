package uk.gov.dwp.health.pip.application.manager.service.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.Contact110;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.PersonalDetailsSchema110;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v2.dto.ContactDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v2.dto.PersonalDetailsDto;

@Component
@RequiredArgsConstructor
class PersonalDetailsMapperV2 {

  private final AddressMapperV2 addressMapperV2;
  private final AlternateFormatMapperV2 alternateFormatMapperV2;

  PersonalDetailsDto toDto(PersonalDetailsSchema110 personalDetails) {
    var address = personalDetails.getAddress();
    var alternativeAddress = personalDetails.getAlternativeAddress();
    var contact = personalDetails.getContact();
    var alternateFormat = personalDetails.getAlternateFormat();

    return new PersonalDetailsDto()
        .firstName(personalDetails.getFirstname())
        .surname(personalDetails.getSurname())
        .nationalInsuranceNumber(personalDetails.getNino())
        .dateOfBirth(personalDetails.getDob())
        .address(addressMapperV2.getAddress(address))
        .alternativeAddress(addressMapperV2.getAddress(alternativeAddress))
        .contact(getContact(contact))
        .alternateFormat(alternateFormatMapperV2.toDto(alternateFormat));
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
