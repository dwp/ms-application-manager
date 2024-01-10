package uk.gov.dwp.health.pip.application.manager.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.pip.application.manager.constant.ApplicationState;
import uk.gov.dwp.health.pip.application.manager.constant.RegistrationState;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.entity.FormData;
import uk.gov.dwp.health.pip.application.manager.entity.History;
import uk.gov.dwp.health.pip.application.manager.entity.State;
import uk.gov.dwp.health.pip.application.manager.event.model.request.PipGatewayRequestEventSchemaV1;
import uk.gov.dwp.health.pip.application.manager.exception.ApplicationNotFoundException;
import uk.gov.dwp.health.pip.application.manager.exception.ProhibitedActionException;
import uk.gov.dwp.health.pip.application.manager.exception.RegistrationDataNotValid;
import uk.gov.dwp.health.pip.application.manager.messaging.PipcsApiMessagePublisher;
import uk.gov.dwp.health.pip.application.manager.messaging.properties.InboundEventProperties;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.AboutYourHealthSchema110;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.RegistrationSchema120;
import uk.gov.dwp.health.pip.application.manager.repository.ApplicationRepository;
import uk.gov.dwp.health.pip.application.manager.service.mapper.RegistrationDataMapperForPipcs;
import uk.gov.dwp.health.pip.pipcsapimodeller.Pip1RegistrationForm;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationSubmitter {

  private final ApplicationRepository repository;
  private final Clock clock;
  private final ObjectMapper objectMapper;
  private final PipcsApiMessagePublisher publisher;
  private final InboundEventProperties inboundEventProperties;
  private final RegistrationDataMapperForPipcs registrationDataMapper;
  private final RegistrationDataMarshaller registrationDataMarshaller;

  @Value("${pip.registration.resubmit.timeout.second:3600}")
  protected int resubmitTimeoutSecond;

  public void submitRegistrationData(String applicationId) {
    Application application = getApplication(applicationId);
    isAllowedSubmitRegistration(application);
    RegistrationSchema120 registrationSchema = marshallRegistrationData(application);
    setClaimantDetails(registrationSchema, application);
    setDateRegistrationSubmitted(application);
    Pip1RegistrationForm pip1RegistrationForm =
        mapRegistrationData(application, registrationSchema);
    publishRegistrationData(application, pip1RegistrationForm);
    setRegistrationState(application);
    setApplicationState(application);
    setAudit(application);
    setHealthDisabilityFormData(registrationSchema, application);
    repository.save(application);
  }

  private Application getApplication(String applicationId) {
    return repository
        .findById(applicationId)
        .orElseThrow(
            () ->
                new ApplicationNotFoundException(
                    "No application found against provided Application ID"));
  }

  private void setClaimantDetails(
      RegistrationSchema120 registrationData, Application application
  ) {
    application.setForename(registrationData.getPersonalDetails().getFirstname());
    application.setSurname(registrationData.getPersonalDetails().getSurname());
    application.setNino(registrationData.getPersonalDetails().getNino());
  }

  private RegistrationSchema120 marshallRegistrationData(Application application) {
    return registrationDataMarshaller.marshallRegistrationData(
        application.getRegistrationData().getData()
    );
  }

  private void setDateRegistrationSubmitted(Application application) {
    application.setDateRegistrationSubmitted(LocalDate.now());
  }

  private Pip1RegistrationForm mapRegistrationData(
      Application application, RegistrationSchema120 registrationSchema) {
    return registrationDataMapper.mapRegistrationData(
        application.getId(), application.getDateRegistrationSubmitted(), registrationSchema);
  }

  private void publishRegistrationData(
      Application application, Pip1RegistrationForm pip1RegistrationForm) {

    String valueAsString;
    try {
      valueAsString = objectMapper.writeValueAsString(pip1RegistrationForm);
      log.debug("Registration Payload {}", valueAsString);
    } catch (JsonProcessingException e) {
      log.error("Registration data not valid will not publish request to PIPCS-gw");
      throw new RegistrationDataNotValid("Registration data not valid");
    }

    var event = new PipGatewayRequestEventSchemaV1();
    event.setApplicationId(application.getId());
    event.setPayload(valueAsString);
    event.setRespondTopic(inboundEventProperties.getTopicName());
    event.setRespondMessageRoutingKey(inboundEventProperties.getRoutingKeyRegistrationResponse());
    publisher.publishMessage(event);
  }

  private void setRegistrationState(Application application) {
    if (application.getPipcsRegistrationState() == null) {
      application.setPipcsRegistrationState(State.builder().build());
    }
    var submitting =
        History.builder()
            .state(RegistrationState.PENDING.getLabel())
            .timeStamp(clock.instant())
            .build();
    application.getPipcsRegistrationState().addHistory(submitting);
  }

  private void setApplicationState(Application application) {
    var registrationCompleted =
        History.builder()
            .state(ApplicationState.HEALTH_AND_DISABILITY.toString())
            .timeStamp(clock.instant())
            .build();
    application.getState().addHistory(registrationCompleted);
  }

  private void setAudit(Application application) {
    application.getAudit().setLastModified(clock.instant());
  }

  private void isAllowedSubmitRegistration(Application application) {
    if (application.getPipcsRegistrationState() != null) {
      var currentRegistrationState = application.getPipcsRegistrationState().getCurrent();
      if (currentRegistrationState != null
          && currentRegistrationState.equalsIgnoreCase(RegistrationState.PENDING.getLabel())
          && !isResubmitTimeoutElapsed(application.getAudit().getLastModified())) {
        throw new ProhibitedActionException(
            "Registration data submission is not allowed when registration "
                + "is pending and time lock active");
      }
    }
    var currentApplicationState = application.getState().getCurrent();
    if (ApplicationState.valueOf(currentApplicationState).getValue()
            == ApplicationState.SUBMITTED.getValue()
        || application.getRegistrationData().getData() == null) {
      throw new ProhibitedActionException(
          "Registration data submission is not allowed after application submitted");
    }
  }

  private void setHealthDisabilityFormData(
      RegistrationSchema120 registrationSchema, Application application) {
    var aboutYourHealthFromRegistration = registrationSchema.getAboutYourHealth();
    var healthProfessionalsDetailsList =
        aboutYourHealthFromRegistration != null
            ? aboutYourHealthFromRegistration.getHealthProfessionalsDetails()
            : null;

    var aboutYourHealth = new AboutYourHealthSchema110();
    aboutYourHealth.setHealthProfessionalsDetails(
        healthProfessionalsDetailsList != null
            ? List.copyOf(healthProfessionalsDetailsList)
            : null);

    application.setHealthDisabilityData(FormData.builder().data(aboutYourHealth).build());
  }

  private boolean isResubmitTimeoutElapsed(Instant lastModified) {
    return clock.instant().minus(resubmitTimeoutSecond, ChronoUnit.SECONDS).isAfter(lastModified);
  }
}
