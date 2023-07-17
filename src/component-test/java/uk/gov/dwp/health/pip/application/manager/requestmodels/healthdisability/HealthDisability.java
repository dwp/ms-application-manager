package uk.gov.dwp.health.pip.application.manager.requestmodels.healthdisability;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import uk.gov.dwp.health.pip.application.manager.requestmodels.Meta;

@Getter
@Builder(toBuilder = true)
public class HealthDisability {
  @Default
  @JsonProperty("claimant_id")
  private final String claimantId = "6ed1d430716609122be7a4d6";

  @Default
  @JsonProperty("benefit_type")
  private final String benefitType = "PIP";

  @JsonProperty("form_data")
  @Default private final FormData formData = FormData.builder().build();

  @Default private final Meta meta = Meta.builder().build();

  @Default private final String language = "EN";
}
