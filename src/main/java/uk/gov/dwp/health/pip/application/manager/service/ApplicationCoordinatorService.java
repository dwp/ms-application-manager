package uk.gov.dwp.health.pip.application.manager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.pip.application.manager.constant.ApplicationState;
import uk.gov.dwp.health.pip.application.manager.entity.History;
import uk.gov.dwp.health.pip.application.manager.entity.State;
import uk.gov.dwp.health.pip.application.manager.openapi.coordinator.DefaultMsCoordinatorClient;
import uk.gov.dwp.health.pip.application.manager.openapi.coordinator.dto.ActiveApplicationsDto;
import uk.gov.dwp.health.pip.application.manager.openapi.coordinator.dto.ApplicationStateDto;
import uk.gov.dwp.health.pip.application.manager.openapi.coordinator.dto.HistoryDto;
import uk.gov.dwp.health.pip.application.manager.openapi.coordinator.dto.StateDto;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApplicationCoordinatorService {

  private final DefaultMsCoordinatorClient defaultMsCoordinatorClient;
  private final Clock clock;

  public State getApplicationState(String applicationId) {
    ApplicationStateDto application = defaultMsCoordinatorClient.getApplication(
        applicationId, null, null, null);
    log.info("Response received from post to ms-pip-application-coordinator");
    ApplicationState state =
        ApplicationState.valueOf(application.getState().getCurrentState().getValue());
    return State.builder()
        .current(state.name())
        .history(fromHistoryDtoToHistoryList(application.getHistory()))
        .build();
  }

  public Boolean hasActiveApplications(List<String> applications) {
    if (applications.isEmpty()) {
      return false;
    } else {

      log.info(
          String.format(
              "About to make post for active applications querying %s applications",
              applications.size()));

      ActiveApplicationsDto activeApplications =
          defaultMsCoordinatorClient.getActiveApplications(applications);

      log.info(
          "Response received from post for active applications"
              + " to ms-pip-application-coordinator: {}",
          activeApplications.isActiveApplications());

      return activeApplications.isActiveApplications();
    }
  }

  public List<String> getRegistrationApplicationIds(List<String> applicationIds) {
    if (applicationIds.isEmpty()) {
      return List.of();
    } else {
      log.info(
          String.format(
              "About to make post for registration applications querying %s applications",
              applicationIds.size()));

      List<String> registrationApplications =
          defaultMsCoordinatorClient.getRegistrationApplications(applicationIds);

      log.info(
          "Response received from post for registrations to ms-pip-application-coordinator: {}",
          registrationApplications.size());

      return registrationApplications;
    }
  }

  public State postApplicationId(String applicationId) {
    log.info(
        "About to make post to create application in ms-pip-application-coordinator for {}",
        applicationId);
    ApplicationStateDto applicationStateDto =
        defaultMsCoordinatorClient.createApplication(applicationId);
    log.info("Response received from post to ms-pip-application-coordinator");
    ApplicationState state =
        ApplicationState.valueOf(applicationStateDto.getState().getCurrentState().getValue());
    return State.builder()
        .current(state.name())
        .history(fromHistoryDtoToHistoryList(applicationStateDto.getHistory()))
        .build();
  }

  public void updateState(String applicationId, StateDto.CurrentStateEnum state) {
    ApplicationStateDto applicationStateDto =
        new ApplicationStateDto()
            .applicationId(applicationId)
            .state(new StateDto().currentState(state));
    defaultMsCoordinatorClient.updateApplication(applicationStateDto);
    log.info("Response received from post to ms-pip-application-coordinator");
  }

  private List<History> fromHistoryDtoToHistoryList(List<HistoryDto> historyDto) {
    List<History> historyList = new ArrayList<>(historyDto.size());
    for (HistoryDto dto : historyDto) {
      History history = new History(dto.getState().getValue(), clock.instant()); // change
      historyList.add(history);
    }
    return historyList;
  }
}
