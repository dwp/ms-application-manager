package uk.gov.dwp.health.pip.application.manager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.pip.application.manager.constant.ApplicationState;
import uk.gov.dwp.health.pip.application.manager.entity.State;
import uk.gov.dwp.health.pip.application.manager.openapi.coordinator.DefaultMsCoordinatorClient;
import uk.gov.dwp.health.pip.application.manager.openapi.coordinator.dto.ActiveApplicationsDto;
import uk.gov.dwp.health.pip.application.manager.openapi.coordinator.dto.ApplicationCoordinatorDto;
import uk.gov.dwp.health.pip.application.manager.openapi.coordinator.dto.StateDto;

import java.time.Clock;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApplicationCoordinatorService {

  private final DefaultMsCoordinatorClient defaultMsCoordinatorClient;
  private final Clock clock;

  public State getApplicationState(String applicationId) {
    ApplicationCoordinatorDto application = defaultMsCoordinatorClient.getApplication(
        applicationId, null, null, null);
    log.info("Response received from post to ms-pip-application-coordinator");
    return getApplicationState(application);
  }

  public State getApplicationState(ApplicationCoordinatorDto application) {
    ApplicationState state =
        ApplicationState.valueOf(application.getState().getCurrentState().getValue());
    return State.builder()
        .current(state.name())
        .build();
  }

  public ApplicationCoordinatorDto getApplicationCoordinatorDto(String applicationId) {
    return defaultMsCoordinatorClient.getApplication(
        applicationId, null, null, null);
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

  public State postApplicationId(String applicationId, String claimantId) {
    log.info(
        "About to make post to create application in ms-pip-application-coordinator for {}",
        applicationId);

    ApplicationCoordinatorDto dto = new ApplicationCoordinatorDto();
    dto.setApplicationId(applicationId);
    dto.setClaimantId(claimantId);

    ApplicationCoordinatorDto applicationCoordinatorDto =
        defaultMsCoordinatorClient.createApplication(dto);

    log.info("Response received from post to ms-pip-application-coordinator");

    ApplicationState state =
        ApplicationState.valueOf(applicationCoordinatorDto.getState().getCurrentState().getValue());

    return State.builder()
        .current(state.name())
        .build();
  }

  public void updateState(String applicationId, StateDto.CurrentStateEnum state) {
    ApplicationCoordinatorDto applicationCoordinatorDto =
        new ApplicationCoordinatorDto()
            .applicationId(applicationId)
            .state(new StateDto().currentState(state));
    defaultMsCoordinatorClient.updateApplication(applicationCoordinatorDto);
    log.info("Response received from post to ms-pip-application-coordinator");
  }
}
