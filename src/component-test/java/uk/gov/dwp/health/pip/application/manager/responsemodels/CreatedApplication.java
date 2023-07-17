package uk.gov.dwp.health.pip.application.manager.responsemodels;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Data
@NoArgsConstructor
public class CreatedApplication {
  @JsonProperty("application_id")
  private String applicationId;

  @JsonProperty("application_status")
  private String applicationStatus;
}
