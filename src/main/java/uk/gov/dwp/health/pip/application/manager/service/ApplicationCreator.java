package uk.gov.dwp.health.pip.application.manager.service;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.pip.application.manager.config.properties.ApplicationProperties;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.entity.Audit;
import uk.gov.dwp.health.pip.application.manager.entity.FormData;
import uk.gov.dwp.health.pip.application.manager.entity.State;
import uk.gov.dwp.health.pip.application.manager.entity.enums.Language;
import uk.gov.dwp.health.pip.application.manager.openapi.v1.dto.ApplicationCreateDto;
import uk.gov.dwp.health.pip.application.manager.openapi.v1.dto.ApplicationDto;
import uk.gov.dwp.health.pip.application.manager.repository.ApplicationRepository;

@RequiredArgsConstructor
@Service
@Slf4j
public class ApplicationCreator {

  private final ApplicationProperties applicationProperties;
  private final ApplicationRepository applicationRepository;
  private final ApplicationCoordinatorService applicationCoordinatorService;
  private final Clock clock;

  public ApplicationDto createApplication(ApplicationCreateDto applicationCreateDto) {
    log.info("About to create new application");

    if (hasActiveApplication(applicationCreateDto.getClaimantId())) {
      log.info("Claimant already has active application");
      throw new IllegalStateException("ERROR: claimant already has active application");
    }

    log.info("dto to model");
    Application application = toModel(applicationCreateDto);

    log.info("About to save to db");
    Application savedApplication = applicationRepository.save(application);

    log.info("model to dto");
    var applicationDto = toDto(savedApplication);

    log.info("Created new application");

    applicationCoordinatorService.postApplicationId(
        savedApplication.getId(), savedApplication.getClaimantId());

    return applicationDto;
  }

  private boolean hasActiveApplication(String claimantId) {
    List<Application> applications = applicationRepository.findAllByClaimantId(claimantId);
    List<String> ids = applications.stream().map(Application::getId).toList();
    return applicationCoordinatorService.hasActiveApplications(ids);
  }

  private Application toModel(ApplicationCreateDto applicationCreateDto) {
    LocalDate today = LocalDate.now();
    Instant now = clock.instant();

    return Application.builder()
        .audit(Audit.builder().created(now).lastModified(now).build())
        .benefitCode(applicationCreateDto.getBenefitType().getValue())
        .claimantId(applicationCreateDto.getClaimantId())
        .effectiveFrom(today)
        .effectiveTo(today.plusDays(applicationProperties.getActiveDuration()))
        .language(Language.valueOf(applicationCreateDto.getLanguage().getValue()))
        .registrationData(FormData.builder().build())
        .pipcsRegistrationState(State.builder().build())
        .build();
  }

  private ApplicationDto toDto(Application application) {
    return new ApplicationDto()
        .applicationId(application.getId());
  }
}
