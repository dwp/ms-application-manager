package uk.gov.dwp.health.pip.application.manager.api.v2;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v1.dto.ClaimantIdAndApplicationStatus;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v2.V2Api;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v2.dto.ClaimantIdAndStatusDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v2.dto.RegistrationDto;
import uk.gov.dwp.health.pip.application.manager.service.ApplicationStatusGetter;
import uk.gov.dwp.health.pip.application.manager.service.RegistrationDataGetterV2;

@Controller
@RequiredArgsConstructor
public class RegistrationApiAdapterV2 implements V2Api {

  private final RegistrationDataGetterV2 registrationDataGetterV2;
  private final ApplicationStatusGetter applicationStatusGetter;

  @Override
  public ResponseEntity<RegistrationDto> getRegistrationDataByApplicationId(String applicationId) {
    var registrationDto =
        registrationDataGetterV2.getRegistrationDataByApplicationId(applicationId);
    return ResponseEntity.status(HttpStatus.OK).body(registrationDto);
  }

  @Override
  public ResponseEntity<ClaimantIdAndStatusDto> getClaimantIdAndStatusHistory(
          String applicationId) {
    var status = applicationStatusGetter.getClaimantIdAndStatusHistory(applicationId);
    return ResponseEntity.ok().body(status);
  }
}
