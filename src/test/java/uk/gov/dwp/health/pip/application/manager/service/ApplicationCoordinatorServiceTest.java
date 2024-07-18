package uk.gov.dwp.health.pip.application.manager.service;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.dwp.health.pip.application.manager.entity.State;
import uk.gov.dwp.health.pip.application.manager.exception.ApplicationNotFoundException;
import uk.gov.dwp.health.pip.application.manager.openapi.coordinator.DefaultMsCoordinatorClient;
import uk.gov.dwp.health.pip.application.manager.openapi.coordinator.dto.ActiveApplicationsDto;
import uk.gov.dwp.health.pip.application.manager.openapi.coordinator.dto.ApplicationStateDto;
import uk.gov.dwp.health.pip.application.manager.openapi.coordinator.dto.HistoryDto;
import uk.gov.dwp.health.pip.application.manager.openapi.coordinator.dto.StateDto;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class ApplicationCoordinatorServiceTest {

  private static final Instant DATE_TIME_1 = Instant.parse("2021-03-02T08:00:00.000Z");

  @Mock
  private DefaultMsCoordinatorClient defaultMsCoordinatorClient;
  @Mock
  private Clock clock;
  @InjectMocks
  private ApplicationCoordinatorService applicationCoordinatorService;

  @Test
  void when_getting_application_state() {

    String applicationId = "application-1-id";
    StateDto state = new StateDto().currentState(StateDto.CurrentStateEnum.REGISTRATION);
    HistoryDto history =
        new HistoryDto().state(HistoryDto.StateEnum.REGISTRATION).timestamp(DATE_TIME_1.toString());

    ApplicationStateDto applicationStateDto =
        new ApplicationStateDto()
            .applicationId(applicationId)
            .state(state)
            .history(List.of(history));

    when(clock.instant()).thenReturn(DATE_TIME_1);

    when(defaultMsCoordinatorClient.getApplication(
        applicationId, null, null, null)).thenReturn(applicationStateDto);

    State result = applicationCoordinatorService.getApplicationState(applicationId);

    assertThat(result.getCurrent()).isEqualTo("REGISTRATION");
    assertThat(result.getHistory().size()).isEqualTo(1);
    assertThat(result.getHistory().get(0).getState()).isEqualTo("REGISTRATION");
    assertThat(result.getHistory().get(0).getTimeStamp()).isEqualTo(DATE_TIME_1.toString());
  }

  @Test
  void when_getting_application_not_found() {

    String applicationId = "application-1-id";
    String msg = "No application found for passed id";

    when(defaultMsCoordinatorClient.getApplication(applicationId, null, null, null))
        .thenThrow(new ApplicationNotFoundException(msg));

    assertThatThrownBy(() -> applicationCoordinatorService.getApplicationState(applicationId))
        .isInstanceOf(ApplicationNotFoundException.class)
        .hasMessage("No application found for passed id");
  }

  @Test
  void when_getting_active_applications() {

    String applicationId = "application-1-id";

    when(defaultMsCoordinatorClient.getActiveApplications(List.of(applicationId)))
        .thenReturn(new ActiveApplicationsDto().activeApplications(true));

    Boolean result = applicationCoordinatorService.hasActiveApplications(List.of(applicationId));

    assertThat(result).isTrue();
  }

  @Test
  void when_getting_active_applications_with_empty_list() {

    Boolean result = applicationCoordinatorService.hasActiveApplications(List.of());

    verifyNoInteractions(defaultMsCoordinatorClient);

    assertThat(result).isFalse();
  }

  @Test
  void when_getting_registration_applications() {

    String applicationId = "application-1-id";

    when(defaultMsCoordinatorClient.getRegistrationApplications(List.of(applicationId)))
        .thenReturn(List.of(applicationId));

    List<String> result = applicationCoordinatorService.getRegistrationApplicationIds(List.of(applicationId));

    assertThat(result).isEqualTo(List.of(applicationId));
  }

  @Test
  void when_getting_registration_applications_with_empty_list() {

    List<String> result = applicationCoordinatorService.getRegistrationApplicationIds(List.of());

    verifyNoInteractions(defaultMsCoordinatorClient);

    assertThat(result.isEmpty()).isTrue();
  }

  @Test
  void when_creating_application() {

    String applicationId = "application-1-id";
    StateDto state = new StateDto().currentState(StateDto.CurrentStateEnum.REGISTRATION);
    HistoryDto history =
        new HistoryDto().state(HistoryDto.StateEnum.REGISTRATION).timestamp(DATE_TIME_1.toString());

    ApplicationStateDto applicationStateDto =
        new ApplicationStateDto()
            .applicationId(applicationId)
            .state(state)
            .history(List.of(history));

    when(clock.instant()).thenReturn(DATE_TIME_1);

    when(defaultMsCoordinatorClient.createApplication(applicationId)).thenReturn(applicationStateDto);

    State result = applicationCoordinatorService.postApplicationId(applicationId);

    assertThat(result.getCurrent()).isEqualTo("REGISTRATION");
    assertThat(result.getHistory().size()).isEqualTo(1);
    assertThat(result.getHistory().get(0).getState()).isEqualTo("REGISTRATION");
    assertThat(result.getHistory().get(0).getTimeStamp()).isEqualTo(DATE_TIME_1.toString());
  }

}
