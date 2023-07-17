package uk.gov.dwp.health.pip.application.manager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.exception.ApplicationNotFoundException;
import uk.gov.dwp.health.pip.application.manager.exception.ProhibitedActionException;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v1.dto.FormDataDto;
import uk.gov.dwp.health.pip.application.manager.repository.ApplicationRepository;

import java.time.Clock;

@Service
@RequiredArgsConstructor
public class RegistrationDataUpdater {

  private final ApplicationRepository repository;
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
    if (!"REGISTRATION".equals(application.getState().getCurrent())) {
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
