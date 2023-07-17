package uk.gov.dwp.health.pip.application.manager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.dwp.health.pip.application.manager.constant.ApplicationState;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.entity.Audit;
import uk.gov.dwp.health.pip.application.manager.entity.FormData;
import uk.gov.dwp.health.pip.application.manager.entity.State;
import uk.gov.dwp.health.pip.application.manager.exception.ApplicationNotFoundException;
import uk.gov.dwp.health.pip.application.manager.exception.ProhibitedActionException;
import uk.gov.dwp.health.pip.application.manager.messaging.WorkflowMessagePublisher;
import uk.gov.dwp.health.pip.application.manager.openapi.healthdisability.v1.dto.FormDataDto;
import uk.gov.dwp.health.pip.application.manager.repository.ApplicationRepository;
import uk.gov.dwp.health.pip2.common.Pip2HealthDisabilityForm;
import uk.gov.dwp.health.pip2.common.model.about.Details;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static support.FileUtils.readTestFileAsObject;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class HealthDataSubmitterTest {

  @InjectMocks private HealthDataSubmitter healthDataSubmitter;
  @Mock private ApplicationRepository applicationRepository;
  @Mock private HealthDataMarshaller healthDataMarshaller;
  @Mock private WorkflowMessagePublisher workflowMessagePublisher;
  @Mock private Clock clock;

  @Nested
  class WhenApplicationExistsTest {

    private Application existingApplication;
    private FormDataDto formDataDto;
    private Instant now;
    private Object formData;
    private Pip2HealthDisabilityForm pip2HealthDisabilityForm;
    private SimpleDateFormat simpleDateFormat;

    @BeforeEach
    void beforeEach() throws IOException, ParseException {
      existingApplication = getExistingApplication();
      formData = readTestFileAsObject("health-and-disability/validHealthData.json");
      formDataDto = new FormDataDto().formData(formData);
      now = Instant.now();
      simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
      pip2HealthDisabilityForm = getPip2HealthDisabilityForm(simpleDateFormat);
    }

    @Test
    void when_registration_name_nino_available_then_use_these_and_submit() throws ParseException {
      when(applicationRepository.findById("application-id-1"))
          .thenReturn(Optional.ofNullable(existingApplication));
      when(healthDataMarshaller.marshallHealthData(formData)).thenReturn(pip2HealthDisabilityForm);
      when(clock.instant()).thenReturn(now);
      doNothing()
          .when(workflowMessagePublisher)
          .publishMessage(
              "application-id-1", "Azzzam Azzzle", "RN000006A", simpleDateFormat.parse("2023-01-13"));

      healthDataSubmitter.submitHealthData("application-id-1", "submission-id-1", formDataDto);

      var updatedApplication = captureApplication();
      verifyApplication(updatedApplication);
    }

    @Test
    void when_registration_forename_not_available_then_use_health_name_nino_and_submit()
        throws ParseException {
      when(applicationRepository.findById("application-id-1"))
          .thenReturn(Optional.ofNullable(existingApplication.toBuilder().forename(null).build()));
      when(healthDataMarshaller.marshallHealthData(formData)).thenReturn(pip2HealthDisabilityForm);
      when(clock.instant()).thenReturn(now);
      doNothing()
          .when(workflowMessagePublisher)
          .publishMessage(
              "application-id-1",
              "claimant_first_name claimant_last_name",
              "RN000003A",
              simpleDateFormat.parse("2020-11-11"));

      healthDataSubmitter.submitHealthData("application-id-1", "submission-id-1", formDataDto);

      var updatedApplication = captureApplication();
      verifyApplication(updatedApplication);
    }

    @Test
    void when_registration_surname_not_available_then_use_health_name_nino_and_submit()
        throws ParseException {
      when(applicationRepository.findById("application-id-1"))
          .thenReturn(Optional.ofNullable(existingApplication.toBuilder().surname(null).build()));
      when(healthDataMarshaller.marshallHealthData(formData)).thenReturn(pip2HealthDisabilityForm);
      when(clock.instant()).thenReturn(now);
      doNothing()
          .when(workflowMessagePublisher)
          .publishMessage(
              "application-id-1",
              "claimant_first_name claimant_last_name",
              "RN000003A",
              simpleDateFormat.parse("2020-11-11"));

      healthDataSubmitter.submitHealthData("application-id-1", "submission-id-1", formDataDto);

      var updatedApplication = captureApplication();
      verifyApplication(updatedApplication);
    }

    @Test
    void when_registration_nino_not_available_then_use_health_name_nino_and_submit()
        throws ParseException {
      when(applicationRepository.findById("application-id-1"))
          .thenReturn(Optional.ofNullable(existingApplication.toBuilder().nino(null).build()));
      when(healthDataMarshaller.marshallHealthData(formData)).thenReturn(pip2HealthDisabilityForm);
      when(clock.instant()).thenReturn(now);
      doNothing()
          .when(workflowMessagePublisher)
          .publishMessage(
              "application-id-1",
              "claimant_first_name claimant_last_name",
              "RN000003A",
              simpleDateFormat.parse("2020-11-11"));

      healthDataSubmitter.submitHealthData("application-id-1", "submission-id-1", formDataDto);

      var updatedApplication = captureApplication();
      verifyApplication(updatedApplication);
    }

    private Pip2HealthDisabilityForm getPip2HealthDisabilityForm(SimpleDateFormat simpleDateFormat)
        throws ParseException {
      return Pip2HealthDisabilityForm.builder()
          .details(
              Details.builder()
                  .nino("RN000003A")
                  .title("title")
                  .forename("claimant_first_name")
                  .surname("claimant_last_name")
                  .dob(simpleDateFormat.parse("2020-11-11"))
                  .postcode("postcode")
                  .build())
          .submissionDate(simpleDateFormat.parse("2020-11-11"))
          .build();
    }

    private Application getExistingApplication() {
      return Application.builder()
          .id("application-id-1")
          .forename("Azzzam")
          .surname("Azzzle")
          .nino("RN000006A")
          .state(State.builder().current(ApplicationState.HEALTH_AND_DISABILITY.name()).build())
          .healthDisabilityData(FormData.builder().data("form data").build())
          .audit(Audit.builder().build())
          .dateRegistrationSubmitted(LocalDate.of(2023, 1, 13))
          .build();
    }

    private Application captureApplication() {
      var applicationArgumentCaptor = ArgumentCaptor.forClass(Application.class);
      verify(applicationRepository, times(1)).save(applicationArgumentCaptor.capture());
      return applicationArgumentCaptor.getValue();
    }

    private void verifyApplication(Application application) {
      assertThat(application.getState().getCurrent()).isEqualTo(ApplicationState.SUBMITTED.name());
      assertThat(application.getState().getHistory().get(0).getState())
          .isEqualTo(ApplicationState.SUBMITTED.name());
      assertThat(application.getState().getHistory().get(0).getTimeStamp()).isEqualTo(now);
      assertThat(application.getAudit().getLastModified()).isEqualTo(now);
      assertThat(application.getHealthDisabilityData().getData()).isEqualTo(formData);
      assertThat(application.getSubmissionId()).isEqualTo("submission-id-1");
    }
  }

  @Test
  void when_application_doesnt_exist_then_not_found() {
    var formDataDto = new FormDataDto();
    when(applicationRepository.findById("application-id-1")).thenReturn(Optional.empty());
    assertThatThrownBy(
            () ->
                healthDataSubmitter.submitHealthData(
                    "application-id-1", "submission-id-1", formDataDto))
        .isInstanceOf(ApplicationNotFoundException.class)
        .hasMessage("No application found for provided Application Id");
  }

  @Nested
  class WhenNotEligibleForSubmissionThenProhibitedActionTest {

    @Test
    void when_submitted() {
      when(applicationRepository.findById("application-id-1"))
          .thenReturn(
              Optional.ofNullable(
                  Application.builder()
                      .state(State.builder().current(ApplicationState.SUBMITTED.name()).build())
                      .build()));
      verifyExceptionThrown();
    }

    @Test
    void when_health_data_null() {
      when(applicationRepository.findById("application-id-1"))
          .thenReturn(
              Optional.ofNullable(
                  Application.builder()
                      .state(
                          State.builder()
                              .current(ApplicationState.HEALTH_AND_DISABILITY.name())
                              .build())
                      .build()));
      verifyExceptionThrown();
    }

    @Test
    void when_form_data_null() {
      when(applicationRepository.findById("application-id-1"))
          .thenReturn(
              Optional.ofNullable(
                  Application.builder()
                      .state(
                          State.builder()
                              .current(ApplicationState.HEALTH_AND_DISABILITY.name())
                              .build())
                      .healthDisabilityData(FormData.builder().build())
                      .build()));
      verifyExceptionThrown();
    }

    private void verifyExceptionThrown() {
      var formDataDto = new FormDataDto();
      assertThatThrownBy(
              () ->
                  healthDataSubmitter.submitHealthData(
                      "application-id-1", "submission-id-1", formDataDto))
          .isInstanceOf(ProhibitedActionException.class)
          .hasMessage("Health and disability data submission is not allowed");
    }
  }
}
