package uk.gov.dwp.health.pip.application.manager.api.v5;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v5.V5Api;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v5.dto.V5ApplicationStatus;
import uk.gov.dwp.health.pip.application.manager.service.RegistrationDataGetterV5;

@Controller
@RequiredArgsConstructor
public class RegistrationApiAdapterV5 implements V5Api {

  private final RegistrationDataGetterV5 registrationDataGetterV5;

  @Override
  public ResponseEntity<V5ApplicationStatus> getRegistrationStatusByIds(
      String applicationId, String claimantId, String nino, String submissionId) {

    V5ApplicationStatus v5ApplicationStatus = registrationDataGetterV5
        .getRegistrationDataById(applicationId, claimantId, nino, submissionId);
    return ResponseEntity.status(HttpStatus.OK).body(v5ApplicationStatus);
  }

}
