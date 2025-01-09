package uk.gov.dwp.health.pip.application.manager.api.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import uk.gov.dwp.health.pip.application.manager.openapi.v1.V1Api;
import uk.gov.dwp.health.pip.application.manager.openapi.v1.dto.ApplicationCreateDto;
import uk.gov.dwp.health.pip.application.manager.openapi.v1.dto.ApplicationDto;
import uk.gov.dwp.health.pip.application.manager.openapi.v1.dto.ApplicationId;
import uk.gov.dwp.health.pip.application.manager.openapi.v1.dto.NinoDto;
import uk.gov.dwp.health.pip.application.manager.service.ApplicationCreator;
import uk.gov.dwp.health.pip.application.manager.service.ApplicationIdsByNinoGetter;

@Controller
@RequiredArgsConstructor
public class ApplicationApiAdapter implements V1Api {

  private final ApplicationCreator applicationCreator;
  private final ApplicationIdsByNinoGetter applicationIdsByNinoGetter;

  @Override
  public ResponseEntity<ApplicationDto> createApplication(
      ApplicationCreateDto applicationCreateDto) {
    ApplicationDto applicationDto = applicationCreator.createApplication(applicationCreateDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(applicationDto);
  }

  @Override
  public ResponseEntity<ApplicationId> getApplicationIdByNino(NinoDto ninoDto) {
    ApplicationId applicationId =
        applicationIdsByNinoGetter.getApplicationIdByNino(ninoDto.getNino());
    return ResponseEntity.status(HttpStatus.OK).body(applicationId);
  }
}
