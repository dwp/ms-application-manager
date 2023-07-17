package uk.gov.dwp.health.pip.application.manager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.exception.ApplicationNotFoundException;
import uk.gov.dwp.health.pip.application.manager.openapi.healthdisability.v2.dto.HealthDisabilityDto;
import uk.gov.dwp.health.pip.application.manager.repository.ApplicationRepository;
import uk.gov.dwp.health.pip.application.manager.service.mapper.HealthDataMapperV2;
import uk.gov.dwp.health.pip2.common.Pip2HealthDisabilityForm;

@RequiredArgsConstructor
@Service
@Slf4j
public class HealthDataGetterV2 {

  private final ApplicationRepository applicationRepository;
  private final HealthDataMapperV2 healthDataMapperV2;
  private final HealthDataMarshaller healthDataMarshaller;

  public HealthDisabilityDto getHealthDataByApplicationId(String applicationId) {
    log.info("About to get v2 health data for application {}", applicationId);

    var application = getApplication(applicationId);

    var pip2HealthDisabilityForm = getDefinedHealthData(application);

    var healthDisabilityDto =
        healthDataMapperV2.toDto(application.getId(), pip2HealthDisabilityForm);

    log.info("Got v2 health data for application {}", applicationId);

    return healthDisabilityDto;
  }

  private Application getApplication(String applicationId) {
    return applicationRepository
        .findById(applicationId)
        .orElseThrow(
            () ->
                new ApplicationNotFoundException(
                    "No application found for provided Application Id"));
  }

  private Pip2HealthDisabilityForm getDefinedHealthData(Application application) {
    return healthDataMarshaller.marshallHealthData(application.getHealthDisabilityData().getData());
  }
}
