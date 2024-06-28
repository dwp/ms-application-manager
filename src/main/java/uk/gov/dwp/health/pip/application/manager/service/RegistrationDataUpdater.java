package uk.gov.dwp.health.pip.application.manager.service;

import java.time.Clock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.entity.State;
import uk.gov.dwp.health.pip.application.manager.exception.ApplicationNotFoundException;
import uk.gov.dwp.health.pip.application.manager.exception.ProhibitedActionException;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v1.dto.FormDataDto;
import uk.gov.dwp.health.pip.application.manager.repository.ApplicationRepository;

@Service
@RequiredArgsConstructor
public class RegistrationDataUpdater {

  private final ApplicationRepository repository;
  private final ApplicationCoordinatorService applicationCoordinatorService;
  private final Clock clock;

  public void updateRegistrationDataByApplicationId(String applicationId, FormDataDto dataDto) {
    repository
        .findById(applicationId)
        .ifPresentOrElse(
            application -> {
              isAllowedForUpdate(application);
              updateRegistrationData(application, dataDto);
              updateAudit(application);
              repository.save(application);
            },
            () -> {
              throw new ApplicationNotFoundException(
                  "No application found against provided Application ID");
            });
  }

  private void isAllowedForUpdate(Application application) {

    State currentApplicationState;

    try {
      currentApplicationState =
          applicationCoordinatorService.getApplicationState(application.getId());
    } catch (ApplicationNotFoundException exc) {
      currentApplicationState = application.getState();
    }
    if (!"REGISTRATION".equals(currentApplicationState.getCurrent())) {
      throw new ProhibitedActionException(
          "Current application status not allow application to be updated");
    }
  }

  private void updateRegistrationData(Application application, FormDataDto formDataDto) {
    application.getRegistrationData().setData(formDataDto.getFormData());
    application.getRegistrationData().setMeta(formDataDto.getMeta());
  }

  private void updateAudit(Application application) {
    application.getAudit().setLastModified(clock.instant());
  }
}
