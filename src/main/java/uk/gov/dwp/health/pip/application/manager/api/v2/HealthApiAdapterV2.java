package uk.gov.dwp.health.pip.application.manager.api.v2;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import uk.gov.dwp.health.pip.application.manager.openapi.healthdisability.v2.V2Api;
import uk.gov.dwp.health.pip.application.manager.openapi.healthdisability.v2.dto.HealthDisabilityDto;
import uk.gov.dwp.health.pip.application.manager.service.HealthDataGetterV2;
import uk.gov.dwp.health.pip.application.manager.service.HealthDataSubmitterV2;

@Controller
@RequiredArgsConstructor
public class HealthApiAdapterV2 implements V2Api {

  private final HealthDataGetterV2 healthDataGetterV2;
  private final HealthDataSubmitterV2 healthDataSubmitterV2;

  @Override
  public ResponseEntity<HealthDisabilityDto> getHealthDisabilityDataByApplicationId(
      String applicationId) {
    var healthDisabilityDto = healthDataGetterV2.getHealthDataByApplicationId(applicationId);
    return ResponseEntity.status(HttpStatus.OK).body(healthDisabilityDto);
  }

  @Override
  public ResponseEntity<Void> submitHealthDisabilityData(
      String applicationId, String submissionId) {
    healthDataSubmitterV2.submitHealthData(applicationId, submissionId);
    return ResponseEntity.status(HttpStatus.ACCEPTED).build();
  }
}
