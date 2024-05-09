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
import uk.gov.dwp.health.pip.application.manager.model.registration.data.AboutYourHealthSchema110;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.AdditionalSupportSchema100;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.PersonalDetailsSchema110;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.RegistrationSchema130;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.ResidenceAndPresenceSchema110;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v2.dto.AboutYourHealthDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v2.dto.AdditionalSupportDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v2.dto.PersonalDetailsDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v2.dto.ResidenceAndPresenceDto;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class RegistrationDataMapperV2Test {

  @Mock private AdditionalSupportMapperV2 additionalSupportMapperV2;
  @Mock private AboutYourHealthMapperV2 aboutYourHealthMapperV2;
  @Mock private PersonalDetailsMapperV2 personalDetailsMapperV2;
  @Mock private ResidenceAndPresenceMapperV2 residenceAndPresenceMapperV2;

  @InjectMocks private RegistrationDataMapperV2 registrationDataMapperV2;

  @Test
  void when_mapping_form_to_api() {
    var application = Application.builder().dateRegistrationSubmitted(LocalDate.now()).build();

    RegistrationSchema130 registrationSchema = getRegistrationData();

    var personalDetailsDto = new PersonalDetailsDto();
    var aboutYourHealthDto = new AboutYourHealthDto();
    var residenceAndPresenceDto = new ResidenceAndPresenceDto();
    var additionalSupportDto = new AdditionalSupportDto();

    when(personalDetailsMapperV2.toDto(registrationSchema.getPersonalDetails()))
        .thenReturn(personalDetailsDto);
    when(aboutYourHealthMapperV2.toDto(registrationSchema.getAboutYourHealth()))
        .thenReturn(aboutYourHealthDto);
    when(residenceAndPresenceMapperV2.toDto(registrationSchema.getResidenceAndPresence()))
        .thenReturn(residenceAndPresenceDto);
    when(additionalSupportMapperV2.toDto(registrationSchema.getAdditionalSupport()))
        .thenReturn(additionalSupportDto);

    var registrationDto = registrationDataMapperV2.toDto(application, registrationSchema);

    assertThat(registrationDto.getSubmissionDate()).isEqualTo(LocalDate.now().toString());
    assertThat(registrationDto.getPersonalDetails()).isEqualTo(personalDetailsDto);
    assertThat(registrationDto.getAboutYourHealth()).isEqualTo(aboutYourHealthDto);
    assertThat(registrationDto.getResidenceAndPresence()).isEqualTo(residenceAndPresenceDto);
    assertThat(registrationDto.getAdditionalSupport()).isEqualTo(additionalSupportDto);
  }

  private RegistrationSchema130 getRegistrationData() {
    var registrationSchema = new RegistrationSchema130();
    var personalDetails = new PersonalDetailsSchema110();
    var aboutYourHealth = new AboutYourHealthSchema110();
    var residenceAndPresence = new ResidenceAndPresenceSchema110();
    var additionalSupport = new AdditionalSupportSchema100();
    registrationSchema.setPersonalDetails(personalDetails);
    registrationSchema.setAboutYourHealth(aboutYourHealth);
    registrationSchema.setResidenceAndPresence(residenceAndPresence);
    registrationSchema.setAdditionalSupport(additionalSupport);
    return registrationSchema;
  }
}
