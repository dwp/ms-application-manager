package uk.gov.dwp.health.pip.application.manager.service.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.RegistrationSchema140;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v4.dto.RegistrationDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v4.dto.RegistrationDto.LanguageEnum;

import java.time.LocalDate;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RegistrationDataMapperV4 {
  
  private final AboutYourHealthMapperV4 aboutYourHealthMapperV4;
  private final MotabilityMapper motabilityMapper;
  private final AdditionalSupportMapperV4 additionalSupportMapperV4;
  private final PersonalDetailsMapperV4 personalDetailsMapperV4;
  private final ResidenceAndPresenceMapperV4 residenceAndPresenceMapperV4;
  private final StateDtoMapperV4 stateDtoMapperV4;
  
  public RegistrationDto toDto(Application application, RegistrationSchema140 registrationSchema) {
    return new RegistrationDto()
      .claimantId(application.getClaimantId())
      .effectiveFrom(application.getEffectiveFrom().toString())
      .effectiveTo(application.getEffectiveTo().toString())
      .submissionDate(getSubmissionDate(application))
      .channelType(getChannelType(registrationSchema))
      .identityConfirmed(getIdentityConfirmed(registrationSchema))
      .userId(registrationSchema.getUserId())
      .motabilityScheme(motabilityMapper.toDto(registrationSchema.getMotabilityScheme()))
      .personalDetails(personalDetailsMapperV4.toDto(registrationSchema.getPersonalDetails()))
      .aboutYourHealth(aboutYourHealthMapperV4.toDto(registrationSchema.getAboutYourHealth()))
      .residenceAndPresence(
        residenceAndPresenceMapperV4.toDto(registrationSchema.getResidenceAndPresence()))
      .additionalSupport(
        additionalSupportMapperV4.toDto(registrationSchema.getAdditionalSupport()))
      .stateDto(stateDtoMapperV4.toDto(application.getState()))
      .language(LanguageEnum.valueOf(application.getLanguage().name()));
  }
  
  private RegistrationDto.IdentityConfirmedEnum getIdentityConfirmed(
      RegistrationSchema140 registrationSchema) {
    var identityConfirmed =  registrationSchema.getIdentityConfirmed();
    return identityConfirmed != null
      ? RegistrationDto.IdentityConfirmedEnum.fromValue(identityConfirmed.toString()) : null;
  }
  
  private String getSubmissionDate(Application application) {
    return Optional.ofNullable(application.getDateRegistrationSubmitted())
      .map(LocalDate::toString)
      .orElse("");
  }
  
  private RegistrationDto.ChannelTypeEnum getChannelType(RegistrationSchema140 registrationSchema) {
    var channelType = registrationSchema.getChannelType();
    return channelType != null
      ? RegistrationDto.ChannelTypeEnum.fromValue(String.valueOf(channelType)) : null;
  }
}
