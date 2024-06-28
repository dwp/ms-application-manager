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
import uk.gov.dwp.health.pip.application.manager.model.registration.data.PersonalDetailsSchema120;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.RegistrationSchema140;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.ResidenceAndPresenceSchema110;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v3.dto.AboutYourHealthDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v3.dto.AdditionalSupportDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v3.dto.PersonalDetailsDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v3.dto.RegistrationDto.LanguageEnum;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v3.dto.ResidenceAndPresenceDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v3.dto.StateDto;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class RegistrationDataMapperV3Test {

  @Mock private AboutYourHealthMapperV3 aboutYourHealthMapperV3;
  @Mock private AdditionalSupportMapperV3 additionalSupportMapperV3;
  @Mock private PersonalDetailsMapperV3 personalDetailsMapperV3;
  @Mock private ResidenceAndPresenceMapperV3 residenceAndPresenceMapperV3;
  @Mock private StateDtoMapperV3 stateDtoMapperV3;

  @InjectMocks private RegistrationDataMapperV3 registrationDataMapperV3;

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

    when(personalDetailsMapperV3.toDto(registrationSchema100.getPersonalDetails()))
        .thenReturn(personalDetailsDto);
    when(aboutYourHealthMapperV3.toDto(registrationSchema100.getAboutYourHealth()))
        .thenReturn(aboutYourHealthDto);
    when(additionalSupportMapperV3.toDto(registrationSchema100.getAdditionalSupport()))
            .thenReturn(additionalSupportDto);
    when(residenceAndPresenceMapperV3.toDto(registrationSchema100.getResidenceAndPresence()))
            .thenReturn(residenceAndPresenceDto);
    when(stateDtoMapperV3.toDto(application.getState()))
        .thenReturn(stateDto);

    var registrationDto = registrationDataMapperV3.toDto(application, registrationSchema100);

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
    registrationSchema.setPersonalDetails(personalDetails);
    registrationSchema.setAboutYourHealth(aboutYourHealth);
    registrationSchema.setResidenceAndPresence(residenceAndPresence);
    registrationSchema.setAdditionalSupport(additionalSupport);
    return registrationSchema;
  }
}
