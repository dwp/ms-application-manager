package uk.gov.dwp.health.pip.application.manager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.entity.FormData;
import uk.gov.dwp.health.pip.application.manager.entity.State;
import uk.gov.dwp.health.pip.application.manager.exception.ApplicationNotFoundException;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.AddressSchema100;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.PersonalDetailsSchema120;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.RegistrationSchema140;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v1.dto.ApplicationStatusDto;
import uk.gov.dwp.health.pip.application.manager.repository.ApplicationRepository;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class ApplicationStatusGetterTest {

  private static final String CLAIMANT_ID = UUID.randomUUID().toString();
  private static final String APPLICATION_ID = UUID.randomUUID().toString();

  @InjectMocks private ApplicationStatusGetter applicationStatusGetter;
  @Mock private ApplicationRepository repository;
  @Mock private RegistrationDataMarshaller registrationDataMarshaller;
  @Mock private ApplicationCoordinatorService applicationCoordinatorService;
  @Captor private ArgumentCaptor<String> stringArgumentCaptor;

  @Test
  void should_throw_application_not_found_exception_when_application_not_exist() {
    when(repository.findAllByClaimantId(anyString())).thenReturn(Collections.emptyList());
    assertThatThrownBy(() -> applicationStatusGetter.getApplicationStatusByClaimantId(CLAIMANT_ID))
        .isInstanceOf(ApplicationNotFoundException.class)
        .hasMessage(String.format("No application found for given claimant id %s", CLAIMANT_ID));
    verify(repository).findAllByClaimantId(stringArgumentCaptor.capture());
    assertThat(stringArgumentCaptor.getValue()).isEqualTo(CLAIMANT_ID);
  }

  @Test
  void when_registration_not_submitted() {
    Application application = new Application();
    String applicationId = "application-id-1";

    State state = new State();
    state.setCurrent("REGISTRATION");
    application.setState(state);
    application.setId(applicationId);

    when(repository.findAllByClaimantId("claimant-id-1"))
        .thenReturn(Collections.singletonList(application));
    when(applicationCoordinatorService.getApplicationState(applicationId))
        .thenReturn(state);

    ApplicationStatusDto applicationStatusDto =
        applicationStatusGetter.getApplicationStatusByClaimantId("claimant-id-1");

    assertThat(applicationStatusDto.getApplicationStatus().getValue()).isEqualTo(state.getCurrent());
    assertThat(applicationStatusDto.getApplicationId()).isEqualTo(applicationId);
    assertThat(applicationStatusDto.getSubmissionId()).isNull();
    assertThat(applicationStatusDto.getSurname()).isNull();
    assertThat(applicationStatusDto.getForename()).isNull();
    assertThat(applicationStatusDto.getDateOfBirth()).isNull();
    assertThat(applicationStatusDto.getNationalInsuranceNumber()).isNull();
    assertThat(applicationStatusDto.getPostcode()).isNull();
  }

  @Test
  void when_healthdisability_not_submitted() {

    String applicationId = "application-id-1";
    State state = State.builder().current("HEALTH_AND_DISABILITY").build();

    Application application =
        Application.builder()
            .id(applicationId)
            .state(state)
            .registrationData(FormData.builder().data("registration-data").build())
            .build();

    when(repository.findAllByClaimantId("claimant-id-1"))
        .thenReturn(Collections.singletonList(application));
    when(registrationDataMarshaller.marshallRegistrationData("registration-data"))
        .thenReturn(getRegistrationSchemaFixture());
    when(applicationCoordinatorService.getApplicationState(applicationId)).thenReturn(state);
    ApplicationStatusDto applicationStatusDto =
        applicationStatusGetter.getApplicationStatusByClaimantId("claimant-id-1");

    assertThat(applicationStatusDto.getApplicationStatus().getValue())
        .isEqualTo(state.getCurrent());
    assertThat(applicationStatusDto.getApplicationId()).isEqualTo(applicationId);
    assertThat(applicationStatusDto.getSubmissionId()).isNull();
    assertThat(applicationStatusDto.getSurname()).isEqualTo("surname-1");
    assertThat(applicationStatusDto.getForename()).isEqualTo("forename-1");
    assertThat(applicationStatusDto.getDateOfBirth()).isEqualTo("date-of-birth-1");
    assertThat(applicationStatusDto.getNationalInsuranceNumber()).isEqualTo("nino-1");
    assertThat(applicationStatusDto.getPostcode()).isEqualTo("postcode-1");
  }

  @Test
  void when_application_submitted() {

    State state = State.builder().current("SUBMITTED").build();
    String applicationId = "application-id-1";

    Application application =
        Application.builder()
            .id(applicationId)
            .state(state)
            .registrationData(FormData.builder().data("registration-data").build())
            .submissionId("submission-id-1")
            .build();

    when(applicationCoordinatorService.getApplicationState(applicationId)).thenReturn(state);
    when(repository.findAllByClaimantId(anyString()))
        .thenReturn(Collections.singletonList(application));
    when(registrationDataMarshaller.marshallRegistrationData("registration-data"))
        .thenReturn(getRegistrationSchemaFixture());

    var applicationStatusDto =
        applicationStatusGetter.getApplicationStatusByClaimantId("claimant-id-1");

    assertThat(applicationStatusDto.getApplicationStatus().getValue())
        .isEqualTo(state.getCurrent());
    assertThat(applicationStatusDto.getApplicationId()).isEqualTo(applicationId);
    assertThat(applicationStatusDto.getSubmissionId()).isEqualTo("submission-id-1");
    assertThat(applicationStatusDto.getSurname()).isEqualTo("surname-1");
    assertThat(applicationStatusDto.getForename()).isEqualTo("forename-1");
    assertThat(applicationStatusDto.getDateOfBirth()).isEqualTo("date-of-birth-1");
    assertThat(applicationStatusDto.getNationalInsuranceNumber()).isEqualTo("nino-1");
    assertThat(applicationStatusDto.getPostcode()).isEqualTo("postcode-1");
  }

  private RegistrationSchema140 getRegistrationSchemaFixture() {
    var addressSchema = new AddressSchema100();
    addressSchema.setPostcode("postcode-1");

    var personalDetails = new PersonalDetailsSchema120();
    personalDetails.setSurname("surname-1");
    personalDetails.setFirstname("forename-1");
    personalDetails.setDob("date-of-birth-1");
    personalDetails.setNino("nino-1");
    personalDetails.setAddress(addressSchema);

    var registrationSchema = new RegistrationSchema140();
    registrationSchema.setPersonalDetails(personalDetails);

    return registrationSchema;
  }

  @Test
  void should_throw_application_not_found_exception_when_application_id_not_exist() {
    when(repository.findById(anyString())).thenReturn(Optional.empty());
    assertThatThrownBy(() -> applicationStatusGetter.getClaimantIdAndStatus(APPLICATION_ID))
        .isInstanceOf(ApplicationNotFoundException.class)
        .hasMessage(
            String.format("No application found for given application id %s", APPLICATION_ID));
    verify(repository).findById(stringArgumentCaptor.capture());
    assertThat(stringArgumentCaptor.getValue()).isEqualTo(APPLICATION_ID);
  }

  @Test
  void when_application_exists_for_claimant_id_and_status_request() {
    State state = State.builder().current("SUBMITTED").build();
    String applicationId = "application-id-1";

    var application =
        Application.builder().id(applicationId).claimantId("claimant-id-1").state(state).build();

    when(repository.findById(anyString())).thenReturn(Optional.of(application));

    when(applicationCoordinatorService.getApplicationState(applicationId)).thenReturn(state);

    var claimantStatusDto = applicationStatusGetter.getClaimantIdAndStatus(applicationId);

    assertThat(claimantStatusDto.getApplicationStatus().getValue()).isEqualTo(state.getCurrent());
    assertThat(claimantStatusDto.getClaimantId()).isEqualTo("claimant-id-1");
  }
}
