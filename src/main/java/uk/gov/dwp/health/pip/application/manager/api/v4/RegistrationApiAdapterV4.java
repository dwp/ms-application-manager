package uk.gov.dwp.health.pip.application.manager.api.v4;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v4.dto.RegistrationDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v4.V4Api;
import uk.gov.dwp.health.pip.application.manager.service.RegistrationDataGetterV4;

@Controller
@RequiredArgsConstructor
public class RegistrationApiAdapterV4 implements V4Api {
  
  private final RegistrationDataGetterV4 registrationDataGetterV4;
  
  @Override
  public ResponseEntity<RegistrationDto> getRegistrationDataByApplicationId(String applicationId) {
    var registrationDto =
        registrationDataGetterV4.getRegistrationDataByApplicationId(applicationId);
    return ResponseEntity.status(HttpStatus.OK).body(registrationDto);
  }
}
