package uk.gov.dwp.health.pip.application.manager.responsemodels;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.dwp.health.pip.application.manager.requestmodels.bankdetails.BankDetails;

@Getter
@Builder(toBuilder = true)
public class BankDetailsDTO {
    @Default private String name = "John Doe";
    @JsonProperty("accountDetails")
    @Default private BankDetails bankDetails = BankDetails.builder().build();
}
