package uk.gov.dwp.health.pip.application.manager.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.dwp.health.mongo.changestream.config.properties.Channel;
import uk.gov.dwp.health.pip.application.manager.constant.ApplicationState;
import uk.gov.dwp.health.pip.application.manager.constant.RegistrationState;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.entity.Audit;
import uk.gov.dwp.health.pip.application.manager.entity.FormData;
import uk.gov.dwp.health.pip.application.manager.entity.History;
import uk.gov.dwp.health.pip.application.manager.entity.State;
import uk.gov.dwp.health.pip.application.manager.event.model.request.PipGatewayRequestEventSchemaV1;
import uk.gov.dwp.health.pip.application.manager.exception.ApplicationNotFoundException;
import uk.gov.dwp.health.pip.application.manager.exception.ProhibitedActionException;
import uk.gov.dwp.health.pip.application.manager.exception.RegistrationDataNotValid;
import uk.gov.dwp.health.pip.application.manager.messaging.PipcsApiMessagePublisher;
import uk.gov.dwp.health.pip.application.manager.messaging.properties.InboundEventProperties;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.AboutYourHealthSchema100;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.HealthProfessionalsDetails100;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.PersonalDetailsSchema100;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.RegistrationSchema110;
import uk.gov.dwp.health.pip.application.manager.repository.ApplicationRepository;
import uk.gov.dwp.health.pip.application.manager.service.mapper.RegistrationDataMapperForPipcs;
import uk.gov.dwp.health.pip.pipcsapimodeller.Pip1RegistrationForm;
import uk.gov.dwp.health.pip.pipcsapimodeller.registration.type.YesNoDontKnow;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class RegistrationSubmitterTest {

  private static String APPLICATION_ID;
  private static Instant NOW;

  @InjectMocks private RegistrationSubmitter registrationSubmitter;
  @Mock private ApplicationRepository repository;
  @Mock private Clock clock;
  @Mock private ObjectMapper objectMapper;
  @Mock private PipcsApiMessagePublisher publisher;
  @Mock private InboundEventProperties inboundEventProperties;
  @Mock private RegistrationDataMapperForPipcs registrationDataMapper;
  @Mock private RegistrationDataMarshaller registrationDataMarshaller;

  @BeforeAll
  static void setup() {
    APPLICATION_ID = UUID.randomUUID().toString();
    NOW = Instant.now();
  }

  @BeforeEach
  void set_timeout() {
    registrationSubmitter.resubmitTimeoutSecond = 10;
  }

  @Test
  void when_application_doesnt_exist_then_throw_application_not_found() {
    when(repository.findById(anyString())).thenReturn(Optional.empty());
    assertThatThrownBy(() -> registrationSubmitter.submitRegistrationData(APPLICATION_ID))
        .isInstanceOf(ApplicationNotFoundException.class)
        .hasMessage("No application found against provided Application ID");
  }

  @Test
  void when_application_submitted_then_throw_prohibited_action_exception() {
    var application =
        Application.builder().state(State.builder().current("SUBMITTED").build()).build();

    when(repository.findById(anyString())).thenReturn(Optional.of(application));
    assertThatThrownBy(() -> registrationSubmitter.submitRegistrationData(APPLICATION_ID))
        .isInstanceOf(ProhibitedActionException.class)
        .hasMessage("Registration data submission is not allowed after application submitted");
  }

  @Test
  void when_registration_data_empty_then_throw_prohibited_action_exception() {
    var application =
        Application.builder()
            .audit(Audit.builder().build())
            .pipcsRegistrationState(State.builder().build())
            .registrationData(FormData.builder().build())
            .state(State.builder().current("REGISTRATION").build())
            .build();
    when(repository.findById(anyString())).thenReturn(Optional.of(application));
    assertThatThrownBy(() -> registrationSubmitter.submitRegistrationData(APPLICATION_ID))
        .isInstanceOf(ProhibitedActionException.class)
        .hasMessage("Registration data submission is not allowed after application submitted");
  }

  @Test
  void when_registration_data_not_valid() {
    Application application =
        Application.builder()
            .audit(Audit.builder().build())
            .pipcsRegistrationState(State.builder().build())
            .registrationData(FormData.builder().data("{bad data}").build())
            .state(State.builder().current("REGISTRATION").build())
            .build();
    when(repository.findById(APPLICATION_ID)).thenReturn(Optional.of(application));
    when(registrationDataMarshaller.marshallRegistrationData110(anyString()))
        .thenThrow(RegistrationDataNotValid.class);

    assertThatThrownBy(() -> registrationSubmitter.submitRegistrationData(APPLICATION_ID))
        .isInstanceOf(RegistrationDataNotValid.class);
  }

  @Test
  void when_pipcs_registration_state_null_then_submit_application() throws JsonProcessingException {
    var application = getApplicationFixture().toBuilder().pipcsRegistrationState(null).build();
    var registrationSchema = getRegistrationSchemaFixture();
    var pip1RegistrationForm = getPip1RegistrationFormFixture();

    when(repository.findById(APPLICATION_ID)).thenReturn(Optional.of(application));
    when(registrationDataMarshaller.marshallRegistrationData110("{good data}"))
        .thenReturn(registrationSchema);
    when(registrationDataMapper.mapRegistrationData(
            APPLICATION_ID, LocalDate.now(), registrationSchema))
        .thenReturn(pip1RegistrationForm);
    when(objectMapper.writeValueAsString(pip1RegistrationForm)).thenReturn("{pip-api-payload}");
    when(inboundEventProperties.getTopicName()).thenReturn("pipcs-api-request");
    when(inboundEventProperties.getRoutingKeyRegistrationResponse())
        .thenReturn("registration-response-routing-key");
    when(clock.instant()).thenReturn(NOW);
    Channel channel = new Channel();
    channel.setCollection("application");
    channel.setInstanceId("instance-id-123");

    registrationSubmitter.submitRegistrationData(APPLICATION_ID);

    var order = inOrder(publisher, repository);
    var eventCaptor = ArgumentCaptor.forClass(PipGatewayRequestEventSchemaV1.class);

    order.verify(publisher).publishMessage(eventCaptor.capture());
    assertPublishedEvent(eventCaptor.getValue());
    order.verify(repository).save(application);
    assertApplication(application);
  }

  @Test
  void when_all_good_then_update_application() throws JsonProcessingException {
    var application = getApplicationFixture();
    var registrationSchema = getRegistrationSchemaFixture();
    var pip1RegistrationForm = getPip1RegistrationFormFixture();

    when(repository.findById(APPLICATION_ID)).thenReturn(Optional.of(application));
    when(registrationDataMarshaller.marshallRegistrationData110("{good data}"))
        .thenReturn(registrationSchema);
    when(registrationDataMapper.mapRegistrationData(
            APPLICATION_ID, LocalDate.now(), registrationSchema))
        .thenReturn(pip1RegistrationForm);
    when(objectMapper.writeValueAsString(pip1RegistrationForm)).thenReturn("{pip-api-payload}");
    when(inboundEventProperties.getTopicName()).thenReturn("pipcs-api-request");
    when(inboundEventProperties.getRoutingKeyRegistrationResponse())
        .thenReturn("registration-response-routing-key");
    when(clock.instant()).thenReturn(NOW);

    registrationSubmitter.submitRegistrationData(APPLICATION_ID);

    var order = inOrder(publisher, repository);
    var eventCaptor = ArgumentCaptor.forClass(PipGatewayRequestEventSchemaV1.class);

    order.verify(publisher).publishMessage(eventCaptor.capture());
    assertPublishedEvent(eventCaptor.getValue());
    order.verify(repository).save(application);
    assertApplication(application);
  }

  @Test
  void
      when_registration_status_pending_and_timeout_not_elapsed_then_ProhibitedActionException_thrown() {
    var application = getApplicationFixture();
    application.getPipcsRegistrationState().setCurrent(RegistrationState.PENDING.getLabel());
    when(clock.instant()).thenReturn(NOW);
    application.setAudit(
        Audit.builder().lastModified(Instant.now().minus(9, ChronoUnit.SECONDS)).build());
    application.getState().setCurrent(ApplicationState.HEALTH_AND_DISABILITY.toString());
    when(repository.findById(APPLICATION_ID)).thenReturn(Optional.of(application));
    assertThatThrownBy(() -> registrationSubmitter.submitRegistrationData(APPLICATION_ID))
        .isInstanceOf(ProhibitedActionException.class)
        .hasMessage(
            "Registration data submission is not allowed when registration is pending and time lock active");
  }

  private void assertApplication(Application application) {
    assertAll(
        () -> {
          assertThat(application.getForename()).isEqualTo("Azzzam");
          assertThat(application.getSurname()).isEqualTo("Azzzle");
          assertThat(application.getNino()).isEqualTo("RN000006A");
          assertThat(application.getRegistrationData().getData()).isEqualTo("{good data}");
          assertThat(application.getState().getCurrent())
              .isEqualTo(ApplicationState.HEALTH_AND_DISABILITY.toString());
          assertThat(application.getState().getHistory()).hasSize(2);
          assertThat(application.getAudit().getLastModified()).isEqualTo(NOW);
          assertThat(application.getPipcsRegistrationState().getHistory()).hasSize(1);
          assertThat(application.getPipcsRegistrationState().getCurrent())
              .isEqualTo(RegistrationState.PENDING.getLabel());
          assertThat(application.getDateRegistrationSubmitted()).isEqualTo(LocalDate.now());
          AboutYourHealthSchema100 aboutYourHealth =
              (AboutYourHealthSchema100) application.getHealthDisabilityData().getData();
          assertThat(aboutYourHealth.getHealthProfessionalsDetails()).hasSize(2);
        });
  }

  private void assertPublishedEvent(PipGatewayRequestEventSchemaV1 capturedEvent) {
    assertAll(
        "assert published event",
        () -> {
          assertThat(capturedEvent.getPayload()).isEqualTo("{pip-api-payload}");
          assertThat(capturedEvent.getApplicationId()).isEqualTo(APPLICATION_ID);
          assertThat(capturedEvent.getRespondTopic()).isEqualTo("pipcs-api-request");
          assertThat(capturedEvent.getRespondMessageRoutingKey())
              .isEqualTo("registration-response-routing-key");
        });
  }

  private Application getApplicationFixture() {
    var past = Instant.now().minus(1, ChronoUnit.DAYS);
    var history =
        History.builder().state(ApplicationState.REGISTRATION.toString()).timeStamp(past).build();
    var applicationState = State.builder().build();
    applicationState.addHistory(history);
    var audit = Audit.builder().created(past).lastModified(past).build();
    return Application.builder()
        .id(APPLICATION_ID)
        .registrationData(FormData.builder().data("{good data}").build())
        .pipcsRegistrationState(State.builder().build())
        .state(applicationState)
        .audit(audit)
        .build();
  }

  private RegistrationSchema110 getRegistrationSchemaFixture() {
    var registrationSchema = new RegistrationSchema110();
    var personalDetails = new PersonalDetailsSchema100();
    personalDetails.setFirstname("Azzzam");
    personalDetails.setSurname("Azzzle");
    personalDetails.setNino("RN000006A");
    registrationSchema.setPersonalDetails(personalDetails);

    var aboutYourHealth = new AboutYourHealthSchema100();
    var healthProfessionalsDetails1 = new HealthProfessionalsDetails100();
    var healthProfessionalsDetails2 = new HealthProfessionalsDetails100();
    aboutYourHealth.setHealthProfessionalsDetails(
        List.of(healthProfessionalsDetails1, healthProfessionalsDetails2));
    registrationSchema.setAboutYourHealth(aboutYourHealth);

    return registrationSchema;
  }

  private Pip1RegistrationForm getPip1RegistrationFormFixture() {
    return Pip1RegistrationForm.builder().altFormatRequired(YesNoDontKnow.NO).build();
  }
}
