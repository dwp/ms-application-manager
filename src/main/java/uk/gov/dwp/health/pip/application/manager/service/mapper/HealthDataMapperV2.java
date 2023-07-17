package uk.gov.dwp.health.pip.application.manager.service.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.dwp.health.pip.application.manager.openapi.healthdisability.v2.dto.HealthDisabilityDto;
import uk.gov.dwp.health.pip2.common.Pip2HealthDisabilityForm;
import uk.gov.dwp.health.pip2.common.model.about.Health;

@Component
@RequiredArgsConstructor
public class HealthDataMapperV2 {

  private final ConditionsMapperV2 conditionsMapperV2;
  private final DailyLivingActivityMapperV2 dailyLivingActivityMapperV2;
  private final MobilityMapperV2 mobilityMapperV2;

  public HealthDisabilityDto toDto(
      String applicationId, Pip2HealthDisabilityForm pip2HealthDisabilityForm) {

    if (pip2HealthDisabilityForm.getHealth() == null) {
      pip2HealthDisabilityForm.setHealth(Health.builder().build());
    }

    return new HealthDisabilityDto()
        .applicationId(applicationId)
        .conditions(conditionsMapperV2.toDtos(pip2HealthDisabilityForm.getHealth().getConditions()))
        .dailyLivingActivity(
            dailyLivingActivityMapperV2.toDto(pip2HealthDisabilityForm.getDailyLiving()))
        .mobility(mobilityMapperV2.toDto(pip2HealthDisabilityForm.getMobility()));
  }
}
