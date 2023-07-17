package uk.gov.dwp.health.pip.application.manager.service;

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
import uk.gov.dwp.health.pip.application.manager.model.registration.data.PersonalDetails;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.RegistrationSchema100;
import uk.gov.dwp.health.pip.application.manager.repository.ApplicationRepository;

import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class ApplicationStatusGetterTest {

  private static final String CLAIMANT_ID = UUID.randomUUID().toString();

  @InjectMocks private ApplicationStatusGetter applicationStatusGetter;
  @Mock private ApplicationRepository repository;
  @Mock private RegistrationDataMarshaller registrationDataMarshaller;
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
    var application = new Application();
    var state = new State();
    state.setCurrent("REGISTRATION");
    application.setState(state);
    application.setId("application-id-1");

    when(repository.findAllByClaimantId("claimant-id-1"))
        .thenReturn(Collections.singletonList(application));

    var applicationStatusDto =
        applicationStatusGetter.getApplicationStatusByClaimantId("claimant-id-1");

    assertThat(applicationStatusDto.getApplicationStatus().getValue()).isEqualTo("REGISTRATION");
    assertThat(applicationStatusDto.getApplicationId()).isEqualTo("application-id-1");
    assertThat(applicationStatusDto.getSubmissionId()).isNull();
    assertThat(applicationStatusDto.getSurname()).isNull();
    assertThat(applicationStatusDto.getForename()).isNull();
    assertThat(applicationStatusDto.getDateOfBirth()).isNull();
    assertThat(applicationStatusDto.getNationalInsuranceNumber()).isNull();
    assertThat(applicationStatusDto.getPostcode()).isNull();
  }

  @Test
  void when_healthdisability_not_submitted() {
    var application =
        Application.builder()
            .id("application-id-1")
            .state(State.builder().current("HEALTH_AND_DISABILITY").build())
            .registrationData(FormData.builder().data("registration-data").build())
            .build();

    when(repository.findAllByClaimantId("claimant-id-1"))
        .thenReturn(Collections.singletonList(application));
    when(registrationDataMarshaller.marshallRegistrationData("registration-data"))
        .thenReturn(getRegistrationSchemaFixture());

    var applicationStatusDto =
        applicationStatusGetter.getApplicationStatusByClaimantId("claimant-id-1");

    assertThat(applicationStatusDto.getApplicationStatus().getValue())
        .isEqualTo("HEALTH_AND_DISABILITY");
    assertThat(applicationStatusDto.getApplicationId()).isEqualTo("application-id-1");
    assertThat(applicationStatusDto.getSubmissionId()).isNull();
    assertThat(applicationStatusDto.getSurname()).isEqualTo("surname-1");
    assertThat(applicationStatusDto.getForename()).isEqualTo("forename-1");
    assertThat(applicationStatusDto.getDateOfBirth()).isEqualTo("date-of-birth-1");
    assertThat(applicationStatusDto.getNationalInsuranceNumber()).isEqualTo("nino-1");
    assertThat(applicationStatusDto.getPostcode()).isEqualTo("postcode-1");
  }

  @Test
  void when_application_submitted() {
    var application =
        Application.builder()
            .id("application-id-1")
            .state(State.builder().current("SUBMITTED").build())
            .registrationData(FormData.builder().data("registration-data").build())
            .submissionId("submission-id-1")
            .build();

    when(repository.findAllByClaimantId(anyString()))
        .thenReturn(Collections.singletonList(application));
    when(registrationDataMarshaller.marshallRegistrationData("registration-data"))
        .thenReturn(getRegistrationSchemaFixture());

    var applicationStatusDto =
        applicationStatusGetter.getApplicationStatusByClaimantId("claimant-id-1");

    assertThat(applicationStatusDto.getApplicationStatus().getValue()).isEqualTo("SUBMITTED");
    assertThat(applicationStatusDto.getApplicationId()).isEqualTo("application-id-1");
    assertThat(applicationStatusDto.getSubmissionId()).isEqualTo("submission-id-1");
    assertThat(applicationStatusDto.getSurname()).isEqualTo("surname-1");
    assertThat(applicationStatusDto.getForename()).isEqualTo("forename-1");
    assertThat(applicationStatusDto.getDateOfBirth()).isEqualTo("date-of-birth-1");
    assertThat(applicationStatusDto.getNationalInsuranceNumber()).isEqualTo("nino-1");
    assertThat(applicationStatusDto.getPostcode()).isEqualTo("postcode-1");
  }

  private RegistrationSchema100 getRegistrationSchemaFixture() {
    var addressSchema = new AddressSchema100();
    addressSchema.setPostcode("postcode-1");

    var personalDetails = new PersonalDetails();
    personalDetails.setSurname("surname-1");
    personalDetails.setFirstname("forename-1");
    personalDetails.setDob("date-of-birth-1");
    personalDetails.setNino("nino-1");
    personalDetails.setAddress(addressSchema);

    var registrationSchema = new RegistrationSchema100();
    registrationSchema.setPersonalDetails(personalDetails);

    return registrationSchema;
  }
}
