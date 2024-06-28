package uk.gov.dwp.health.pip.application.manager.service.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.RegistrationSchema140;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v2.dto.RegistrationDto;

import java.time.LocalDate;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RegistrationDataMapperV2 {

  private final AboutYourHealthMapperV2 aboutYourHealthMapperV2;
  private final AdditionalSupportMapperV2 additionalSupportMapperV2;
  private final PersonalDetailsMapperV2 personalDetailsMapperV2;
  private final ResidenceAndPresenceMapperV2 residenceAndPresenceMapperV2;

  public RegistrationDto toDto(Application application, RegistrationSchema140 registrationSchema) {
    return new RegistrationDto()
        .submissionDate(getSubmissionDate(application))
        .personalDetails(personalDetailsMapperV2.toDto(registrationSchema.getPersonalDetails()))
        .aboutYourHealth(aboutYourHealthMapperV2.toDto(registrationSchema.getAboutYourHealth()))
        .residenceAndPresence(
            residenceAndPresenceMapperV2.toDto(registrationSchema.getResidenceAndPresence()))
        .additionalSupport(
            additionalSupportMapperV2.toDto(registrationSchema.getAdditionalSupport()));
  }

  private String getSubmissionDate(Application application) {
    return Optional.ofNullable(application.getDateRegistrationSubmitted())
        .map(LocalDate::toString)
        .orElse("");
  }
}
