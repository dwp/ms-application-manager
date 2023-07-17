package uk.gov.dwp.health.pip.application.manager.service.mapper;

import org.springframework.stereotype.Component;
import uk.gov.dwp.health.pip.application.manager.openapi.healthdisability.v2.dto.ActivityDto;
import uk.gov.dwp.health.pip.application.manager.openapi.healthdisability.v2.dto.MobilityDto;
import uk.gov.dwp.health.pip.application.manager.openapi.healthdisability.v2.dto.SeverityDto;
import uk.gov.dwp.health.pip2.common.model.mobility.Mobility;
import uk.gov.dwp.health.pip2.common.model.mobility.MovingRound;
import uk.gov.dwp.health.pip2.common.model.mobility.PlanNavigate;
import uk.gov.dwp.health.pip2.common.model.mobility.SeverityMeasure;

import static java.util.Optional.ofNullable;

@Component
class MobilityMapperV2 {

  MobilityDto toDto(Mobility mobility) {
    if (ofNullable(mobility).isPresent()) {
      return new MobilityDto()
          .planningNavigate(toDto(mobility.getPlanNavigate()))
          .movingAround(toDto(mobility.getMovingRound()));
    }
    return new MobilityDto();
  }

  private ActivityDto toDto(PlanNavigate planNavigate) {
    if (ofNullable(planNavigate).isPresent()) {
      return new ActivityDto()
          .conditionAffected(planNavigate.isAffected())
          .description(planNavigate.getDescription());
    }
    return new ActivityDto();
  }

  private ActivityDto toDto(MovingRound movingRound) {
    if (ofNullable(movingRound).isPresent()) {
      return new ActivityDto()
          .conditionAffected(movingRound.isAffected())
          .description(movingRound.getDescription())
          .severity(toDto(movingRound.getSeverity()));
    }
    return new ActivityDto();
  }

  private SeverityDto toDto(SeverityMeasure severityMeasure) {
    if (ofNullable(severityMeasure).isPresent()) {
      return new SeverityDto()
          .grade(SeverityDto.GradeEnum.fromValue(severityMeasure.getGrade().toValue()))
          .note(severityMeasure.getNote());
    }
    return new SeverityDto();
  }
}
