package uk.gov.dwp.health.pip.application.manager.api.v2;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.dwp.health.pip.application.manager.service.HealthDataSubmitterV2;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class HealthApiAdapterV2Test {

  @Mock private HealthDataSubmitterV2 healthDataSubmitterV2;
  @InjectMocks private HealthApiAdapterV2 healthApiAdapterV2;

  @Test
  void when_submitting_health_data_by_application_id_and_submission_id() {
    ResponseEntity<Void> response =
        healthApiAdapterV2.submitHealthDisabilityData("application-id-1", "submission-id-1");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
  }
}
