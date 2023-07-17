package uk.gov.dwp.health.pip.application.manager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.openapi.v1.dto.ApplicationId;
import uk.gov.dwp.health.pip.application.manager.repository.ApplicationRepository;

@RequiredArgsConstructor
@Service
@Slf4j
public class ApplicationIdsByNinoGetter {

  private final ApplicationRepository applicationRepository;

  public ApplicationId getApplicationIdByNino(String nino) {
    log.info("About to get application id for the given nino");

    NinoValidator.validate(nino);

    var applications = applicationRepository.findApplicationIdsByNino(nino);
    log.info("Number of application ids found for given nino is {}", applications.size());

    if (applications.size() > 1) {
      throw new IllegalStateException("Multiple applications found for given NINO");
    }

    var applicationId = new ApplicationId();

    if (!applications.isEmpty()) {
      applicationId = toDto(applications.get(0));
    }

    log.info("Finished getting application id for the given nino");

    return applicationId;
  }

  private ApplicationId toDto(Application application) {
    return new ApplicationId().applicationId(application.getId());
  }
}
