package uk.gov.dwp.health.pip.application.manager.service;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.exception.ApplicationNotFoundException;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v5.dto.HistoryDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v5.dto.V5ApplicationStatus;
import uk.gov.dwp.health.pip.application.manager.repository.ApplicationRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
@Slf4j
public class RegistrationDataGetterV5 {

  private final ApplicationRepository repository;

  public V5ApplicationStatus getRegistrationDataById(
      String applicationId, String claimantId, String nino, String submissionId) {

    checkParams(applicationId, claimantId, nino, submissionId);
    Application application;
    if (StringUtils.hasLength(applicationId)) {
      application = repository.findById(applicationId)
          .orElseThrow(() ->
              new ApplicationNotFoundException("No registration data found for provided ID")
          );
    } else {
      List<Application> applications = StringUtils.hasLength(claimantId)
          ? repository.findAllByClaimantId(claimantId)
          : StringUtils.hasLength(nino)
          ? repository.getAllByNino(nino)
          : repository.getAllBySubmissionId(submissionId);

      if (applications.isEmpty()) {
        throw new ApplicationNotFoundException("No registration data found for provided ID");
      }
      if (applications.size() > 1) {
        throw new IllegalStateException("ERROR: multiple registration data found for ID");
      }
      application = applications.get(0);
    }
    return toDto(application);
  }

  private V5ApplicationStatus toDto(Application application) {
    return new V5ApplicationStatus()
        .applicationId(application.getId())
        .claimantId(application.getClaimantId())
        .nino(application.getNino())
        .submissionId(application.getSubmissionId())
        .currentState(application.getState() != null && application.getState().getCurrent() != null
            ? V5ApplicationStatus.CurrentStateEnum.valueOf(application.getState().getCurrent())
            : null)
        .history(application.getState() != null && application.getState().getHistory() != null
            ? application.getState().getHistory().stream().map(history ->
                new HistoryDto()
                    .state(HistoryDto.StateEnum.fromValue(history.getState()))
                    .timestamp(history.getTimeStamp().toString()))
            .toList()
            : null);
  }

  private void checkParams(String applicationId,
                           String claimantId,
                           String nino,
                           String submissionId) {

    if (Stream.of(StringUtils.hasLength(applicationId),
            StringUtils.hasLength(claimantId),
            StringUtils.hasLength(nino),
            StringUtils.hasLength(submissionId))
        .filter(b -> b)
        .count() != 1) {
      throw new ConstraintViolationException("One and only one of application_id, "
          + "claimant_id, nino or submission_id required", Collections.EMPTY_SET);
    }
  }
}
