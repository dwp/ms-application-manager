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
import org.springframework.http.ResponseEntity;
import uk.gov.dwp.health.pip.application.manager.openapi.healthdisability.v1.dto.FormDataDto;
import uk.gov.dwp.health.pip.application.manager.openapi.healthdisability.v1.dto.HealthDisabilityDto;
import uk.gov.dwp.health.pip.application.manager.service.HealthDataGetter;
import uk.gov.dwp.health.pip.application.manager.service.HealthDataSubmitter;
import uk.gov.dwp.health.pip.application.manager.service.HealthDataUpdater;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class HealthApiAdapterTest {

  @InjectMocks private HealthApiAdapter healthApiAdapter;
  @Mock private HealthDataGetter healthDataGetter;
  @Mock private HealthDataUpdater healthDataUpdater;
  @Mock private HealthDataSubmitter healthDataSubmitter;

  @Test
  void when_get_healthdisability_data_then_ok() {
    var healthDisabilityDto = new HealthDisabilityDto();
    healthDisabilityDto.applicationId("application-id-1");

    when(healthDataGetter.getHealthData("claimant-id-1")).thenReturn(healthDisabilityDto);

    ResponseEntity<HealthDisabilityDto> responseEntity =
        healthApiAdapter.getHealthDisabilityData("claimant-id-1");

    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseEntity.getBody()).isNotNull();
    assertThat(responseEntity.getBody().getApplicationId()).isEqualTo("application-id-1");
  }

  @Test
  void when_getting_healthdisability_data_by_application_id_then_ok() {
    var healthDisabilityDto = new HealthDisabilityDto();
    when(healthDataGetter.getHealthDataByApplicationId("application-id-1"))
        .thenReturn(healthDisabilityDto);

    var response = healthApiAdapter.getHealthDisabilityDataByApplicationId("application-id-1");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo(healthDisabilityDto);
  }

  @Test
  void when_update_healthdisability_data_then_ok() {
    var formDataDto = new FormDataDto();
    var responseEntity =
        healthApiAdapter.updateHealthDisabilityData("application-id-1", formDataDto);
    verify(healthDataUpdater, times(1)).updateHealthData("application-id-1", formDataDto);
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void when_submit_healthdisability_data_then_ok() {
    var formDataDto = new FormDataDto();
    var responseEntity =
        healthApiAdapter.submitHealthDisabilityData(
            "application-id-1", "submission-id-1", formDataDto);
    verify(healthDataSubmitter, times(1))
        .submitHealthData("application-id-1", "submission-id-1", formDataDto);
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
  }

  @Test
  void when_query_applications_by_state_then_ok() {
    var responseEntity = healthApiAdapter.getClaimantsWithState(0, 0, "REGISTRATION", "2001-01-01T00:00:00.0", "2001-01-01T00:00:00.0");
    verify(healthDataGetter, times(1))
        .getHealthDataByStateAndTimestamp(anyInt(), anyInt(), anyString(), any(), any());
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

}
