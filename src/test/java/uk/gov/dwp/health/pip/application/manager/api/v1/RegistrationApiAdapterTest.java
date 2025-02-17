package uk.gov.dwp.health.pip.application.manager.api.v1;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v1.dto.ApplicationCoordinatorStatusDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v1.dto.ClaimantIdAndApplicationStatus;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v1.dto.FormDataDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v1.dto.RegistrationDto;
import uk.gov.dwp.health.pip.application.manager.service.RegistrationDataGetter;
import uk.gov.dwp.health.pip.application.manager.service.RegistrationDataUpdater;
import uk.gov.dwp.health.pip.application.manager.service.RegistrationSubmitter;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class RegistrationApiAdapterTest {

  @Mock private RegistrationDataGetter registrationDataGetter;
  @Mock private RegistrationDataUpdater registrationDataUpdater;
  @Mock private RegistrationSubmitter registrationSubmitter;
  @InjectMocks private RegistrationApiAdapter registrationApiAdapter;

  @Test
  void when_get_registration_data_called_registration_data_getter_invoked() {
    var claimantId = UUID.randomUUID().toString();
    var registrationDto = new RegistrationDto();
    registrationDto.setApplicationId(UUID.randomUUID().toString());
    registrationDto.setFormData("{form-data}");
    when(registrationDataGetter.getRegistrationDataByClaimantId(anyString()))
        .thenReturn(registrationDto);

    var actual = registrationApiAdapter.getRegistrationData(claimantId);
    var captor = ArgumentCaptor.forClass(String.class);
    verify(registrationDataGetter).getRegistrationDataByClaimantId(captor.capture());
    assertThat(captor.getValue()).isEqualTo(claimantId);
    assertAll(
        "assert endpoint response",
        () -> {
          assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
          assertThat(actual.getBody()).isEqualTo(registrationDto);
        });
  }

  @Test
  void when_getting_registration_data_by_application_id() {
    var registrationDto = new RegistrationDto();
    when(registrationDataGetter.getRegistrationDataByApplicationId("application-id-1"))
        .thenReturn(registrationDto);

    var response = registrationApiAdapter.getRegistrationDataByApplicationId("application-id-1");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo(registrationDto);
  }

  @Test
  void when_update_registration_data_called_registration_updater_invoked() {
    var applicationId = UUID.randomUUID().toString();
    var formDataDto = new FormDataDto().formData("{form-data}");
    var actual = registrationApiAdapter.updateRegistrationData(applicationId, formDataDto);
    assertAll(
        "assert endpoint response",
        () -> {
          assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
          assertThat(actual.getBody()).isNull();
        });
    var appIdCaptor = ArgumentCaptor.forClass(String.class);
    var formDtoCaptor = ArgumentCaptor.forClass(FormDataDto.class);
    verify(registrationDataUpdater)
        .updateRegistrationDataByApplicationId(appIdCaptor.capture(), formDtoCaptor.capture());
    assertThat(appIdCaptor.getValue()).isEqualTo(applicationId);
    assertThat(formDtoCaptor.getValue()).isEqualTo(formDataDto);
  }

  @Test
  void when_submit_registration_data_called_registration_submitter_invoked() {
    var applicationId = UUID.randomUUID().toString();
    var actual = registrationApiAdapter.registrationSubmission(applicationId);
    var captor = ArgumentCaptor.forClass(String.class);
    verify(registrationSubmitter).submitRegistrationData(captor.capture());
    assertThat(captor.getValue()).isEqualTo(applicationId);
    assertAll(
        "assert endpoint response",
        () -> {
          assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
          assertThat(actual.getBody()).isNull();
        });
  }

}
