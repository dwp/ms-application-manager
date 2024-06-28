package uk.gov.dwp.health.pip.application.manager.service.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.dwp.health.pip.application.manager.exception.ApplicationNotFoundException;
import uk.gov.dwp.health.pip.application.manager.exception.RegistrationDataNotValid;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.BankDetails110;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.RegistrationSchema140;
import uk.gov.dwp.health.pip.application.manager.openapi.bankdetails.v1.dto.AccountDetails;
import uk.gov.dwp.health.pip.application.manager.openapi.bankdetails.v1.dto.BankDetailsDto;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class BankDetailsMapperV1 {
  public BankDetailsDto toDto(RegistrationSchema140 registrationSchema) {
    BankDetails110 bankDetails = registrationSchema.getPersonalDetails().getBankDetails();
    try {
      AccountDetails accountDetails = new AccountDetails()
              .accountNumber(bankDetails.getAdditionalProperties().get("accountNumber").toString())
              .sortCode(bankDetails.getAdditionalProperties().get("sortCode").toString())
              .rollNumber(Objects.toString(
                      bankDetails.getAdditionalProperties().get("rollNumber"), null));

      return new BankDetailsDto()
              .name(bankDetails.getAdditionalProperties().get("accountName").toString())
              .accountDetails(accountDetails);
    } catch (Exception exception) {
      throw new RegistrationDataNotValid("Bank details are invalid");
    }
  }
}
