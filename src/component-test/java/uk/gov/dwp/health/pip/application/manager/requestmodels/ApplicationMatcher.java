package uk.gov.dwp.health.pip.application.manager.requestmodels;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class ApplicationMatcher {

  @JsonProperty("nino")
  private final String nino;
}
