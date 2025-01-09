package uk.gov.dwp.health.pip.application.manager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.exception.ApplicationNotFoundException;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.RegistrationSchema140;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v3.dto.RegistrationDto;
import uk.gov.dwp.health.pip.application.manager.repository.ApplicationRepository;
import uk.gov.dwp.health.pip.application.manager.service.mapper.RegistrationDataMapperV3;

@RequiredArgsConstructor
@Service
@Slf4j
public class RegistrationDataGetterV3 {

  private final ApplicationRepository repository;
  private final RegistrationDataMapperV3 registrationDataMapperV3;
  private final RegistrationDataMarshaller registrationDataMarshaller;

  public RegistrationDto getRegistrationDataByApplicationId(String applicationId) {
    log.info("About to get registration data for application {}", applicationId);

    Application application = getApplication(applicationId);

    RegistrationSchema140 registrationSchema = getDefinedRegistrationData(application);

    return registrationDataMapperV3.toDto(application, registrationSchema);

  }

  private Application getApplication(String applicationId) {
    return repository
            .findById(applicationId)
            .orElseThrow(() ->
                    new ApplicationNotFoundException(
                            "No registration data found for application id: " + applicationId));
  }

  private RegistrationSchema140 getDefinedRegistrationData(Application application) {
    return registrationDataMarshaller.marshallRegistrationData(
            application.getRegistrationData().getData());
  }
}
