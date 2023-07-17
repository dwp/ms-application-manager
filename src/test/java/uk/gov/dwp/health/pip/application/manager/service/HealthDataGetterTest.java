package uk.gov.dwp.health.pip.application.manager.service;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.entity.FormData;
import uk.gov.dwp.health.pip.application.manager.entity.State;
import uk.gov.dwp.health.pip.application.manager.exception.ApplicationNotFoundException;
import uk.gov.dwp.health.pip.application.manager.openapi.healthdisability.v1.dto.ClaimantObject;
import uk.gov.dwp.health.pip.application.manager.openapi.healthdisability.v1.dto.HealthDisabilityDto;
import uk.gov.dwp.health.pip.application.manager.repository.ApplicationRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Tag("unit")
class HealthDataGetterTest {

  @InjectMocks private HealthDataGetter healthDataGetter;
  @Mock private ApplicationRepository applicationRepository;

  @Test
  void getHealthDataByStateAndTimestamp() {
    final String state = "state";
    final LocalDateTime fromDate = LocalDateTime.of(2022, 11, 11, 11, 11, 0);
    final LocalDateTime toDate = LocalDateTime.of(2022, 11, 12, 11, 11, 0);
    String id1 = "application-id-1";
    String id2 = "application-id-2";
    String id3 = "application-id-3";
    when(applicationRepository.findAllByStateAndStateTimestampRange(0, 0, state, fromDate, toDate))
        .thenReturn(
            List.of(
                Application.builder()
                    .claimantId(id1)
                    .state(State.builder().current("SUBMITTED").build())
                    .build(),
                Application.builder()
                    .claimantId(id2)
                    .healthDisabilityData(
                        FormData.builder()
                            .data("health and disability data")
                            .meta("meta data")
                            .build())
                    .state(State.builder().current("HEALTH_AND_DISABILITY").build())
                    .build(),
                Application.builder()
                    .claimantId(id3)
                    .state(State.builder().current("REGISTRATION").build())
                    .build()));
    List<ClaimantObject> results =
        healthDataGetter.getHealthDataByStateAndTimestamp(0, 0, state, fromDate, toDate);
    assertThat(results.size() == 3);
    assertThat(results.get(0).getClaimantId().equals(id1));
    assertThat(results.get(1).getClaimantId().equals(id2));
    assertThat(results.get(2).getClaimantId().equals(id3));
  }

  @Nested
  class GetByClaimantIdTest {

    @Test
    void when_health_data_exists_then_found() {
      when(applicationRepository.findAllByClaimantId("claimant-id-1"))
          .thenReturn(
              List.of(
                  Application.builder().state(State.builder().current("SUBMITTED").build()).build(),
                  Application.builder()
                      .id("application-id-1")
                      .healthDisabilityData(
                          FormData.builder()
                              .data("health and disability data")
                              .meta("meta data")
                              .build())
                      .state(State.builder().current("HEALTH_AND_DISABILITY").build())
                      .build(),
                  Application.builder()
                      .state(State.builder().current("REGISTRATION").build())
                      .build()));

      var healthDisabilityDto = healthDataGetter.getHealthData("claimant-id-1");

      assertThat(healthDisabilityDto.getApplicationId()).isEqualTo("application-id-1");
      assertThat(healthDisabilityDto.getFormData()).isEqualTo("health and disability data");
      assertThat(healthDisabilityDto.getMeta()).isEqualTo("meta data");
      assertThat(healthDisabilityDto.getApplicationStatus())
          .isEqualTo(HealthDisabilityDto.ApplicationStatusEnum.HEALTH_AND_DISABILITY);
    }

    @Test
    void when_health_data_doesnt_exist_then_not_found() {
      when(applicationRepository.findAllByClaimantId("claimant-id-1"))
          .thenReturn(Collections.emptyList());

      assertThatThrownBy(() -> healthDataGetter.getHealthData("claimant-id-1"))
          .isInstanceOf(ApplicationNotFoundException.class)
          .hasMessageContaining("No health and disability data found for provided Claimant Id");
    }

    @Test
    void when_multiple_health_data_exists_then_illegal_state() {
      when(applicationRepository.findAllByClaimantId("claimant-id-1"))
          .thenReturn(
              List.of(
                  Application.builder()
                      .state(State.builder().current("HEALTH_AND_DISABILITY").build())
                      .build(),
                  Application.builder()
                      .state(State.builder().current("HEALTH_AND_DISABILITY").build())
                      .build()));

      assertThatThrownBy(() -> healthDataGetter.getHealthData("claimant-id-1"))
          .isInstanceOf(IllegalStateException.class)
          .hasMessageContaining("ERROR: multiple health and disability data found for claimant");
    }
  }

  @Nested
  class GetByApplicationIdTest {

    @Test
    void when_health_data_exists_then_found() {
      when(applicationRepository.findById("application-id-1"))
          .thenReturn(
              Optional.ofNullable(
                  Application.builder()
                      .id("application-id-1")
                      .healthDisabilityData(
                          FormData.builder()
                              .data("health and disability data")
                              .meta("meta data")
                              .build())
                      .state(State.builder().current("HEALTH_AND_DISABILITY").build())
                      .build()));

      var healthDisabilityDto = healthDataGetter.getHealthDataByApplicationId("application-id-1");

      assertThat(healthDisabilityDto.getApplicationId()).isEqualTo("application-id-1");
      assertThat(healthDisabilityDto.getFormData()).isEqualTo("health and disability data");
      assertThat(healthDisabilityDto.getMeta()).isEqualTo("meta data");
      assertThat(healthDisabilityDto.getApplicationStatus())
          .isEqualTo(HealthDisabilityDto.ApplicationStatusEnum.HEALTH_AND_DISABILITY);
    }

    @Test
    void when_health_data_doesnt_exist_then_not_found() {
      when(applicationRepository.findById("application-id-1")).thenReturn(Optional.empty());

      assertThatThrownBy(() -> healthDataGetter.getHealthDataByApplicationId("application-id-1"))
          .isInstanceOf(ApplicationNotFoundException.class)
          .hasMessageContaining("No health and disability data found for provided application id");
    }
  }
}
