package uk.gov.dwp.health.pip.application.manager.service.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.dwp.health.pip.application.manager.entity.History;
import uk.gov.dwp.health.pip.application.manager.entity.State;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v3.dto.HistoryDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v3.dto.HistoryDto.StateEnum;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v3.dto.StateDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v3.dto.StateDto.CurrentStateEnum;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StateDtoMapperV3 {
  StateDto toDto(State state) {
    return new StateDto()
        .currentState(CurrentStateEnum.valueOf(state.getCurrent()))
        .history(getHistory(state.getHistory()));
  }

  private List<HistoryDto> getHistory(List<History> history) {
    List<HistoryDto> historyList = new ArrayList<>();

    history.forEach(his -> {
      var historyDto = new HistoryDto()
          .state(StateEnum.valueOf(his.getState()))
          .timestamp(his.getTimeStamp().toString());

      historyList.add(historyDto);
    });

    return historyList;
  }
}
