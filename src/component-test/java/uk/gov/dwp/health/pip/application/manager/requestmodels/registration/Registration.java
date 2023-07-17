package uk.gov.dwp.health.pip.application.manager.requestmodels.registration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class Registration {
  @Default
  @JsonProperty("claimant_id")
  private final String claimantId = "6ed1d430716609122be7a4d6";

  @Default
  @JsonProperty("benefit_type")
  private final String benefitType = "PIP";

  @Default private final String language = "EN";
}
