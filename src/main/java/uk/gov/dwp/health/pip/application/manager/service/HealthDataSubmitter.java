package uk.gov.dwp.health.pip.application.manager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.pip.application.manager.constant.ApplicationState;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.entity.History;
import uk.gov.dwp.health.pip.application.manager.exception.ApplicationNotFoundException;
import uk.gov.dwp.health.pip.application.manager.exception.ProhibitedActionException;
import uk.gov.dwp.health.pip.application.manager.messaging.WorkflowMessagePublisher;
import uk.gov.dwp.health.pip.application.manager.openapi.healthdisability.v1.dto.FormDataDto;
import uk.gov.dwp.health.pip.application.manager.repository.ApplicationRepository;
import uk.gov.dwp.health.pip2.common.Pip2HealthDisabilityForm;

import java.time.Clock;
import java.time.ZoneId;
import java.util.Date;

@RequiredArgsConstructor
@Service
@Slf4j
public class HealthDataSubmitter {

  private final ApplicationRepository applicationRepository;
  private final HealthDataMarshaller healthDataMarshaller;
  private final WorkflowMessagePublisher workflowMessagePublisher;
  private final Clock clock;

  public void submitHealthData(String applicationId, String submissionId, FormDataDto formDataDto) {
    Application application = getApplication(applicationId);
    checkAllowSubmit(application);
    setFormData(application, formDataDto);
    setApplicationState(application);
    setAudit(application);
    setSubmissionId(application, submissionId);
    var pip2HealthDisabilityForm =
        healthDataMarshaller.marshallHealthData(application.getHealthDisabilityData().getData());
    applicationRepository.save(application);
    publishMessage(application, pip2HealthDisabilityForm);
  }

  private Application getApplication(String applicationId) {
    return applicationRepository
        .findById(applicationId)
        .orElseThrow(
            () ->
                new ApplicationNotFoundException(
                    "No application found for provided Application Id"));
  }

  private void checkAllowSubmit(Application application) {
    var currentState = application.getState().getCurrent();
    if (ApplicationState.valueOf(currentState).getValue()
            != ApplicationState.HEALTH_AND_DISABILITY.getValue()
        || application.getHealthDisabilityData() == null
        || application.getHealthDisabilityData().getData() == null) {
      throw new ProhibitedActionException("Health and disability data submission is not allowed");
    }
  }

  private void setFormData(Application application, FormDataDto formDataDto) {
    application.getHealthDisabilityData().setData(formDataDto.getFormData());
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

  private void publishMessage(
      Application application, Pip2HealthDisabilityForm pip2HealthDisabilityForm) {
    String forename;
    String surname;
    String nino;
    Date submissionDate;
    if (null == application.getForename()
        || null == application.getSurname()
        || null == application.getNino()) {
      forename = pip2HealthDisabilityForm.getDetails().getForename();
      surname = pip2HealthDisabilityForm.getDetails().getSurname();
      nino = pip2HealthDisabilityForm.getDetails().getNino();
      submissionDate = pip2HealthDisabilityForm.getSubmissionDate();
    } else {
      forename = application.getForename();
      surname = application.getSurname();
      nino = application.getNino();
      submissionDate =
          Date.from(
              application
                  .getDateRegistrationSubmitted()
                  .atStartOfDay()
                  .atZone(ZoneId.systemDefault())
                  .toInstant());
    }

    workflowMessagePublisher.publishMessage(
        application.getId(), forename + " " + surname, nino, submissionDate);
  }
}
