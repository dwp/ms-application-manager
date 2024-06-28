package uk.gov.dwp.health.pip.application.manager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.exception.ApplicationNotFoundException;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.RegistrationSchema140;
import uk.gov.dwp.health.pip.application.manager.openapi.bankdetails.v1.dto.BankDetailsDto;
import uk.gov.dwp.health.pip.application.manager.repository.ApplicationRepository;
import uk.gov.dwp.health.pip.application.manager.service.mapper.BankDetailsMapperV1;

@RequiredArgsConstructor
@Service
@Slf4j
public class BankDetailsGetter {

  private final ApplicationRepository repository;
  private final BankDetailsMapperV1 bankDetailsMapperV1;
  private final RegistrationDataMarshaller registrationDataMarshaller;

  public BankDetailsDto getBankDetailsByApplicationId(String applicationId) {
    Application application = repository
          .findById(applicationId)
          .orElseThrow(
            () -> {
              throw new ApplicationNotFoundException(
                "No registration data found for application id: " + applicationId);
            });
    return bankDetailsMapperV1.toDto(getDefinedRegistrationData(application));
  }

  private RegistrationSchema140 getDefinedRegistrationData(Application application) {
    return registrationDataMarshaller.marshallRegistrationData(
        application.getRegistrationData().getData());
  }
}
