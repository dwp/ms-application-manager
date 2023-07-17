package uk.gov.dwp.health.pip.application.manager.service.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.pip.application.manager.exception.RegistrationDataNotValid;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.AddressSchema100;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.HospitalHospiceOrCarehome100;
import uk.gov.dwp.health.pip.pipcsapimodeller.registration.carehome.HospitalAndOtherAccomDetails;
import uk.gov.dwp.health.pip.pipcsapimodeller.registration.common.Address;
import uk.gov.dwp.health.pip.pipcsapimodeller.registration.type.AccommodationType;

import java.time.LocalDate;

import static uk.gov.dwp.health.pip.application.manager.service.mapper.FormToLegacyMap.getAccommodationTypeLegacyValue;

@RequiredArgsConstructor
@Service
@Slf4j
class HospitalAndOtherAccomDetailsMapper {

  private final FormCommons formCommons;
  private final PostcodeMapper postcodeMapper;

  HospitalAndOtherAccomDetails getHospitalAndOtherAccomDetails(
      HospitalHospiceOrCarehome100 hospitalHospiceOrCarehome) {

    if (hospitalHospiceOrCarehome != null) {
      HospitalHospiceOrCarehome100.AccommodationType accommodationType =
          hospitalHospiceOrCarehome.getAccommodationType();
      checkAccommodationTypeExists(accommodationType);
      if (HospitalHospiceOrCarehome100.AccommodationType.NONE == accommodationType) {
        return null;
      }

      String accommodationTypeLegacyValue =
          getAccommodationTypeLegacyValue(accommodationType.toString());
      var addressObject =
          formCommons.getCareAccommodationAddressFromForm(hospitalHospiceOrCarehome);
      AddressSchema100 addressSchema100 = formCommons.marshallAddress(addressObject);
      Address address = getAddress(addressSchema100, hospitalHospiceOrCarehome);

      return HospitalAndOtherAccomDetails.builder()
          .accommodationType(AccommodationType.fromValue(accommodationTypeLegacyValue))
          .hospitalAndOtherAccomAddressDetails(address)
          .admissionDate(getAdmissionDate(hospitalHospiceOrCarehome))
          .build();
    }

    return null;
  }

  private void checkAccommodationTypeExists(
      HospitalHospiceOrCarehome100.AccommodationType accommodationType) {
    if (accommodationType == null) {
      log.debug(
          "Registration data not valid. "
              + "hospitalHospiceOrCarehome exists but accommodation type is null");
      throw new RegistrationDataNotValid(
          "Registration data not valid. Accommodation type not provided.");
    }
  }

  private Address getAddress(
      AddressSchema100 addressSchema, HospitalHospiceOrCarehome100 hospitalHospiceOrCarehome) {
    if (addressSchema == null) {
      return null;
    }

    var accommodationName =
        hospitalHospiceOrCarehome.getAdditionalProperties().get("accommodationName") != null
            ? (String) hospitalHospiceOrCarehome.getAdditionalProperties().get("accommodationName")
            : null;
    var postcode = postcodeMapper.mapPostcode(addressSchema.getPostcode());

    return Address.builder()
        .line1(accommodationName)
        .line2(addressSchema.getLine1())
        .line3(addressSchema.getLine2())
        .townOrCity(addressSchema.getTown())
        .postcode(postcode)
        .county(addressSchema.getCounty())
        .country(addressSchema.getCountry().toString())
        .build();
  }

  private LocalDate getAdmissionDate(HospitalHospiceOrCarehome100 hospitalHospiceOrCarehome) {
    String admissionDate =
        (String) hospitalHospiceOrCarehome.getAdditionalProperties().get("admissionDate");

    return admissionDate != null ? LocalDate.parse(admissionDate) : null;
  }
}
