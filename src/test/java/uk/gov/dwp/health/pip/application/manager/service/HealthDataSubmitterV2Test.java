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
import uk.gov.dwp.health.pip.application.manager.repository.ApplicationRepository;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class HealthDataSubmitterV2Test {

  @InjectMocks private HealthDataSubmitterV2 healthDataSubmitterV2;
  @Mock private ApplicationRepository applicationRepository;
  @Mock private Clock clock;

  @Nested
  class WhenApplicationExistsTest {

    private Application existingApplication;
    private Instant now;

    @BeforeEach
    void beforeEach() {
      existingApplication = getExistingApplication();
      now = Instant.now();
    }

    @Test
    void when_application_exists_submit() {
      when(applicationRepository.findById("application-id-1"))
          .thenReturn(Optional.ofNullable(existingApplication));
      when(clock.instant()).thenReturn(now);

      healthDataSubmitterV2.submitHealthData("application-id-1", "submission-id-1");

      var updatedApplication = captureApplication();
      verifyApplication(updatedApplication);
    }

    @Test
    void when_application_doesnt_exist_then_not_found() {
      when(applicationRepository.findById("application-id-1")).thenReturn(Optional.empty());
      assertThatThrownBy(
              () -> healthDataSubmitterV2.submitHealthData("application-id-1", "submission-id-1"))
          .isInstanceOf(ApplicationNotFoundException.class)
          .hasMessage("No application found for provided Application Id");
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
      assertThat(application.getSubmissionId()).isEqualTo("submission-id-1");
    }
  }
}
