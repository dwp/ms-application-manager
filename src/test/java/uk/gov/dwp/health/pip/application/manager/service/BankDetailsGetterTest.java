package uk.gov.dwp.health.pip.application.manager.service;

import org.junit.jupiter.api.*;
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
import uk.gov.dwp.health.pip.application.manager.model.registration.data.BankDetails110;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.PersonalDetailsSchema120;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.RegistrationSchema140;
import uk.gov.dwp.health.pip.application.manager.openapi.bankdetails.v1.dto.AccountDetails;
import uk.gov.dwp.health.pip.application.manager.openapi.bankdetails.v1.dto.BankDetailsDto;
import uk.gov.dwp.health.pip.application.manager.repository.ApplicationRepository;
import uk.gov.dwp.health.pip.application.manager.service.mapper.BankDetailsMapperV1;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class BankDetailsGetterTest {
  @Mock private ApplicationRepository repository;
  @Mock RegistrationDataMarshaller registrationDataMarshaller;
  @Mock BankDetailsMapperV1 bankDetailsMapperV1;
  @InjectMocks private BankDetailsGetter bankDetailsGetter;

  @Nested
  class GetBankDetailsByApplicationIdTest {

    @Test
    void when_application_exists_bank_details_are_returned() {
      var submittedApplication = getSubmittedApplication("application-id-1");
      BankDetails110 bankDetails = new BankDetails110();
      bankDetails.setAdditionalProperty("sort_code", "000000");
      bankDetails.setAdditionalProperty("account_number", "12345678");
      bankDetails.setAdditionalProperty("roll_number", "123456789");
      PersonalDetailsSchema120 personalDetails = new PersonalDetailsSchema120();
      personalDetails.setBankDetails(bankDetails);
      RegistrationSchema140 registrationData = new RegistrationSchema140();
      registrationData.setPersonalDetails(personalDetails);

      AccountDetails accountDetails = new AccountDetails()
              .accountNumber("12345678")
              .rollNumber("123456789")
              .sortCode("000000");

      BankDetailsDto expectedBankDetailsDto = new BankDetailsDto()
              .name("firstname surname")
              .accountDetails(accountDetails);

      when(registrationDataMarshaller.marshallRegistrationData(submittedApplication.getRegistrationData().getData())).thenReturn(registrationData);
      when(bankDetailsMapperV1.toDto(registrationData)).thenReturn(expectedBankDetailsDto);
      when(repository.findById("application-id-1")).thenReturn(Optional.of(submittedApplication));

      BankDetailsDto actualBankDetailsDto =
          bankDetailsGetter.getBankDetailsByApplicationId("application-id-1");

      assertThat(actualBankDetailsDto.getAccountDetails().getAccountNumber()).isEqualTo("12345678");
      assertThat(actualBankDetailsDto.getName()).isEqualTo("firstname surname");
      assertThat(actualBankDetailsDto.getAccountDetails().getSortCode()).isEqualTo("000000");
      assertThat(actualBankDetailsDto.getAccountDetails().getRollNumber()).isEqualTo("123456789");
    }

    @Test
    void when_application_doesnt_exist_exception_thrown() {
      when(repository.findById("application-id-1")).thenReturn(Optional.empty());
      assertThatThrownBy(
              () -> bankDetailsGetter.getBankDetailsByApplicationId("application-id-1"))
          .isInstanceOf(ApplicationNotFoundException.class)
          .hasMessageContaining("No registration data found for application id: application-id-1");
    }
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
    formData.setData("{\"bankDetails\": {\"sort_code\": \"000000\", \"account_number\": \"12345678\", \"roll_number\": \"123456789\" }}");
    formData.setMeta("meta");
    formData.setType(FormType.REGISTRATION);
    application.setRegistrationData(formData);
    application.setState(state);
    application.setPipcsRegistrationState(legacyRegistrationState);
    application.setDateRegistrationSubmitted(LocalDate.of(2022, Month.MARCH, 27));
    return application;
  }
}
