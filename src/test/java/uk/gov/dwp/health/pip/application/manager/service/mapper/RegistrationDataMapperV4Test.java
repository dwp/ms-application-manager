package uk.gov.dwp.health.pip.application.manager.service.mapper;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.entity.enums.Language;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.AboutYourHealthSchema120;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.AdditionalSupportSchema100;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.MotabilitySchemeSchema100;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.PersonalDetailsSchema120;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.RegistrationSchema140;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.ResidenceAndPresenceSchema110;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v4.dto.AboutYourHealthDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v4.dto.AdditionalSupportDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v4.dto.MotabilityDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v4.dto.PersonalDetailsDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v4.dto.RegistrationDto.LanguageEnum;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v4.dto.ResidenceAndPresenceDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v4.dto.StateDto;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class RegistrationDataMapperV4Test {
  
  @Mock private AboutYourHealthMapperV4 aboutYourHealthMapperV4;
  @Mock private AdditionalSupportMapperV4 additionalSupportMapperV4;
  @Mock private PersonalDetailsMapperV4 personalDetailsMapperV4;
  @Mock private ResidenceAndPresenceMapperV4 residenceAndPresenceMapperV4;
  @Mock private StateDtoMapperV4 stateDtoMapperV4;
  @Mock private MotabilityMapper motabilityMapper;
  
  @InjectMocks private RegistrationDataMapperV4 registrationDataMapperV4;
  
  @Test
  void when_mapping_form_to_api() {
    var testDate = LocalDate.now();
    
    var application = Application.builder()
      .dateRegistrationSubmitted(testDate)
      .effectiveFrom(testDate)
      .effectiveTo(testDate)
      .claimantId("123456879")
      .language(Language.CY)
      .build();
    
    RegistrationSchema140 registrationSchema100 = getRegistrationData();
    
    var personalDetailsDto = new PersonalDetailsDto();
    var aboutYourHealthDto = new AboutYourHealthDto();
    var residenceAndPresenceDto = new ResidenceAndPresenceDto();
    var additionalSupportDto = new AdditionalSupportDto();
    var stateDto = new StateDto();
    var motabilityDto = new MotabilityDto();
    
    when(personalDetailsMapperV4.toDto(registrationSchema100.getPersonalDetails()))
      .thenReturn(personalDetailsDto);
    when(aboutYourHealthMapperV4.toDto(registrationSchema100.getAboutYourHealth()))
      .thenReturn(aboutYourHealthDto);
    when(additionalSupportMapperV4.toDto(registrationSchema100.getAdditionalSupport()))
      .thenReturn(additionalSupportDto);
    when(residenceAndPresenceMapperV4.toDto(registrationSchema100.getResidenceAndPresence()))
      .thenReturn(residenceAndPresenceDto);
    when(stateDtoMapperV4.toDto(application.getState()))
      .thenReturn(stateDto);
    when(motabilityMapper.toDto(registrationSchema100.getMotabilityScheme()))
      .thenReturn(motabilityDto);
    
    var registrationDto = registrationDataMapperV4.toDto(application, registrationSchema100);
    
    assertThat(registrationDto.getClaimantId()).isEqualTo("123456879");
    assertThat(registrationDto.getEffectiveFrom()).isEqualTo(testDate.toString());
    assertThat(registrationDto.getEffectiveTo()).isEqualTo(testDate.toString());
    assertThat(registrationDto.getLanguage()).isEqualTo(LanguageEnum.CY);
    
    assertThat(registrationDto.getSubmissionDate()).isEqualTo(testDate.toString());
    assertThat(registrationDto.getPersonalDetails()).isEqualTo(personalDetailsDto);
    assertThat(registrationDto.getAboutYourHealth()).isEqualTo(aboutYourHealthDto);
    assertThat(registrationDto.getResidenceAndPresence()).isEqualTo(residenceAndPresenceDto);
    assertThat(registrationDto.getAdditionalSupport()).isEqualTo(additionalSupportDto);
    assertThat(registrationDto.getStateDto()).isEqualTo(stateDto);
  }
  
  private RegistrationSchema140 getRegistrationData() {
    var registrationSchema = new RegistrationSchema140();
    var personalDetails = new PersonalDetailsSchema120();
    var aboutYourHealth = new AboutYourHealthSchema120();
    var residenceAndPresence = new ResidenceAndPresenceSchema110();
    var additionalSupport = new AdditionalSupportSchema100();
    var motabilityScheme = new MotabilitySchemeSchema100();
    registrationSchema.setPersonalDetails(personalDetails);
    registrationSchema.setAboutYourHealth(aboutYourHealth);
    registrationSchema.setResidenceAndPresence(residenceAndPresence);
    registrationSchema.setAdditionalSupport(additionalSupport);
    registrationSchema.setMotabilityScheme(motabilityScheme);
    return registrationSchema;
  }
}
