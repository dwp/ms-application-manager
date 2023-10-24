package uk.gov.dwp.health.pip.application.manager.api.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import uk.gov.dwp.health.pip.application.manager.openapi.healthdisability.v1.V1Api;
import uk.gov.dwp.health.pip.application.manager.openapi.healthdisability.v1.dto.ClaimantObject;
import uk.gov.dwp.health.pip.application.manager.service.HealthDataGetter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HealthApiAdapter implements V1Api {

  private final HealthDataGetter healthDataGetter;

  @Override
  public ResponseEntity<List<ClaimantObject>> getClaimantsWithState(
      final Integer pageSize,
      final Integer page,
      final String state,
      final String timestampStringFrom,
      final String timestampStringTo) {
    final LocalDateTime timestampFrom = getDateFromString(timestampStringFrom);
    final LocalDateTime timestampTo = getDateFromString(timestampStringTo);
    final List<ClaimantObject> matchingClaimants =
        healthDataGetter.getHealthDataByStateAndTimestamp(
            pageSize, page, state, timestampFrom, timestampTo);
    return ResponseEntity.status(HttpStatus.OK).body(matchingClaimants);
  }

  public static LocalDateTime getDateFromString(String timestampStringFrom) {
    return LocalDateTime.parse(timestampStringFrom, DateTimeFormatter.ISO_DATE_TIME);
  }
}
