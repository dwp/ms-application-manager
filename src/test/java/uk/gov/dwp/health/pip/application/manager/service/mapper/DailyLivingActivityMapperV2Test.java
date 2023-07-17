package uk.gov.dwp.health.pip.application.manager.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.health.pip.application.manager.openapi.healthdisability.v2.dto.ActivityDto;
import uk.gov.dwp.health.pip.application.manager.openapi.healthdisability.v2.dto.DailyLivingActivityDto;
import uk.gov.dwp.health.pip2.common.model.dla.CommCognitive;
import uk.gov.dwp.health.pip2.common.model.dla.DailyLiving;
import uk.gov.dwp.health.pip2.common.model.dla.Dressing;
import uk.gov.dwp.health.pip2.common.model.dla.EatingDrinking;
import uk.gov.dwp.health.pip2.common.model.dla.ManageFinance;
import uk.gov.dwp.health.pip2.common.model.dla.ManageTreatment;
import uk.gov.dwp.health.pip2.common.model.dla.PreparingFood;
import uk.gov.dwp.health.pip2.common.model.dla.Reading;
import uk.gov.dwp.health.pip2.common.model.dla.Social;
import uk.gov.dwp.health.pip2.common.model.dla.ToiletIncontinence;
import uk.gov.dwp.health.pip2.common.model.dla.WashingBathing;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Tag("unit")
class DailyLivingActivityMapperV2Test {

  private DailyLivingActivityMapperV2 dailyLivingActivityMapperV2;

  @BeforeEach
  void beforeEach() {
    dailyLivingActivityMapperV2 = new DailyLivingActivityMapperV2();
  }

  @Test
  void when_activities_present() {
    var dailyLiving = getDailyLiving();

    var dailyLivingActivityDto = dailyLivingActivityMapperV2.toDto(dailyLiving);

    verifyActivities(dailyLivingActivityDto);
  }

  @Test
  void when_activities_null() {
    var dailyLivingActivityDto = dailyLivingActivityMapperV2.toDto(null);

    verifyActivitiesNull(dailyLivingActivityDto);
  }

  @Test
  void when_activity_null() {
    var dailyLiving = DailyLiving.builder().build();

    var dailyLivingActivityDto = dailyLivingActivityMapperV2.toDto(dailyLiving);

    verifyActivityNull(dailyLivingActivityDto.getPreparingFood());
    verifyActivityNull(dailyLivingActivityDto.getEatingDrinking());
    verifyActivityNull(dailyLivingActivityDto.getManageTreatment());
    verifyActivityNull(dailyLivingActivityDto.getWashingBathing());
    verifyActivityNull(dailyLivingActivityDto.getToiletIncontinence());
    verifyActivityNull(dailyLivingActivityDto.getDressingUndressing());
    verifyActivityNull(dailyLivingActivityDto.getCommCognitive());
    verifyActivityNull(dailyLivingActivityDto.getReading());
    verifyActivityNull(dailyLivingActivityDto.getSocial());
    verifyActivityNull(dailyLivingActivityDto.getManageMoney());
  }

  private void verifyActivityNull(ActivityDto activityDto) {
    assertThat(activityDto.isConditionAffected()).isNull();
    assertThat(activityDto.getDescription()).isNull();
    assertThat(activityDto.getTherapy()).isNull();
    assertThat(activityDto.getSeverity()).isNull();
  }

  private DailyLiving getDailyLiving() {
    return DailyLiving.builder()
        .preparingFood(getPreparingFood())
        .eatingDrinking(getEatingDrinking())
        .manageTreatment(getManageTreatment())
        .washingBathing(getWashingBathing())
        .toiletIncontinence(getToiletIncontinence())
        .dressing(getDressing())
        .cognitive(getCommCognitive())
        .reading(getReading())
        .social(getSocial())
        .manageFinance(getManageFinance())
        .build();
  }

  private PreparingFood getPreparingFood() {
    var preparingFood = new PreparingFood();
    preparingFood.setAffected(true);
    preparingFood.setDescription("preparing-food");
    return preparingFood;
  }

