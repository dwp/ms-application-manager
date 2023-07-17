package uk.gov.dwp.health.pip.application.manager.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.health.pip.application.manager.openapi.healthdisability.v2.dto.ActivityDto;
import uk.gov.dwp.health.pip.application.manager.openapi.healthdisability.v2.dto.MobilityDto;
import uk.gov.dwp.health.pip.application.manager.openapi.healthdisability.v2.dto.SeverityDto;
import uk.gov.dwp.health.pip2.common.model.mobility.Mobility;
import uk.gov.dwp.health.pip2.common.model.mobility.MovingRound;
import uk.gov.dwp.health.pip2.common.model.mobility.PlanNavigate;
import uk.gov.dwp.health.pip2.common.model.mobility.SeverityLevel;
import uk.gov.dwp.health.pip2.common.model.mobility.SeverityMeasure;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Tag("unit")
class MobilityMapperV2Test {

  private MobilityMapperV2 mobilityMapperV2;

  @BeforeEach
  void beforeEach() {
    mobilityMapperV2 = new MobilityMapperV2();
  }

  @Test
  void map_mobility_to_dto() {
    var mobility = getMobility();

    var mobilityDto = mobilityMapperV2.toDto(mobility);

    verifyMobilityDto(mobilityDto);
  }

  @Test
  void when_mobility_null() {
    var mobilityDto = mobilityMapperV2.toDto(null);

    assertThat(mobilityDto.getPlanningNavigate()).isNull();
    assertThat(mobilityDto.getMovingAround()).isNull();
  }

  @Test
  void when_mobility_options_null() {
    var mobility = Mobility.builder().build();

    var mobilityDto = mobilityMapperV2.toDto(mobility);

    verifyActivityNull(mobilityDto.getPlanningNavigate());
    verifyActivityNull(mobilityDto.getMovingAround());
  }

  @Test
  void when_severity_measure_null() {
    var movingRound = new MovingRound();
    movingRound.setAffected(true);
    movingRound.setDescription("moving-round");

    var mobility = Mobility.builder().movingRound(movingRound).build();

    var mobilityDto = mobilityMapperV2.toDto(mobility);

    assertThat(mobilityDto.getMovingAround().getSeverity().getGrade()).isNull();
    assertThat(mobilityDto.getMovingAround().getSeverity().getNote()).isNull();
  }

  private Mobility getMobility() {
    return Mobility.builder().planNavigate(getPlanNavigate()).movingRound(getMovingRound()).build();
  }

  private PlanNavigate getPlanNavigate() {
    var planNavigate = new PlanNavigate();
    planNavigate.setAffected(true);
    planNavigate.setDescription("plan-navigate");
    return planNavigate;
  }

  private MovingRound getMovingRound() {
    var movingRound = new MovingRound();
    movingRound.setAffected(true);
    movingRound.setDescription("moving-round");
    movingRound.setSeverity(
        SeverityMeasure.builder().grade(SeverityLevel.BET_20M_50M).note("severity").build());
    return movingRound;
  }

  private void verifyMobilityDto(MobilityDto mobilityDto) {
    assertThat(mobilityDto.getPlanningNavigate().isConditionAffected()).isTrue();
    assertThat(mobilityDto.getPlanningNavigate().getDescription()).isEqualTo("plan-navigate");
    assertThat(mobilityDto.getMovingAround().isConditionAffected()).isTrue();
    assertThat(mobilityDto.getMovingAround().getDescription()).isEqualTo("moving-round");
    assertThat(mobilityDto.getMovingAround().getSeverity().getGrade())
        .isEqualTo(SeverityDto.GradeEnum.BETWEEN20AND50M);
    assertThat(mobilityDto.getMovingAround().getSeverity().getNote()).isEqualTo("severity");
  }

  private void verifyActivityNull(ActivityDto activityDto) {
    assertThat(activityDto.isConditionAffected()).isNull();
    assertThat(activityDto.getDescription()).isNull();
    assertThat(activityDto.getTherapy()).isNull();
    assertThat(activityDto.getSeverity()).isNull();
  }
}
