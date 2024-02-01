package uk.gov.dwp.health.pip.application.manager.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.pip.application.manager.constant.RegistrationState;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.entity.History;
import uk.gov.dwp.health.pip.application.manager.entity.LegacyApplicationReference;
import uk.gov.dwp.health.pip.application.manager.entity.State;
import uk.gov.dwp.health.pip.application.manager.entity.enums.System;
import uk.gov.dwp.health.pip.application.manager.event.model.response.PipGatewayRespondEventSchemaV1;
import uk.gov.dwp.health.pip.application.manager.exception.ApplicationNotFoundException;
import uk.gov.dwp.health.pip.application.manager.repository.ApplicationRepository;

import java.time.Clock;

@Service
@RequiredArgsConstructor
public class RegistrationResponseUpdater {

  protected static Logger log = LoggerFactory.getLogger(RegistrationResponseUpdater.class);
  private final ApplicationRepository repository;
  private final Clock clock;

  public void updateApplicationWithRegistrationResponse(PipGatewayRespondEventSchemaV1 response) {
    repository
        .findById(response.getApplicationId())
        .ifPresentOrElse(
            application -> {
              updateLegacyApplicationReference(application, response);
              updateRegistrationState(application, response);
              updateAudit(application);
              repository.save(application);
            },
            () -> {
              log.info(
                  "No application found against provided Application ID: {}",
                  response.getApplicationId());
              throw new ApplicationNotFoundException(
                  "No application found against provided Application ID");
            });
  }

  private void updateRegistrationState(
      Application application, PipGatewayRespondEventSchemaV1 response) {
    final State pipcsRegistrationState = application.getPipcsRegistrationState();
    final History history = History.builder()
        .state(response.getState())
        .timeStamp(clock.instant()).build();
    pipcsRegistrationState.addHistory(history);
    pipcsRegistrationState.setError(response.getMessage());
    if (RegistrationState.VALIDATION_FAILED.getLabel().equals(response.getState())) {
      log.warn("Application [{}] submission failed with state [{}] details message {}",
          response.getApplicationId(), response.getState(), response.getMessage());
    }
  }

  private void updateLegacyApplicationReference(
      Application application, PipGatewayRespondEventSchemaV1 response) {
    LegacyApplicationReference legacyApplicationRef;
    if (application.getLegacyApplicationReference() == null) {
      legacyApplicationRef = new LegacyApplicationReference();
    } else {
      legacyApplicationRef = application.getLegacyApplicationReference();
    }
    if (!response.getApplicationRef().isBlank()) {
      legacyApplicationRef.setApplicationReference(response.getApplicationRef());
    }
    legacyApplicationRef.setSystem(System.PIPCS);
    application.setLegacyApplicationReference(legacyApplicationRef);
  }

  private void updateAudit(Application application) {
    application.getAudit().setLastModified(clock.instant());
  }
}
