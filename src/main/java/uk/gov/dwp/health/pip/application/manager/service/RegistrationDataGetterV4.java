package uk.gov.dwp.health.pip.application.manager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.exception.ApplicationNotFoundException;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.RegistrationSchema140;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v4.dto.RegistrationDto;
import uk.gov.dwp.health.pip.application.manager.repository.ApplicationRepository;
import uk.gov.dwp.health.pip.application.manager.service.mapper.RegistrationDataMapperV4;

@RequiredArgsConstructor
@Service
@Slf4j
public class RegistrationDataGetterV4 {
  
  private final ApplicationRepository repository;
  private final RegistrationDataMapperV4 registrationDataMapperV4;
  private final RegistrationDataMarshaller registrationDataMarshaller;
  
  public RegistrationDto getRegistrationDataByApplicationId(String applicationId) {
    log.info("About to get registration data for application {}", applicationId);
    
    var application = getApplication(applicationId);
    
    var registrationSchema = getDefinedRegistrationData(application);
    
    var registrationDto = registrationDataMapperV4.toDto(application, registrationSchema);
    
    log.info("Got registration data for application {}", applicationId);
    
    return registrationDto;
  }
  
  private Application getApplication(String applicationId) {
    return repository
      .findById(applicationId)
      .orElseThrow(
        () -> {
          throw new ApplicationNotFoundException(
            "No registration data found for application id: " + applicationId);
        });
  }
  
  private RegistrationSchema140 getDefinedRegistrationData(Application application) {
    return registrationDataMarshaller.marshallRegistrationData(
      application.getRegistrationData().getData());
  }
}
