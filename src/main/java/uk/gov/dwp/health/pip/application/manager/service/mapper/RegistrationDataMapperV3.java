package uk.gov.dwp.health.pip.application.manager.service.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.entity.State;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.RegistrationSchema140;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v3.dto.RegistrationDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v3.dto.RegistrationDto.LanguageEnum;
import uk.gov.dwp.health.pip.application.manager.service.ApplicationCoordinatorService;

import java.time.LocalDate;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class RegistrationDataMapperV3 {

  private final AboutYourHealthMapperV3 aboutYourHealthMapperV3;
  private final AdditionalSupportMapperV3 additionalSupportMapperV3;
  private final PersonalDetailsMapperV3 personalDetailsMapperV3;
  private final ResidenceAndPresenceMapperV3 residenceAndPresenceMapperV3;
  private final ApplicationCoordinatorService applicationCoordinatorService;
  private final StateDtoMapperV3 stateDtoMapperV3;

  public RegistrationDto toDto(Application application, RegistrationSchema140 registrationSchema) {
    RegistrationDto registrationDto = new RegistrationDto()
            .claimantId(application.getClaimantId())
            .effectiveFrom(application.getEffectiveFrom().toString())
            .effectiveTo(application.getEffectiveTo().toString())
            .submissionDate(getSubmissionDate(application))
            .personalDetails(personalDetailsMapperV3.toDto(registrationSchema.getPersonalDetails()))
            .aboutYourHealth(aboutYourHealthMapperV3.toDto(registrationSchema.getAboutYourHealth()))
            .residenceAndPresence(
                    residenceAndPresenceMapperV3.toDto(
                            registrationSchema.getResidenceAndPresence()))
            .additionalSupport(
                    additionalSupportMapperV3.toDto(registrationSchema.getAdditionalSupport()))
            .language(LanguageEnum.valueOf(application.getLanguage().name()));

    withState(registrationDto, application);

    return registrationDto;
  }

  private String getSubmissionDate(Application application) {
    return Optional.ofNullable(application.getDateRegistrationSubmitted())
            .map(LocalDate::toString)
            .orElse("");
  }

  private RegistrationDto withState(RegistrationDto registrationDto, Application application) {
    try {
      State getRegistrationState =
              applicationCoordinatorService.getApplicationState(application.getId());
      registrationDto.setStateDto(stateDtoMapperV3.toDto(getRegistrationState));
      log.info("Got registration data for application {}", application.getId());
      return registrationDto;
    } catch (Exception exc) {
      if (application.getState() != null) {
        registrationDto.stateDto(stateDtoMapperV3.toDto(application.getState()));
      }
      log.info("Exception mapping of state from coordinator {}", exc.getMessage());
      return registrationDto;
    }
  }
}
