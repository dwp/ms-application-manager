package uk.gov.dwp.health.pip.application.manager.responsemodels;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Data
@NoArgsConstructor
public class ApplicationStatus {
  @JsonProperty("application_id")
  private String applicationId;

  @JsonProperty("application_status")
  private String applicationStatus;

  private String surname;

  private String forename;

  @JsonProperty("date_of_birth")
  private String dateOfBirth;

  @JsonProperty("national_insurance_number")
  private String nationalInsuranceNumber;

  private String postcode;

  @JsonProperty("submission_id")
  private String submissionId;
}
