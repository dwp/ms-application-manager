package uk.gov.dwp.health.pip.application.manager.api.v1;

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
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v1.dto.ApplicationStatusDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v1.dto.FormDataDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v1.dto.RegistrationDto;
import uk.gov.dwp.health.pip.application.manager.service.ApplicationStatusGetter;
import uk.gov.dwp.health.pip.application.manager.service.RegistrationDataGetter;
import uk.gov.dwp.health.pip.application.manager.service.RegistrationDataUpdater;
import uk.gov.dwp.health.pip.application.manager.service.RegistrationSubmitter;

import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class RegistrationApiAdapterTest {

  @Mock private RegistrationDataGetter registrationDataGetter;
  @Mock private RegistrationDataUpdater registrationDataUpdater;
  @Mock private RegistrationSubmitter registrationSubmitter;
  @Mock private ApplicationStatusGetter applicationStatusGetter;

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

  @Test
  void should_return_application_status_application_status_getter_invoked() {
    var claimantId = UUID.randomUUID().toString();
    var applicationDto = new ApplicationStatusDto();
    applicationDto.setApplicationStatus(ApplicationStatusDto.ApplicationStatusEnum.REGISTRATION);
    when(applicationStatusGetter.getApplicationStatusByClaimantId(anyString()))
        .thenReturn(applicationDto);
    var actual = registrationApiAdapter.getApplicationStatus(claimantId);

    assertAll(
        "assert application status response to be 200 and body with current status",
        () -> {
          var httpStatus = actual.getStatusCode();
          assertThat(httpStatus).isEqualTo(HttpStatus.OK);
          var applicationStatus =
              Objects.requireNonNull(actual.getBody()).getApplicationStatus().getValue();
          assertThat(applicationStatus).isEqualTo("REGISTRATION");
        });
    var strCaptor = ArgumentCaptor.forClass(String.class);
    verify(applicationStatusGetter).getApplicationStatusByClaimantId(strCaptor.capture());
    assertThat(strCaptor.getValue()).isEqualTo(claimantId);
  }
}
