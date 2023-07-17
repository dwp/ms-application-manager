package uk.gov.dwp.health.pip.application.manager.requestmodels.bankdetails;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class BankDetails {
  @Default private String accountNumber = "12345678";
  @Default private  String sortCode = "123456";
  @Default private  String rollNumber = "123456acbd-";
}
