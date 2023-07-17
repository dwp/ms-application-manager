package uk.gov.dwp.health.pip.application.manager.responsemodels;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Data
@NoArgsConstructor
public class Application {
  @JsonProperty("application_id")
  private String applicationId;

  @JsonProperty("form_data")
  private Object formData;

  private Object meta;

  @JsonProperty("application_status")
  private String applicationStatus;

  @JsonProperty("submission_date")
  private String submissionDate;
}




