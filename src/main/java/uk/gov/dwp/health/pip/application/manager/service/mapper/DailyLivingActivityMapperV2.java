package uk.gov.dwp.health.pip.application.manager.service.mapper;

import org.springframework.stereotype.Component;
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

import static java.util.Optional.ofNullable;

@Component
class DailyLivingActivityMapperV2 {

  DailyLivingActivityDto toDto(DailyLiving dailyLiving) {
    if (ofNullable(dailyLiving).isPresent()) {
      return new DailyLivingActivityDto()
          .preparingFood(toDto(dailyLiving.getPreparingFood()))
          .eatingDrinking(toDto(dailyLiving.getEatingDrinking()))
          .manageTreatment(toDto(dailyLiving.getManageTreatment()))
          .washingBathing(toDto(dailyLiving.getWashingBathing()))
          .toiletIncontinence(toDto(dailyLiving.getToiletIncontinence()))
          .dressingUndressing(toDto(dailyLiving.getDressing()))
          .commCognitive(toDto(dailyLiving.getCognitive()))
          .reading(toDto(dailyLiving.getReading()))
          .social(toDto(dailyLiving.getSocial()))
          .manageMoney(toDto(dailyLiving.getManageFinance()));
    }
    return new DailyLivingActivityDto();
  }

  private ActivityDto toDto(PreparingFood preparingFood) {
    if (ofNullable(preparingFood).isPresent()) {
      return new ActivityDto()
          .conditionAffected(preparingFood.isAffected())
          .description(preparingFood.getDescription());
    }
    return new ActivityDto();
  }

  private ActivityDto toDto(EatingDrinking eatingDrinking) {
    if (ofNullable(eatingDrinking).isPresent()) {
      return new ActivityDto()
          .conditionAffected(eatingDrinking.isAffected())
          .description(eatingDrinking.getDescription())
          .therapy(eatingDrinking.getFeedTube());
    }
    return new ActivityDto();
  }

  private ActivityDto toDto(ManageTreatment manageTreatment) {
    if (ofNullable(manageTreatment).isPresent()) {
      return new ActivityDto()
          .conditionAffected(manageTreatment.isAffected())
          .description(manageTreatment.getDescription())
          .therapy(manageTreatment.getTherapy());
    }
    return new ActivityDto();
  }

  private ActivityDto toDto(WashingBathing washingBathing) {
    if (ofNullable(washingBathing).isPresent()) {
      return new ActivityDto()
          .conditionAffected(washingBathing.isAffected())
          .description(washingBathing.getDescription());
    }
    return new ActivityDto();
  }

  private ActivityDto toDto(ToiletIncontinence toiletIncontinence) {
    if (ofNullable(toiletIncontinence).isPresent()) {
      return new ActivityDto()
          .conditionAffected(toiletIncontinence.isAffected())
          .description(toiletIncontinence.getDescription());
    }
    return new ActivityDto();
  }

  private ActivityDto toDto(Dressing dressing) {
    if (ofNullable(dressing).isPresent()) {
      return new ActivityDto()
          .conditionAffected(dressing.isAffected())
          .description(dressing.getDescription());
    }
    return new ActivityDto();
  }

  private ActivityDto toDto(CommCognitive commCognitive) {
    if (ofNullable(commCognitive).isPresent()) {
      return new ActivityDto()
          .conditionAffected(commCognitive.isAffected())
          .description(commCognitive.getDescription());
    }
    return new ActivityDto();
  }

  private ActivityDto toDto(Reading reading) {
    if (ofNullable(reading).isPresent()) {
      return new ActivityDto()
          .conditionAffected(reading.isAffected())
          .description(reading.getDescription());
    }
    return new ActivityDto();
  }

  private ActivityDto toDto(Social social) {
    if (ofNullable(social).isPresent()) {
      return new ActivityDto()
          .conditionAffected(social.isAffected())
          .description(social.getDescription());
    }
    return new ActivityDto();
  }

  private ActivityDto toDto(ManageFinance manageFinance) {
    if (ofNullable(manageFinance).isPresent()) {
      return new ActivityDto()
          .conditionAffected(manageFinance.isAffected())
          .description(manageFinance.getDescription());
    }
    return new ActivityDto();
  }
}
