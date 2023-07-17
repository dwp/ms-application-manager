package uk.gov.dwp.health.pip.application.manager.api.v3;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v3.dto.RegistrationDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v3.V3Api;
import uk.gov.dwp.health.pip.application.manager.service.RegistrationDataGetterV3;

@Controller
@RequiredArgsConstructor
public class RegistrationApiAdapterV3 implements V3Api {

  private final RegistrationDataGetterV3 registrationDataGetterV3;

  @Override
  public ResponseEntity<RegistrationDto> getRegistrationDataByApplicationId(String applicationId) {
    var registrationDto =
        registrationDataGetterV3.getRegistrationDataByApplicationId(applicationId);
    return ResponseEntity.status(HttpStatus.OK).body(registrationDto);
  }
}
