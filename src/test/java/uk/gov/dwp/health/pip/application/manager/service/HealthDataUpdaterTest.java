package uk.gov.dwp.health.pip.application.manager.service;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.dwp.health.pip.application.manager.constant.ApplicationState;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.entity.State;
import uk.gov.dwp.health.pip.application.manager.exception.ApplicationNotFoundException;
import uk.gov.dwp.health.pip.application.manager.exception.ProhibitedActionException;
import uk.gov.dwp.health.pip.application.manager.openapi.healthdisability.v1.dto.FormDataDto;
import uk.gov.dwp.health.pip.application.manager.repository.ApplicationRepository;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Tag("unit")
class HealthDataUpdaterTest {

  @InjectMocks private HealthDataUpdater healthDataUpdater;
  @Mock private ApplicationRepository applicationRepository;
  @Mock private Clock clock;

  @Test
  void when_health_data_exists_then_update() {
    when(applicationRepository.findById("application-id-1"))
        .thenReturn(
            Optional.ofNullable(
                Application.builder()
                    .state(
                        State.builder()
                            .current(ApplicationState.HEALTH_AND_DISABILITY.name())
                            .build())
                    .build()));
    var now = Instant.now();
    when(clock.instant()).thenReturn(now);

    var formDataDto = new FormDataDto();
    formDataDto.formData("form data");
    formDataDto.meta("meta data");

    healthDataUpdater.updateHealthData("application-id-1", formDataDto);

    var applicationArgumentCaptor = ArgumentCaptor.forClass(Application.class);
    verify(applicationRepository, times(1)).save(applicationArgumentCaptor.capture());

    var application = applicationArgumentCaptor.getValue();
    assertThat(application.getHealthDisabilityData().getData()).isEqualTo("form data");
    assertThat(application.getHealthDisabilityData().getMeta()).isEqualTo("meta data");
    assertThat(application.getAudit().getLastModified()).isEqualTo(now);
  }

  @Test
  void when_application_doesnt_exist_then_not_found() {
    when(applicationRepository.findById("application-id-1")).thenReturn(Optional.empty());

    var formDataDto = new FormDataDto();

    assertThatThrownBy(() -> healthDataUpdater.updateHealthData("application-id-1", formDataDto))
        .isInstanceOf(ApplicationNotFoundException.class)
        .hasMessage("No application found against provided Application Id");
  }

  @Test
  void when_health_data_state_not_health_and_disability_then_prohibited_action() {
    when(applicationRepository.findById("application-id-1"))
        .thenReturn(
            Optional.ofNullable(
                Application.builder()
                    .state(State.builder().current(ApplicationState.REGISTRATION.name()).build())
                    .build()));

    var formDataDto = new FormDataDto();

    assertThatThrownBy(() -> healthDataUpdater.updateHealthData("application-id-1", formDataDto))
        .isInstanceOf(ProhibitedActionException.class)
        .hasMessage(
          "Current application status does not allow health and disability data to be updated");
  }
}
