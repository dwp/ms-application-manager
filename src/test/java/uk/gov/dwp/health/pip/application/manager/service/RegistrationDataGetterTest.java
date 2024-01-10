package uk.gov.dwp.health.pip.application.manager.service;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.entity.FormData;
import uk.gov.dwp.health.pip.application.manager.entity.History;
import uk.gov.dwp.health.pip.application.manager.entity.State;
import uk.gov.dwp.health.pip.application.manager.entity.enums.FormType;
import uk.gov.dwp.health.pip.application.manager.exception.ApplicationNotFoundException;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v1.dto.RegistrationDto;
import uk.gov.dwp.health.pip.application.manager.repository.ApplicationRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class RegistrationDataGetterTest {

  private static final String CLAIMANT_ID = UUID.randomUUID().toString();

  @InjectMocks private RegistrationDataGetter registrationDataGetter;
  @Mock private ApplicationRepository repository;

  @Nested
  class GetByClaimantIdTest {

    @Test
    void when_application_for_claimant_initial_gather_then_return_registration_data() {
      when(repository.findAllByClaimantId(CLAIMANT_ID))
          .thenReturn(Collections.singletonList(getUnsubmittedApplication("application-id-1")));
      var registrationDto = registrationDataGetter.getRegistrationDataByClaimantId(CLAIMANT_ID);
      assertAll(
          "Form data dto",
          () -> {
            assertThat(registrationDto.getApplicationId()).isEqualTo("application-id-1");
            assertThat(registrationDto.getFormData()).isEqualTo("{REGISTRATION_FORM_DATA}");
            assertThat(registrationDto.getMeta()).isEqualTo("meta");
            assertThat(registrationDto.getApplicationStatus())
                .isEqualTo(RegistrationDto.ApplicationStatusEnum.REGISTRATION);
            assertThat(registrationDto.getSubmissionDate()).isEmpty();
          });
    }

    @Test
    void when_application_for_claimant_main_gather_then_application_not_found() {
      when(repository.findAllByClaimantId("claimant-id"))
          .thenReturn(List.of(getSubmittedApplication("application-id")));
      assertThatThrownBy(
              () -> registrationDataGetter.getRegistrationDataByClaimantId("claimant-id"))
          .isInstanceOf(ApplicationNotFoundException.class)
          .hasMessageContaining("No registration data found for provided Claimant ID");
    }

    @Test
    void when_no_applications_for_claimant_then_application_not_found() {
      when(repository.findAllByClaimantId(CLAIMANT_ID)).thenReturn(Collections.emptyList());
      assertThatThrownBy(() -> registrationDataGetter.getRegistrationDataByClaimantId(CLAIMANT_ID))
          .isInstanceOf(ApplicationNotFoundException.class)
          .hasMessageContaining("No registration data found for provided Claimant ID");
    }

    @Test
    void when_multiple_registration_data_for_claimant_then_illegal_state() {
      when(repository.findAllByClaimantId(CLAIMANT_ID))
          .thenReturn(
              List.of(
                  getUnsubmittedApplication(UUID.randomUUID().toString()),
                  getUnsubmittedApplication(UUID.randomUUID().toString())));
      assertThatThrownBy(() -> registrationDataGetter.getRegistrationDataByClaimantId(CLAIMANT_ID))
          .isInstanceOf(IllegalStateException.class)
          .hasMessageContaining("ERROR: multiple registration data found for claimant");
    }

    @Test
    void when_multiple_active_applications_for_claimant_then_return_registration_data() {
      when(repository.findAllByClaimantId(CLAIMANT_ID))
          .thenReturn(
              List.of(
                  getUnsubmittedApplication("application-id-1"),
                  getSubmittedApplication("application-id-2")));
      var registrationDto = registrationDataGetter.getRegistrationDataByClaimantId(CLAIMANT_ID);
      assertAll(
          "Form data dto",
          () -> {
            assertThat(registrationDto.getApplicationId()).isEqualTo("application-id-1");
            assertThat(registrationDto.getFormData()).isEqualTo("{REGISTRATION_FORM_DATA}");
            assertThat(registrationDto.getMeta()).isEqualTo("meta");
            assertThat(registrationDto.getApplicationStatus())
                .isEqualTo(RegistrationDto.ApplicationStatusEnum.REGISTRATION);
            assertThat(registrationDto.getSubmissionDate()).isEmpty();
          });
    }
  }

  @Nested
  class GetByApplicationIdTest {

    @Test
    void when_application_exists() {
      var submittedApplication = getSubmittedApplication("application-id-1");

      when(repository.findById("application-id-1")).thenReturn(Optional.of(submittedApplication));

      var registrationDto =
          registrationDataGetter.getRegistrationDataByApplicationId("application-id-1");

      assertThat(registrationDto.getApplicationId()).isEqualTo("application-id-1");
      assertThat(registrationDto.getFormData()).isEqualTo("{REGISTRATION_FORM_DATA}");
      assertThat(registrationDto.getMeta()).isEqualTo("meta");
      assertThat(registrationDto.getApplicationStatus())
          .isEqualTo(RegistrationDto.ApplicationStatusEnum.HEALTH_AND_DISABILITY);
      assertThat(registrationDto.getSubmissionDate()).isEqualTo("2022-03-27");
    }

    @Test
    void when_application_doesnt_exist() {
      when(repository.findById("application-id-1")).thenReturn(Optional.empty());
      assertThatThrownBy(
              () -> registrationDataGetter.getRegistrationDataByApplicationId("application-id-1"))
          .isInstanceOf(ApplicationNotFoundException.class)
          .hasMessageContaining("No registration data found for provided application id");
    }
  }

  private Application getUnsubmittedApplication(String id) {
    var application = new Application();
    application.setId(id);
    var state = State.builder().build();
    state.addHistory(History.builder().timeStamp(Instant.now()).state("REGISTRATION").build());
    var formData = new FormData();
    formData.setData("{REGISTRATION_FORM_DATA}");
    formData.setMeta("meta");
    formData.setType(FormType.REGISTRATION);
    application.setRegistrationData(formData);
    application.setState(state);
    return application;
  }

  private Application getSubmittedApplication(String id) {
    var application = new Application();
    application.setId(id);
    var state = State.builder().build();
    state.addHistory(History.builder().timeStamp(Instant.now()).state("REGISTRATION").build());
    state.addHistory(
        History.builder().timeStamp(Instant.now()).state("HEALTH_AND_DISABILITY").build());
    var legacyRegistrationState = State.builder().build();
    legacyRegistrationState.addHistory(
        History.builder().timeStamp(Instant.now()).state("PENDING").build());
    var formData = new FormData();
    formData.setData("{REGISTRATION_FORM_DATA}");
    formData.setMeta("meta");
    formData.setType(FormType.REGISTRATION);
    application.setRegistrationData(formData);
    application.setState(state);
    application.setPipcsRegistrationState(legacyRegistrationState);
    application.setDateRegistrationSubmitted(LocalDate.of(2022, Month.MARCH, 27));
    return application;
  }
}
