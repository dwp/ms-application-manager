package uk.gov.dwp.health.pip.application.manager.service.mapper;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.dwp.health.pip.application.manager.openapi.healthdisability.v2.dto.ConditionDto;
import uk.gov.dwp.health.pip.application.manager.openapi.healthdisability.v2.dto.DailyLivingActivityDto;
import uk.gov.dwp.health.pip.application.manager.openapi.healthdisability.v2.dto.MobilityDto;
import uk.gov.dwp.health.pip2.common.Pip2HealthDisabilityForm;
import uk.gov.dwp.health.pip2.common.model.about.Health;
import uk.gov.dwp.health.pip2.common.model.dla.DailyLiving;
import uk.gov.dwp.health.pip2.common.model.mobility.Mobility;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class HealthDataMapperV2Test {

  @Mock private ConditionsMapperV2 conditionsMapperV2;
  @Mock private DailyLivingActivityMapperV2 dailyLivingActivityMapperV2;
  @Mock private MobilityMapperV2 mobilityMapperV2;
  @InjectMocks private HealthDataMapperV2 healthDataMapperV2;

  @Test
  void when_mapping_health_data_to_dto() {
    var pip2HealthDisabilityForm =
        Pip2HealthDisabilityForm.builder()
            .health(Health.builder().conditions(Collections.emptyList()).build())
            .dailyLiving(DailyLiving.builder().build())
            .mobility(Mobility.builder().build())
            .build();

    List<ConditionDto> conditionDtos = Collections.emptyList();
    var dailyLivingActivityDto = new DailyLivingActivityDto();
    var mobilityDto = new MobilityDto();

    when(conditionsMapperV2.toDtos(pip2HealthDisabilityForm.getHealth().getConditions()))
        .thenReturn(conditionDtos);
    when(dailyLivingActivityMapperV2.toDto(pip2HealthDisabilityForm.getDailyLiving()))
        .thenReturn(dailyLivingActivityDto);

    when(mobilityMapperV2.toDto(pip2HealthDisabilityForm.getMobility())).thenReturn(mobilityDto);

    var healthDisabilityDto =
        healthDataMapperV2.toDto("application-id-1", pip2HealthDisabilityForm);

    assertThat(healthDisabilityDto.getApplicationId()).isEqualTo("application-id-1");
    assertThat(healthDisabilityDto.getConditions()).isEqualTo(conditionDtos);
    assertThat(healthDisabilityDto.getDailyLivingActivity()).isEqualTo(dailyLivingActivityDto);
    assertThat(healthDisabilityDto.getMobility()).isEqualTo(mobilityDto);
  }

  @Test
  void when_health_is_null() {
    var pip2HealthDisabilityForm = Pip2HealthDisabilityForm.builder().build();

    var healthDisabilityDto =
        healthDataMapperV2.toDto("application-id-1", pip2HealthDisabilityForm);

    assertThat(healthDisabilityDto.getApplicationId()).isEqualTo("application-id-1");
    assertThat(healthDisabilityDto.getConditions()).isEmpty();
    assertThat(healthDisabilityDto.getDailyLivingActivity()).isNull();
    assertThat(healthDisabilityDto.getMobility()).isNull();
  }
}