  private EatingDrinking getEatingDrinking() {
    var eatingDrinking = new EatingDrinking();
    eatingDrinking.setAffected(true);
    eatingDrinking.setDescription("eating-drinking");
    eatingDrinking.setFeedTube("feed-tube");
    return eatingDrinking;
  }

  private ManageTreatment getManageTreatment() {
    var manageTreatment = new ManageTreatment();
    manageTreatment.setAffected(true);
    manageTreatment.setDescription("manage-treatment");
    manageTreatment.setTherapy("therapy");
    return manageTreatment;
  }

  private WashingBathing getWashingBathing() {
    var washingBathing = new WashingBathing();
    washingBathing.setAffected(true);
    washingBathing.setDescription("washing-bathing");
    return washingBathing;
  }

  private ToiletIncontinence getToiletIncontinence() {
    var toiletIncontinence = new ToiletIncontinence();
    toiletIncontinence.setAffected(true);
    toiletIncontinence.setDescription("toilet-incontinence");
    return toiletIncontinence;
  }

  private Dressing getDressing() {
    var dressing = new Dressing();
    dressing.setAffected(true);
    dressing.setDescription("dressing");
    return dressing;
  }

  private CommCognitive getCommCognitive() {
    var commCognitive = new CommCognitive();
    commCognitive.setAffected(true);
    commCognitive.setDescription("comm-cognitive");
    return commCognitive;
  }

  private Reading getReading() {
    var reading = new Reading();
    reading.setAffected(true);
    reading.setDescription("reading");
    return reading;
  }

  private Social getSocial() {
    var social = new Social();
    social.setAffected(true);
    social.setDescription("social");
    return social;
  }

  private ManageFinance getManageFinance() {
    var manageFinance = new ManageFinance();
    manageFinance.setAffected(true);
    manageFinance.setDescription("manage-finance");
    return manageFinance;
  }

  private void verifyActivities(DailyLivingActivityDto dailyLivingActivityDto) {
    verifyActivity(dailyLivingActivityDto.getPreparingFood(), "preparing-food", null);
    verifyActivity(dailyLivingActivityDto.getEatingDrinking(), "eating-drinking", "feed-tube");
    verifyActivity(dailyLivingActivityDto.getManageTreatment(), "manage-treatment", "therapy");
    verifyActivity(dailyLivingActivityDto.getWashingBathing(), "washing-bathing", null);
    verifyActivity(dailyLivingActivityDto.getToiletIncontinence(), "toilet-incontinence", null);
    verifyActivity(dailyLivingActivityDto.getDressingUndressing(), "dressing", null);
    verifyActivity(dailyLivingActivityDto.getCommCognitive(), "comm-cognitive", null);
    verifyActivity(dailyLivingActivityDto.getReading(), "reading", null);
    verifyActivity(dailyLivingActivityDto.getSocial(), "social", null);
    verifyActivity(dailyLivingActivityDto.getManageMoney(), "manage-finance", null);
  }

  private void verifyActivity(ActivityDto activityDto, String description, String therapy) {
    assertThat(activityDto.isConditionAffected()).isTrue();
    assertThat(activityDto.getDescription()).isEqualTo(description);
    assertThat(activityDto.getTherapy()).isEqualTo(therapy);
  }

  private void verifyActivitiesNull(DailyLivingActivityDto dailyLivingActivityDto) {
    assertThat(dailyLivingActivityDto.getPreparingFood()).isNull();
    assertThat(dailyLivingActivityDto.getEatingDrinking()).isNull();
    assertThat(dailyLivingActivityDto.getManageTreatment()).isNull();
    assertThat(dailyLivingActivityDto.getWashingBathing()).isNull();
    assertThat(dailyLivingActivityDto.getToiletIncontinence()).isNull();
    assertThat(dailyLivingActivityDto.getDressingUndressing()).isNull();
    assertThat(dailyLivingActivityDto.getCommCognitive()).isNull();
    assertThat(dailyLivingActivityDto.getReading()).isNull();
    assertThat(dailyLivingActivityDto.getSocial()).isNull();
    assertThat(dailyLivingActivityDto.getManageMoney()).isNull();
  }
}
