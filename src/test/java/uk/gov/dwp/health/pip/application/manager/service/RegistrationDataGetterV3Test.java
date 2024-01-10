package uk.gov.dwp.health.pip.application.manager.service;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.entity.FormData;
import uk.gov.dwp.health.pip.application.manager.exception.ApplicationNotFoundException;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.RegistrationSchema120;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v3.dto.RegistrationDto;
import uk.gov.dwp.health.pip.application.manager.repository.ApplicationRepository;
import uk.gov.dwp.health.pip.application.manager.service.mapper.RegistrationDataMapperV3;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class RegistrationDataGetterV3Test {

  @InjectMocks private RegistrationDataGetterV3 registrationDataGetterV3;
  @Mock private ApplicationRepository applicationRepository;
  @Mock private RegistrationDataMapperV3 registrationDataMapperV3;
  @Mock private RegistrationDataMarshaller registrationDataMarshaller;

  @Test
  void getRegistrationDataByApplicationId() {
    var application =
        Application.builder()
            .registrationData(FormData.builder().data("{registration form data}").build())
            .build();
    var registrationSchema = new RegistrationSchema120();
    var expectedRegistrationDto = new RegistrationDto();

    when(applicationRepository.findById("application-id-1")).thenReturn(Optional.of(application));
    when(registrationDataMarshaller.marshallRegistrationData("{registration form data}"))
        .thenReturn(registrationSchema);
    when(registrationDataMapperV3.toDto(application, registrationSchema))
        .thenReturn(expectedRegistrationDto);

    var registrationDto =
        registrationDataGetterV3.getRegistrationDataByApplicationId("application-id-1");

    assertThat(registrationDto).isNotNull();
  }

  @Test
  void when_no_applications_for_claimant_then_application_not_found() {
    when(applicationRepository.findById("application-id-1")).thenReturn(Optional.empty());
    assertThatThrownBy(
            () -> registrationDataGetterV3.getRegistrationDataByApplicationId("application-id-1"))
        .isInstanceOf(ApplicationNotFoundException.class)
        .hasMessageContaining("No registration data found for application id: application-id-1");
  }
}
