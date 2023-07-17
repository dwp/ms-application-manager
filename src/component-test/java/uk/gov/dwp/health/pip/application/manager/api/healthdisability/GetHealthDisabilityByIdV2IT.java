package uk.gov.dwp.health.pip.application.manager.api.healthdisability;

import org.junit.jupiter.api.Test;
import uk.gov.dwp.health.pip.application.manager.api.ApiTest;
import uk.gov.dwp.health.pip.application.manager.config.MongoClientConnection;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.entity.FormData;
import uk.gov.dwp.health.pip.application.manager.openapi.healthdisability.v2.dto.ActivityDto;
import uk.gov.dwp.health.pip.application.manager.openapi.healthdisability.v2.dto.ConditionDto;
import uk.gov.dwp.health.pip.application.manager.openapi.healthdisability.v2.dto.DailyLivingActivityDto;
import uk.gov.dwp.health.pip.application.manager.openapi.healthdisability.v2.dto.HealthDisabilityDto;
import uk.gov.dwp.health.pip.application.manager.openapi.healthdisability.v2.dto.MobilityDto;
import uk.gov.dwp.health.pip.application.manager.openapi.healthdisability.v2.dto.SeverityDto;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static support.FileUtils.readTestFileAsObject;
import static uk.gov.dwp.health.pip.application.manager.utils.UrlBuilderUtil.buildGetHealthDisabilityByIdV2Url;

class GetHealthDisabilityByIdV2IT extends ApiTest {

  @Test
  void shouldReturn200StatusCodeAndCorrectResponseBody() throws IOException {
    var formDataAsObject = readTestFileAsObject("health-and-disability/validHealthData.json");
    var application =
        Application.builder()
            .healthDisabilityData(FormData.builder().data(formDataAsObject).build())
            .build();
    var mongoTemplate = MongoClientConnection.getMongoTemplate();
    var savedApplication = mongoTemplate.save(application, "application");

    var url = buildGetHealthDisabilityByIdV2Url(savedApplication.getId());
    int actualResponseCode = getRequest(url).statusCode();
    var healthDisabilityDto = extractGetRequest(url, HealthDisabilityDto.class);

    assertThat(actualResponseCode).isEqualTo(200);
    assertThat(healthDisabilityDto.getApplicationId()).isNotNull();
    verifyConditionDtos(healthDisabilityDto.getConditions());
    verifyDailyLivingActivityDto(healthDisabilityDto.getDailyLivingActivity());
    verifyMobilityDto(healthDisabilityDto.getMobility());
  }

  private void verifyConditionDtos(List<ConditionDto> conditionDtos) {
    assertThat(conditionDtos).hasSize(1);

    var conditionDto = conditionDtos.get(0);
    assertThat(conditionDto.getHealthCondition()).isEqualTo("Kidney Failure");
    assertThat(conditionDto.getConditionDescription()).isEqualTo("description of health condition");
    assertThat(conditionDto.getApproxStartDate()).isEqualTo("1999-8-08");
  }

  private void verifyDailyLivingActivityDto(DailyLivingActivityDto dailyLivingActivityDto) {
    verifyActivity(
        dailyLivingActivityDto.getPreparingFood(), "reason not be able to preparing food", null);
    verifyActivity(
        dailyLivingActivityDto.getEatingDrinking(),
        "reason not be able to drink and eat unassisted",
        "Yes");
    verifyActivity(
        dailyLivingActivityDto.getManageTreatment(), "reason not be able to apply treatment", null);
    verifyActivity(
        dailyLivingActivityDto.getWashingBathing(), "reason not be able to washing", null);
    verifyActivity(
        dailyLivingActivityDto.getToiletIncontinence(),
        "reason not be able to  go to toilet unassisted",
        null);
    verifyActivity(
        dailyLivingActivityDto.getDressingUndressing(),
        "reason not be able to dressing unassisted",
        null);
    verifyActivity(
        dailyLivingActivityDto.getCommCognitive(), "reason not be able to communicate", null);
    verifyActivity(dailyLivingActivityDto.getReading(), "reason not be able to read", null);
    verifyActivity(
        dailyLivingActivityDto.getSocial(),
        "reason not be able to social mixture withe others",
        null);
    verifyActivity(
        dailyLivingActivityDto.getManageMoney(), "reason not be able to manage own finance", null);
  }

  private void verifyActivity(ActivityDto activityDto, String description, String therapy) {
    assertThat(activityDto.isConditionAffected()).isTrue();
    assertThat(activityDto.getDescription()).isEqualTo(description);
    assertThat(activityDto.getTherapy()).isEqualTo(therapy);
    assertThat(activityDto.getSeverity()).isNull();
  }

  private void verifyMobilityDto(MobilityDto mobilityDto) {
    assertThat(mobilityDto.getPlanningNavigate().isConditionAffected()).isTrue();
    assertThat(mobilityDto.getPlanningNavigate().getDescription())
        .isEqualTo("reason not be able to plan or navigate");
    assertThat(mobilityDto.getMovingAround().isConditionAffected()).isTrue();
    assertThat(mobilityDto.getMovingAround().getDescription())
        .isEqualTo("reason not be able to move");
    assertThat(mobilityDto.getMovingAround().getSeverity().getGrade())
        .isEqualTo(SeverityDto.GradeEnum.LESSTHAN20M);
    assertThat(mobilityDto.getMovingAround().getSeverity().getNote()).isEqualTo("severity note");
  }
}
