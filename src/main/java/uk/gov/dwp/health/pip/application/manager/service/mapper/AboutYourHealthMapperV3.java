package uk.gov.dwp.health.pip.application.manager.service.mapper;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.AboutYourHealth;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.HealthProfessionalsDetails;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.HospitalHospiceOrCarehome;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v3.dto.CareAccommodationDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v3.dto.HealthProfessionalDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v3.dto.AboutYourHealthDto;

@Component
@RequiredArgsConstructor
class AboutYourHealthMapperV3 {

  private final AddressMapperV3 addressMapperV3;

  AboutYourHealthDto toDto(AboutYourHealth aboutYourHealth) {
    var hospitalHospiceOrCarehome = aboutYourHealth.getHospitalHospiceOrCarehome();
    var healthProfessionalsDetailsList = aboutYourHealth.getHealthProfessionalsDetails();

    return new AboutYourHealthDto()
            .conditions(aboutYourHealth.getHealthConditions())
            .careAccommodation(
                    new CareAccommodationDto()
                            .accommodationType(getAccommodationType(hospitalHospiceOrCarehome))
                            .admissionDate(getAdmissionDate(hospitalHospiceOrCarehome))
                            .address(addressMapperV3.getAddress(hospitalHospiceOrCarehome)))
            .hcpContactConsent(aboutYourHealth.getHcpContactConsent())
            .hcpShareConsent(aboutYourHealth.getHcpShareConsent())
            .healthProfessionals(getHealthProfessionals(healthProfessionalsDetailsList));
  }

  private List<HealthProfessionalDto> getHealthProfessionals(
          List<HealthProfessionalsDetails> healthProfessionalsDetailsList) {
    return healthProfessionalsDetailsList.stream()
            .map(this::toHealthProfessionalDto)
            .collect(Collectors.toList());
  }

  private HealthProfessionalDto toHealthProfessionalDto(
          HealthProfessionalsDetails healthProfessionalsDetails) {
    return new HealthProfessionalDto()
            .name(healthProfessionalsDetails.getName())
            .profession(healthProfessionalsDetails.getProfession())
            .phoneNumber(healthProfessionalsDetails.getPhoneNumber())
            .address(addressMapperV3.getAddress(healthProfessionalsDetails.getAddress()))
            .lastContact(healthProfessionalsDetails.getLastContact());
  }

  private CareAccommodationDto.AccommodationTypeEnum getAccommodationType(
          HospitalHospiceOrCarehome hospitalHospiceOrCarehome) {
    var accommodationTypeValue = hospitalHospiceOrCarehome.getAccommodationType().value();
    return CareAccommodationDto.AccommodationTypeEnum.fromValue(accommodationTypeValue);
  }

  private String getAdmissionDate(HospitalHospiceOrCarehome hospitalHospiceOrCarehome) {
    return (String) hospitalHospiceOrCarehome.getAdditionalProperties().get("admissionDate");
  }
}
