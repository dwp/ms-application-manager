package uk.gov.dwp.health.pip.application.manager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.entity.State;
import uk.gov.dwp.health.pip.application.manager.exception.ApplicationNotFoundException;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.RegistrationSchema140;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v3.dto.RegistrationDto;
import uk.gov.dwp.health.pip.application.manager.repository.ApplicationRepository;
import uk.gov.dwp.health.pip.application.manager.service.mapper.RegistrationDataMapperV3;
import uk.gov.dwp.health.pip.application.manager.service.mapper.StateDtoMapperV3;

@RequiredArgsConstructor
@Service
@Slf4j
public class RegistrationDataGetterV3 {

  private final ApplicationRepository repository;
  private final RegistrationDataMapperV3 registrationDataMapperV3;
  private final RegistrationDataMarshaller registrationDataMarshaller;
  private final ApplicationCoordinatorService applicationCoordinatorService;
  private final StateDtoMapperV3 stateDtoMapperV3;

  public RegistrationDto getRegistrationDataByApplicationId(String applicationId) {
    log.info("About to get registration data for application {}", applicationId);

    Application application = getApplication(applicationId);

    RegistrationSchema140 registrationSchema = getDefinedRegistrationData(application);

    RegistrationDto registrationDto =
        registrationDataMapperV3.toDto(application, registrationSchema);

    try {
      State getRegistrationState = applicationCoordinatorService.getApplicationState(applicationId);
      registrationDto.setStateDto(stateDtoMapperV3.toDto(getRegistrationState));
      log.info("Got registration data for application {}", applicationId);
      return registrationDto;
    } catch (Exception exc) {
      log.info("Exception mapping of state from coordinator {}", exc.getMessage());
      return registrationDto;
    }
  }

  private Application getApplication(String applicationId) {
    return repository
        .findById(applicationId)
        .orElseThrow(
            () ->
                new ApplicationNotFoundException(
                    "No registration data found for application id: " + applicationId));
  }

  private RegistrationSchema140 getDefinedRegistrationData(Application application) {
    return registrationDataMarshaller.marshallRegistrationData(
        application.getRegistrationData().getData());
  }
}
