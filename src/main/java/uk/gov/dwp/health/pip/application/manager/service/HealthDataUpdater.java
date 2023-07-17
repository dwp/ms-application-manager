package uk.gov.dwp.health.pip.application.manager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.pip.application.manager.constant.ApplicationState;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.entity.FormData;
import uk.gov.dwp.health.pip.application.manager.exception.ApplicationNotFoundException;
import uk.gov.dwp.health.pip.application.manager.exception.ProhibitedActionException;
import uk.gov.dwp.health.pip.application.manager.openapi.healthdisability.v1.dto.FormDataDto;
import uk.gov.dwp.health.pip.application.manager.repository.ApplicationRepository;

import java.time.Clock;

@RequiredArgsConstructor
@Service
public class HealthDataUpdater {

  private final ApplicationRepository applicationRepository;
  private final Clock clock;

  public void updateHealthData(String applicationId, FormDataDto formDataDto) {
    applicationRepository
        .findById(applicationId)
        .ifPresentOrElse(
            application -> {
              isAllowedForUpdate(application);
              updateHealthDisabilityData(application, formDataDto);
              updateAudit(application);
              applicationRepository.save(application);
            },
            () -> {
              throw new ApplicationNotFoundException(
                  "No application found against provided Application Id");
            });
  }

  private void isAllowedForUpdate(Application application) {
    if (!ApplicationState.HEALTH_AND_DISABILITY
        .name()
        .equals(application.getState().getCurrent())) {
      throw new ProhibitedActionException(
          "Current application status does not allow health and disability data to be updated");
    }
  }

  private void updateHealthDisabilityData(Application application, FormDataDto formDataDto) {
    if (application.getHealthDisabilityData() == null) {
      application.setHealthDisabilityData(FormData.builder().build());
    }
    application.getHealthDisabilityData().setData(formDataDto.getFormData());
    application.getHealthDisabilityData().setMeta(formDataDto.getMeta());
  }

  private void updateAudit(Application application) {
    application.getAudit().setLastModified(clock.instant());
  }
}
