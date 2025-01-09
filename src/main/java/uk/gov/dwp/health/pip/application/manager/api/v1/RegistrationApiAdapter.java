package uk.gov.dwp.health.pip.application.manager.api.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v1.V1Api;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v1.dto.ApplicationCoordinatorStatusDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v1.dto.FormDataDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v1.dto.RegistrationDto;
import uk.gov.dwp.health.pip.application.manager.service.ApplicationStatusGetter;
import uk.gov.dwp.health.pip.application.manager.service.RegistrationDataGetter;
import uk.gov.dwp.health.pip.application.manager.service.RegistrationDataUpdater;
import uk.gov.dwp.health.pip.application.manager.service.RegistrationSubmitter;

@Controller
@RequiredArgsConstructor
public class RegistrationApiAdapter implements V1Api {

  private final RegistrationDataGetter registrationDataGetter;
  private final RegistrationDataUpdater registrationDataUpdater;
  private final RegistrationSubmitter registrationSubmitter;
  private final ApplicationStatusGetter applicationStatusGetter;

  @Override
  public ResponseEntity<RegistrationDto> getRegistrationData(String claimantId) {
    RegistrationDto registrationDto =
            registrationDataGetter.getRegistrationDataByClaimantId(claimantId);
    return ResponseEntity.status(HttpStatus.OK).body(registrationDto);
  }

  @Override
  public ResponseEntity<RegistrationDto> getRegistrationDataByApplicationId(String applicationId) {
    RegistrationDto registrationDto =
            registrationDataGetter.getRegistrationDataByApplicationId(applicationId);
    return ResponseEntity.status(HttpStatus.OK).body(registrationDto);
  }

  @Override
  public ResponseEntity<Void> updateRegistrationData(
          String applicationId, FormDataDto formDataDto) {
    registrationDataUpdater.updateRegistrationDataByApplicationId(applicationId, formDataDto);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @Override
  public ResponseEntity<Void> registrationSubmission(String applicationId) {
    registrationSubmitter.submitRegistrationData(applicationId);
    return ResponseEntity.status(HttpStatus.ACCEPTED).build();
  }

  @Override
  public ResponseEntity<ApplicationCoordinatorStatusDto> getApplicationStatus(String claimantId) {
    ApplicationCoordinatorStatusDto status =
        applicationStatusGetter.getApplicationStatusByClaimantId(claimantId);
    return ResponseEntity.ok().body(status);
  }

}
