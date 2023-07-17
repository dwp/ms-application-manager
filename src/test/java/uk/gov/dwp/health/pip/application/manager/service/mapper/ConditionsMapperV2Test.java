package uk.gov.dwp.health.pip.application.manager.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.health.pip2.common.model.about.Condition;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Tag("unit")
class ConditionsMapperV2Test {

  private ConditionsMapperV2 conditionsMapperV2;

  @BeforeEach
  void beforeEach() {
    conditionsMapperV2 = new ConditionsMapperV2();
  }

  @Test
  void when_conditions_present() {
    var conditions =
        List.of(
            Condition.builder()
                .healthCondition("health-condition")
                .approxStartDate("approximate-start-date")
                .description("description")
                .build());

    var conditionDtos = conditionsMapperV2.toDtos(conditions);

    assertThat(conditionDtos).hasSize(1);
    assertThat(conditionDtos.get(0).getHealthCondition()).isEqualTo("health-condition");
    assertThat(conditionDtos.get(0).getApproxStartDate()).isEqualTo("approximate-start-date");
    assertThat(conditionDtos.get(0).getConditionDescription()).isEqualTo("description");
  }

  @Test
  void when_conditions_empty() {
    var conditionDtos = conditionsMapperV2.toDtos(emptyList());

    assertThat(conditionDtos).isEmpty();
  }

  @Test
  void when_conditions_null() {
    var conditionDtos = conditionsMapperV2.toDtos(null);

    assertThat(conditionDtos).isEmpty();
  }
}
