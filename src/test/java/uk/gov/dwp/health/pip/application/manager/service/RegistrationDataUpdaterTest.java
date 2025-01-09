package uk.gov.dwp.health.pip.application.manager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.entity.Audit;
import uk.gov.dwp.health.pip.application.manager.entity.FormData;
import uk.gov.dwp.health.pip.application.manager.entity.State;
import uk.gov.dwp.health.pip.application.manager.exception.ApplicationNotFoundException;
import uk.gov.dwp.health.pip.application.manager.exception.ProhibitedActionException;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v1.dto.FormDataDto;
import uk.gov.dwp.health.pip.application.manager.repository.ApplicationRepository;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class RegistrationDataUpdaterTest {

  @InjectMocks private RegistrationDataUpdater registrationDataUpdater;
  @Mock private ApplicationRepository repository;
  @Mock private ApplicationCoordinatorService applicationCoordinatorService;
  @Mock private Clock clock;
  @Captor private ArgumentCaptor<String> applicationIdArgumentCaptor;
  @Captor private ArgumentCaptor<Application> applicationArgumentCaptor;

  @Test
  void when_update_registration_data_throws_application_not_found_exception() {
    var applicationId = UUID.randomUUID().toString();
    var formDataDto = new FormDataDto();
    when(repository.findById(anyString())).thenReturn(Optional.empty());
    assertThatThrownBy(
            () ->
                registrationDataUpdater.updateRegistrationDataByApplicationId(
                    applicationId, formDataDto))
        .isInstanceOf(ApplicationNotFoundException.class)
        .hasMessageContaining("No application found against provided Application ID");
    verify(repository).findById(applicationIdArgumentCaptor.capture());
    assertThat(applicationIdArgumentCaptor.getValue()).isEqualTo(applicationId);
  }

  @Test
  void when_update_registration_data_registration_data_audit_updated() {

    var applicationId = UUID.randomUUID().toString();
    var now = Instant.now();

    var formDataDto = new FormDataDto();
    formDataDto.setFormData("{NEW_FORM_DATA}");
    formDataDto.setMeta("meta");

    var existingApplication = applicationFixture();
    existingApplication.setId(applicationId);

    when(repository.findById(anyString())).thenReturn(Optional.of(existingApplication));
    when(clock.instant()).thenReturn(now);
    when(applicationCoordinatorService.getApplicationState(applicationId))
        .thenReturn(State.builder().current("REGISTRATION").build());

    registrationDataUpdater.updateRegistrationDataByApplicationId(applicationId, formDataDto);

    verify(repository).findById(applicationIdArgumentCaptor.capture());
    verify(repository).save(applicationArgumentCaptor.capture());
    assertAll(
        "assert application updated with new values",
        () -> {
          var updatedApp = applicationArgumentCaptor.getValue();
          assertThat(updatedApp.getRegistrationData().getData()).isEqualTo("{NEW_FORM_DATA}");
          assertThat(updatedApp.getRegistrationData().getMeta()).isEqualTo("meta");
          assertThat(updatedApp.getAudit().getLastModified()).isEqualTo(now);
        });
  }

  @Test
  void when_update_application_throws_exception_application_status_not_allowed_to_be_updated() {

    var applicationId = UUID.randomUUID().toString();
    var application = Application.builder()
            .id(applicationId)
            .state(State.builder().current("SUBMITTED").build())
            .build();

    when(repository.findById(anyString())).thenReturn(Optional.of(application));
    when(applicationCoordinatorService.getApplicationState(applicationId))
        .thenReturn(State.builder().current("SUBMITTED").build());

    var formDataDto = new FormDataDto();
    assertThatThrownBy(
            () ->
                registrationDataUpdater.updateRegistrationDataByApplicationId(
                    applicationId, formDataDto))
        .isInstanceOf(ProhibitedActionException.class)
        .hasMessageContaining("Current application status not allow application to be updated");
  }

  private Application applicationFixture() {
    var application = new Application();
    var formData = new FormData();
    formData.setData("{OLD_FORM_DATA}");
    application.setRegistrationData(formData);
    var audit = new Audit();
    audit.setLastModified(Instant.now().minus(1, ChronoUnit.DAYS));
    application.setAudit(audit);
    return application;
  }
}
