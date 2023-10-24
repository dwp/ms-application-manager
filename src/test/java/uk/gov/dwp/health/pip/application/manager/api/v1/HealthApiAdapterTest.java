package uk.gov.dwp.health.pip.application.manager.api.v1;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import uk.gov.dwp.health.pip.application.manager.service.HealthDataGetter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class HealthApiAdapterTest {

  @InjectMocks private HealthApiAdapter healthApiAdapter;
  @Mock private HealthDataGetter healthDataGetter;

  @Test
  void when_query_applications_by_state_then_ok() {
    var responseEntity =
        healthApiAdapter.getClaimantsWithState(
            0, 0, "REGISTRATION", "2001-01-01T00:00:00.0", "2001-01-01T00:00:00.0");
    verify(healthDataGetter, times(1))
        .getHealthDataByStateAndTimestamp(anyInt(), anyInt(), anyString(), any(), any());
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
  }
}
