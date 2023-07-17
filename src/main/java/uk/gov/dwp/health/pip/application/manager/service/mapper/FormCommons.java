package uk.gov.dwp.health.pip.application.manager.service.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.dwp.health.pip.application.manager.exception.RegistrationDataNotValid;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.AddressSchema100;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.HospitalHospiceOrCarehome;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.HospitalHospiceOrCarehome100;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
class FormCommons {

  private final ObjectMapper objectMapper;

  Object getCareAccommodationAddressFromForm(HospitalHospiceOrCarehome hospitalHospiceOrCarehome) {
    return hospitalHospiceOrCarehome.getAdditionalProperties().get("address");
  }

  Object getCareAccommodationAddressFromForm(
      HospitalHospiceOrCarehome100 hospitalHospiceOrCarehome) {
    return hospitalHospiceOrCarehome.getAdditionalProperties().get("address");
  }

  AddressSchema100 marshallAddress(Object address) {
    byte[] bytesAddress;
    AddressSchema100 addressSchema100;

    try {
      bytesAddress = objectMapper.writeValueAsBytes(address);
      addressSchema100 = objectMapper.readValue(bytesAddress, AddressSchema100.class);
    } catch (IOException e) {
      log.debug(
          "Registration data not valid. Problem when marshalling address. {}", e.getMessage());
      throw new RegistrationDataNotValid(
          "Registration data not valid. Problem when marshalling address.");
    }

    return addressSchema100;
  }
}
