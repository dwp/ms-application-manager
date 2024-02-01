package uk.gov.dwp.health.pip.application.manager.service;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.entity.Audit;
import uk.gov.dwp.health.pip.application.manager.entity.History;
import uk.gov.dwp.health.pip.application.manager.entity.State;
import uk.gov.dwp.health.pip.application.manager.entity.enums.System;
import uk.gov.dwp.health.pip.application.manager.event.model.response.PipGatewayRespondEventSchemaV1;
import uk.gov.dwp.health.pip.application.manager.exception.ApplicationNotFoundException;
import uk.gov.dwp.health.pip.application.manager.repository.ApplicationRepository;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class RegistrationResponseUpdaterTest {


  @InjectMocks private RegistrationResponseUpdater registrationDataUpdater;
  @Mock private ApplicationRepository repository;
  @Mock private Clock clock;

  private static final String APP_ID = UUID.randomUUID().toString();
  private static final String PIPCS_REF = UUID.randomUUID().toString();
  private static final Instant NOW = Instant.now();
  private static PipGatewayRespondEventSchemaV1 RESPONSE = responseFixture();

  @Test
  void when_pipcs_api_responded_application_not_found_exception_thrown() {
    when(repository.findById(anyString())).thenReturn(Optional.empty());

    assertThatThrownBy(() -> registrationDataUpdater.updateApplicationWithRegistrationResponse(RESPONSE))
        .isInstanceOf(ApplicationNotFoundException.class)
        .hasMessage("No application found against provided Application ID");
    var strCaptor = ArgumentCaptor.forClass(String.class);
    verify(repository).findById(strCaptor.capture());
    assertThat(strCaptor.getValue()).isEqualTo(APP_ID);
  }

  @Test
  void when_pipcs_api_responded_application_application_state_updated() {
    var application = applicationFixture();
    when(clock.instant()).thenReturn(NOW);
    when(repository.findById(anyString())).thenReturn(Optional.of(application));

    registrationDataUpdater.updateApplicationWithRegistrationResponse(RESPONSE);

    var captor = ArgumentCaptor.forClass(Application.class);
    verify(repository).save(captor.capture());

    var actualApplication = captor.getValue();
    assertAll(
        "assert application legacy reference",
        () -> {
          var actualLegacy = actualApplication.getLegacyApplicationReference();
          assertThat(actualLegacy.getApplicationReference()).isEqualTo(PIPCS_REF);
          assertThat(actualLegacy.getSystem()).isEqualTo(System.PIPCS);
        });
    assertAll(
        "assert registration state and history",
        () -> {
          var actual = actualApplication.getPipcsRegistrationState();
          assertThat(actual.getCurrent()).isEqualTo("SUBMITTED");
          assertThat(actual.getHistory().get(1).getTimeStamp()).isEqualTo(NOW);
          assertThat(actual.getHistory().size()).isEqualTo(2);
        });
    assertThat(application.getAudit().getLastModified()).isEqualTo(NOW);
  }

  @Test
  void should_log_traceable_message_when_response_state_is_not_submitted() {
    var application = applicationFixture();
    when(repository.findById(anyString())).thenReturn(Optional.of(application));
    var response = responseFixture();
    response.setState("Validation failed");
    response.setMessage("VALIDATION FAILED ON NAME");
    var mockLogger = mock(Logger.class);
    RegistrationResponseUpdater.log = mockLogger;
    ArgumentCaptor<Application> updatedApplication = ArgumentCaptor.forClass(Application.class);
    registrationDataUpdater.updateApplicationWithRegistrationResponse(response);
    verify(mockLogger).warn("Application [{}] submission failed with state [{}] details message {}",
        APP_ID , "Validation failed" , "VALIDATION FAILED ON NAME");
    verify(repository, times(1)).save(updatedApplication.capture());
    assertEquals(response.getMessage(), updatedApplication.getValue().getPipcsRegistrationState().getError());
  }

  private Application applicationFixture() {
    var application = new Application();
    application.setId(APP_ID);
    var state = new State();
    var history = new History();
    history.setState("SUBMITTING");
    state.addHistory(history);
    application.setPipcsRegistrationState(state);
    var audit = new Audit();
    audit.setLastModified(Instant.now().minus(1, ChronoUnit.DAYS));
    application.setAudit(audit);
    application.setState(State.builder().current("HEALTH_AND_DISABILITY").build());
    return application;
  }

  private static PipGatewayRespondEventSchemaV1 responseFixture() {
    var response = new PipGatewayRespondEventSchemaV1();
    response.setApplicationId(APP_ID);
    response.setApplicationRef(PIPCS_REF);
    response.setState("SUBMITTED");
    var now = Instant.now();
    response.setTimestamp(now.toString());
    return response;
  }
}
