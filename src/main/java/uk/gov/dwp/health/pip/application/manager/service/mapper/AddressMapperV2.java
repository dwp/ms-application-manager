package uk.gov.dwp.health.pip.application.manager.service.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.AddressSchema100;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.HospitalHospiceOrCarehomeSchema110;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v2.dto.AddressDto;

@Component
@RequiredArgsConstructor
class AddressMapperV2 {

  private final FormCommons formCommons;

  AddressDto getAddress(AddressSchema100 addressSchema100) {
    if (addressSchema100 == null) {
      return null;
    }
    return new AddressDto()
        .line1(addressSchema100.getLine1())
        .line2(addressSchema100.getLine2())
        .line3(addressSchema100.getLine3())
        .town(addressSchema100.getTown())
        .county(addressSchema100.getCounty())
        .postcode(addressSchema100.getPostcode())
        .country(addressSchema100.getCountry().value());
  }

  AddressDto getAddress(HospitalHospiceOrCarehomeSchema110 hospitalHospiceOrCarehome) {
    var addressObject = formCommons.getCareAccommodationAddressFromForm(hospitalHospiceOrCarehome);
    var addressSchema = formCommons.marshallAddress(addressObject);

    if (addressSchema == null) {
      return null;
    }

    var accommodationName =
        hospitalHospiceOrCarehome.getAdditionalProperties().get("accommodationName") != null
            ? (String) hospitalHospiceOrCarehome.getAdditionalProperties().get("accommodationName")
            : null;

    return new AddressDto()
        .line1(accommodationName)
        .line2(addressSchema.getLine1())
        .line3(addressSchema.getLine2())
        .town(addressSchema.getTown())
        .postcode(addressSchema.getPostcode())
        .county(addressSchema.getCounty())
        .country(addressSchema.getCountry().toString());
  }
}
