package uk.gov.dwp.health.pip.application.manager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.pip.application.manager.constant.ApplicationState;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.entity.History;
import uk.gov.dwp.health.pip.application.manager.exception.ApplicationNotFoundException;
import uk.gov.dwp.health.pip.application.manager.repository.ApplicationRepository;

import java.time.Clock;

@RequiredArgsConstructor
@Service
@Slf4j
public class HealthDataSubmitterV2 {

  private final ApplicationRepository applicationRepository;
  private final Clock clock;

  public void submitHealthData(String applicationId, String submissionId) {
    Application application = getApplication(applicationId);
    setApplicationState(application);
    setAudit(application);
    setSubmissionId(application, submissionId);
    applicationRepository.save(application);
  }

  private Application getApplication(String applicationId) {
    return applicationRepository
        .findById(applicationId)
        .orElseThrow(
            () ->
                new ApplicationNotFoundException(
                    "No application found for provided Application Id"));
  }

  private void setApplicationState(Application application) {
    var registrationCompleted =
        History.builder()
            .state(ApplicationState.SUBMITTED.name())
            .timeStamp(clock.instant())
            .build();
    application.getState().addHistory(registrationCompleted);
  }

  private void setAudit(Application application) {
    application.getAudit().setLastModified(clock.instant());
  }

  private void setSubmissionId(Application application, String submissionId) {
    application.setSubmissionId(submissionId);
  }
}
