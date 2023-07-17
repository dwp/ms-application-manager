package uk.gov.dwp.health.pip.application.manager.api.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import uk.gov.dwp.health.pip.application.manager.openapi.healthdisability.v1.V1Api;
import uk.gov.dwp.health.pip.application.manager.openapi.healthdisability.v1.dto.ClaimantObject;
import uk.gov.dwp.health.pip.application.manager.openapi.healthdisability.v1.dto.FormDataDto;
import uk.gov.dwp.health.pip.application.manager.openapi.healthdisability.v1.dto.HealthDisabilityDto;
import uk.gov.dwp.health.pip.application.manager.service.HealthDataGetter;
import uk.gov.dwp.health.pip.application.manager.service.HealthDataSubmitter;
import uk.gov.dwp.health.pip.application.manager.service.HealthDataUpdater;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HealthApiAdapter implements V1Api {

  private final HealthDataGetter healthDataGetter;
  private final HealthDataUpdater healthDataUpdater;
  private final HealthDataSubmitter healthDataSubmitter;

  @Override
  public ResponseEntity<HealthDisabilityDto> getHealthDisabilityData(String claimantId) {
    var healthDisabilityDto = healthDataGetter.getHealthData(claimantId);
    return ResponseEntity.status(HttpStatus.OK).body(healthDisabilityDto);
  }

  @Override
  public ResponseEntity<HealthDisabilityDto> getHealthDisabilityDataByApplicationId(
      String applicationId) {
    var healthDisabilityDto = healthDataGetter.getHealthDataByApplicationId(applicationId);
    return ResponseEntity.status(HttpStatus.OK).body(healthDisabilityDto);
  }

  @Override
  public ResponseEntity<Void> updateHealthDisabilityData(
      String applicationId, FormDataDto formDataDto) {
    healthDataUpdater.updateHealthData(applicationId, formDataDto);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @Override
  public ResponseEntity<Void> submitHealthDisabilityData(
      String applicationId, String submissionId, FormDataDto formDataDto) {
    healthDataSubmitter.submitHealthData(applicationId, submissionId, formDataDto);
    return ResponseEntity.status(HttpStatus.ACCEPTED).build();
  }

  @Override
  public ResponseEntity<List<ClaimantObject>> getClaimantsWithState(
      final Integer pageSize, final Integer page, final String state,
      final String timestampStringFrom, final String timestampStringTo) {
    final LocalDateTime timestampFrom = getDateFromString(timestampStringFrom);
    final LocalDateTime timestampTo = getDateFromString(timestampStringTo);
    final List<ClaimantObject> matchingClaimants = healthDataGetter
        .getHealthDataByStateAndTimestamp(pageSize, page, state, timestampFrom, timestampTo);
    return ResponseEntity.status(HttpStatus.OK).body(matchingClaimants);
  }

  public static LocalDateTime getDateFromString(String timestampStringFrom) {
    return LocalDateTime.parse(timestampStringFrom, DateTimeFormatter.ISO_DATE_TIME);
  }

}
