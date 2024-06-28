package uk.gov.dwp.health.pip.application.manager.service.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.pip.application.manager.exception.RegistrationDataNotValid;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.AddressSchema100;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.HospitalHospiceOrCarehomeSchema110;
import uk.gov.dwp.health.pip.pipcsapimodeller.registration.carehome.HospitalAndOtherAccomDetails;
import uk.gov.dwp.health.pip.pipcsapimodeller.registration.common.Address;
import uk.gov.dwp.health.pip.pipcsapimodeller.registration.type.AccommodationType;
import uk.gov.dwp.health.pip.pipcsapimodeller.registration.type.OrganisationType;
import uk.gov.dwp.health.pip.pipcsapimodeller.registration.type.YesNoDontKnow;

import java.time.LocalDate;

import static uk.gov.dwp.health.pip.application.manager.service.mapper.FormToLegacyMap.getAccommodationTypeLegacyValue;
import static uk.gov.dwp.health.pip.application.manager.service.mapper.FormToLegacyMap.getCostsPaidOrganisationLegacyValue;

@RequiredArgsConstructor
@Service
@Slf4j
class HospitalAndOtherAccomDetailsMapper {
  
  private final FormCommonsV2 formCommons;
  private final PostcodeMapper postcodeMapper;
  
  HospitalAndOtherAccomDetails getHospitalAndOtherAccomDetails(
      HospitalHospiceOrCarehomeSchema110 hospitalHospiceOrCarehome) {
    
    if (hospitalHospiceOrCarehome != null) {
      HospitalHospiceOrCarehomeSchema110.AccommodationType accommodationType =
          hospitalHospiceOrCarehome.getAccommodationType();
      checkAccommodationTypeExists(accommodationType);
      if (HospitalHospiceOrCarehomeSchema110.AccommodationType.NONE == accommodationType) {
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
        .hospitalAndOtherAccomCostsPaid((getCostsPaid(hospitalHospiceOrCarehome)))
        .hospitalAndOtherAccomAgreeToRepay(getAgreeToRepay(hospitalHospiceOrCarehome))
        .hospitalAndOtherAccomPayingOrgName(getPayingOrgName(hospitalHospiceOrCarehome))
        .hospitalAndOtherAccomPrivatePatientPaying(
          getPrivatePatientPaying(hospitalHospiceOrCarehome))
        .build();
    }
    return null;
  }
  
  private String getPayingOrgName(HospitalHospiceOrCarehomeSchema110 hospitalHospiceOrCarehome) {
    var payingOrgName = hospitalHospiceOrCarehome.getAdditionalProperties().get("payingOrgName");
    return payingOrgName != null ? (String) payingOrgName : null;
  }
  
  private YesNoDontKnow getAgreeToRepay(
      HospitalHospiceOrCarehomeSchema110 hospitalHospiceOrCarehome) {
    var agreeToRepay = hospitalHospiceOrCarehome.getAdditionalProperties().get("agreeToRepay");
    if (agreeToRepay != null) {
      if (agreeToRepay.equals("Yes")) {
        return YesNoDontKnow.YES;
      } else if (agreeToRepay.equals("No")) {
        return YesNoDontKnow.NO;
      } else if (agreeToRepay.toString().equals("Don't know")) {
        return YesNoDontKnow.DONT_KNOW;
      }
    }
    return null;
  }
  
  private YesNoDontKnow getPrivatePatientPaying(
      HospitalHospiceOrCarehomeSchema110 hospitalHospiceOrCarehome) {
    var privatePatientPaying =
        hospitalHospiceOrCarehome.getAdditionalProperties().get("privatePatientPaying");
    if (privatePatientPaying != null) {
      if (privatePatientPaying.equals("Yes")) {
        return YesNoDontKnow.YES;
      } else if (privatePatientPaying.equals("No")) {
        return YesNoDontKnow.NO;
      } else if (privatePatientPaying.equals("Don't know")) {
        return YesNoDontKnow.DONT_KNOW;
      }
    }
    return null;
  }
  
  private OrganisationType getCostsPaid(
      HospitalHospiceOrCarehomeSchema110 hospitalHospiceOrCarehome110) {
    var costsPaidValue = hospitalHospiceOrCarehome110.getAdditionalProperties().get("costsPaid");
    return costsPaidValue != null
      ? OrganisationType.fromValue(getCostsPaidOrganisationLegacyValue(costsPaidValue.toString()))
      : null;
  }
  
  private void checkAccommodationTypeExists(
      HospitalHospiceOrCarehomeSchema110.AccommodationType accommodationType) {
    if (accommodationType == null) {
      log.debug(
            "Registration data not valid. "
            + "hospitalHospiceOrCarehome exists but accommodation type is null");
      throw new RegistrationDataNotValid(
        "Registration data not valid. Accommodation type not provided.");
    }
  }
  
  private Address getAddress(
      AddressSchema100 addressSchema,
      HospitalHospiceOrCarehomeSchema110 hospitalHospiceOrCarehome) {
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
  
  private LocalDate getAdmissionDate(HospitalHospiceOrCarehomeSchema110 hospitalHospiceOrCarehome) {
    String admissionDate =
        (String) hospitalHospiceOrCarehome.getAdditionalProperties().get("admissionDate");
    return admissionDate != null ? LocalDate.parse(admissionDate) : null;
  }
}
