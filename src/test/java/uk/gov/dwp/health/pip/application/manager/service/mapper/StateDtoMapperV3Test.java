package uk.gov.dwp.health.pip.application.manager.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.dwp.health.pip.application.manager.entity.History;
import uk.gov.dwp.health.pip.application.manager.entity.State;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v3.dto.HistoryDto.StateEnum;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v3.dto.StateDto.CurrentStateEnum;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
@Tag("unit")
public class StateDtoMapperV3Test {

  private StateDtoMapperV3 stateDtoMapperV3;

  @BeforeEach
  void beforeEach() {
    stateDtoMapperV3 = new StateDtoMapperV3();
  }

  @Nested
  class FormToApiTest {

    @Test
    void when_valid_data() {
      var instant = Instant.now();
      var stateHistory = new History();
      stateHistory.setState("HEALTH_AND_DISABILITY");
      stateHistory.setTimeStamp(instant);
      var stateDetails = new State();
      stateDetails.setCurrent("SUBMITTED");
      stateDetails.setHistory(List.of(stateHistory));

      var stateDto = stateDtoMapperV3.toDto(stateDetails);

      assertThat(stateDto.getCurrentState()).isEqualTo(CurrentStateEnum.SUBMITTED);
      assertThat(stateDto.getHistory()).hasSize(1);

      var history = stateDto.getHistory().get(0);
      assertThat(history.getState()).isEqualTo(StateEnum.HEALTH_AND_DISABILITY);
      assertThat(history.getTimestamp()).isEqualTo(instant.toString());
    }
  }
}
