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
import uk.gov.dwp.health.pip.application.manager.model.registration.data.AboutYourHealth;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.AdditionalSupport;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.PersonalDetails;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.RegistrationSchema100;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.ResidenceAndPresence;
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

    RegistrationSchema100 registrationSchema100 = getRegistrationData();

    var personalDetailsDto = new PersonalDetailsDto();
    var aboutYourHealthDto = new AboutYourHealthDto();
    var residenceAndPresenceDto = new ResidenceAndPresenceDto();
    var additionalSupportDto = new AdditionalSupportDto();

    when(personalDetailsMapperV2.toDto(registrationSchema100.getPersonalDetails()))
        .thenReturn(personalDetailsDto);
    when(aboutYourHealthMapperV2.toDto(registrationSchema100.getAboutYourHealth()))
        .thenReturn(aboutYourHealthDto);
    when(residenceAndPresenceMapperV2.toDto(registrationSchema100.getResidenceAndPresence()))
        .thenReturn(residenceAndPresenceDto);
    when(additionalSupportMapperV2.toDto(registrationSchema100.getAdditionalSupport()))
        .thenReturn(additionalSupportDto);

    var registrationDto = registrationDataMapperV2.toDto(application, registrationSchema100);

    assertThat(registrationDto.getSubmissionDate()).isEqualTo(LocalDate.now().toString());
    assertThat(registrationDto.getPersonalDetails()).isEqualTo(personalDetailsDto);
    assertThat(registrationDto.getAboutYourHealth()).isEqualTo(aboutYourHealthDto);
    assertThat(registrationDto.getResidenceAndPresence()).isEqualTo(residenceAndPresenceDto);
    assertThat(registrationDto.getAdditionalSupport()).isEqualTo(additionalSupportDto);
  }

  private RegistrationSchema100 getRegistrationData() {
    var registrationSchema100 = new RegistrationSchema100();
    var personalDetails = new PersonalDetails();
    var aboutYourHealth = new AboutYourHealth();
    var residenceAndPresence = new ResidenceAndPresence();
    var additionalSupport = new AdditionalSupport();
    registrationSchema100.setPersonalDetails(personalDetails);
    registrationSchema100.setAboutYourHealth(aboutYourHealth);
    registrationSchema100.setResidenceAndPresence(residenceAndPresence);
    registrationSchema100.setAdditionalSupport(additionalSupport);
    return registrationSchema100;
  }
}
