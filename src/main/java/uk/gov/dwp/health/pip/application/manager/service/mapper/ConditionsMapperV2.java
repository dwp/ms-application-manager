package uk.gov.dwp.health.pip.application.manager.service.mapper;

import org.springframework.stereotype.Component;
import uk.gov.dwp.health.pip.application.manager.openapi.healthdisability.v2.dto.ConditionDto;
import uk.gov.dwp.health.pip2.common.model.about.Condition;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Component
class ConditionsMapperV2 {

  List<ConditionDto> toDtos(List<Condition> conditions) {
    if (ofNullable(conditions).isEmpty()) {
      return Collections.emptyList();
    }

    return conditions.stream().map(this::toDto).collect(Collectors.toList());
  }

  private ConditionDto toDto(Condition condition) {
    return new ConditionDto()
        .healthCondition(condition.getHealthCondition())
        .approxStartDate(condition.getApproxStartDate())
        .conditionDescription(condition.getDescription());
  }
}
