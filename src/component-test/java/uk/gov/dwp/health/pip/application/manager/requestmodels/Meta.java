package uk.gov.dwp.health.pip.application.manager.requestmodels;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class Meta {
  @Default private final Nav nav = Nav.builder().build();

  @Getter
  @Builder(toBuilder = true)
  public static class Nav {
    @Default private String language = "en";
  }

  @Default private Identity personalDetails = Identity.builder().build();

  @Getter
  @Builder(toBuilder = true)
  public static class Identity {
    @Default private String id = "default";
  }

  @Default private Validation validation = Validation.builder().build();

  @Getter
  @Builder(toBuilder = true)
  public static class Validation {
    @Default private String start = null;
    @Default
    @JsonProperty("additional-support-communication")
    private String additionalSupportCommunication = null;
    @Default
    @JsonProperty("additional-support-understanding")
    private String additionalSupportUnderstanding = null;
    @Default
    @JsonProperty("additional-support-helping")
    private String additionalSupportHelping = null;
    @Default
    @JsonProperty("additional-support-helpers-name")
    private String additionalSupportHelpersName = null;
    @Default
    @JsonProperty("personal-details-name")
    private String personalDetailsName = null;
    @Default
    @JsonProperty("personal-details-nino")
    private String personalDetailsNino = null;
    @Default
    @JsonProperty("personal-details-date-of-birth")
    private String personalDetailsDateOfBirth = null;
    @Default
    @JsonProperty("personal-details-address")
    private String personalDetailsAddress = null;
    @Default
    @JsonProperty("personal-details-alternative-address")
    private String personalDetailsAlternativeAddress = null;
    @Default
    @JsonProperty("personal-details-contact-details")
    private String personalDetailsAContactDetails = null;
    @Default
    @JsonProperty("personal-details-other-formats")
    private String personalDetailsOtherFormats = null;
    @Default
    @JsonProperty("presence-nationality")
    private String presenceNationality = null;
    @Default
    @JsonProperty("presence-nationality-select")
    private String presenceNationalitySelect = null;
    @Default
    @JsonProperty("presence-living-in-uk")
    private String presenceLivingInUk = null;
    @Default
    @JsonProperty("presence-recent")
    private String presenceRecent = null;
    @Default
    @JsonProperty("presence-eea-question")
    private String presenceEeaQuestion = null;
    @Default
    @JsonProperty("about-your-health-health-conditions")
    private String aboutYourHealthHealthConditions = null;
    @Default
    @JsonProperty("about-your-health-health-professional-contact-consent")
    private String aboutYourHealthHealthProfessionalContactConsent = null;
    @Default
    @JsonProperty("about-your-health-health-professional-share-consent")
    private String aboutYourHealthHealthProfessionalShareConsent = null;
    @Default
    @JsonProperty("about-your-health-health-professional-details")
    private String aboutYourHealthHealthProfessionalDetails = null;
    @Default
    @JsonProperty("about-your-health-health-professional-add-another")
    private String aboutYourHealthHealthProfessionalAddAnother = null;
    @Default
    @JsonProperty("about-your-health-accommodation")
    private String aboutYourHealthAccommodation = null;
    @Default
    @JsonProperty("about-your-health-accommodation-admission")
    private String aboutYourHealthAccommodationAdmission = null;
    @Default
    @JsonProperty("about-your-health-accommodation-address")
    private String aboutYourHealthAccommodationAddress = null;
    @Default
    @JsonProperty("check-your-answers")
    private String checkYourAnswers = null;
    @Default
    @JsonProperty("condition")
    private String condition = null;
    @Default
    @JsonProperty("condition-details")
    private String conditionDetails = null;
    @Default
    @JsonProperty("another-condition")
    private String anotherCondition = null;
    @Default
    @JsonProperty("condition-cya")
    private String conditionCya = null;
    @Default
    @JsonProperty("preparing-food")
    private String preparingFood = null;
    @Default
    @JsonProperty("preparing-food-details")
    private String preparingFoodDetails = null;
    @Default
    @JsonProperty("preparing-food-cya")
    private String preparingFoodCya = null;
    @Default
    @JsonProperty("health-professionals-introduction")
    private String healthProfessionalsIntroduction = null;
    @Default
    @JsonProperty("health-professionals-question")
    private String healthProfessionalsQuestion = null;
    @Default
    @JsonProperty("health-professionals-cya")
    private String healthProfessionalsCya = null;
    @Default
    @JsonProperty("eating-and-drinking")
    private String eatingAndDrinking = null;
    @Default
    @JsonProperty("feeding-tube")
    private String feedingTube = null;
    @Default
    @JsonProperty("eating-and-drinking-details")
    private String eatingAndDrinkingDetails = null;
    @Default
    @JsonProperty("managing-treatments")
    private String managingTreatments = null;
    @Default
    @JsonProperty("managing-treatments-details")
    private String managingTreatmentsDetails = null;
    @Default
    @JsonProperty("managing-treatments-therapies")
    private String managingTreatmentTherapies = null;
    @Default
    @JsonProperty("managing-treatments-cya")
    private String managingTreatmentCya = null;
    @Default
    @JsonProperty("washing-and-bathing")
    private String washingAndBathing = null;
    @Default
    @JsonProperty("washing-and-bathing-details")
    private String washingAndBathingDetails = null;
    @Default
    @JsonProperty("washing-and-bathing-cya")
    private String washingAndBathingCya = null;
    @Default
    @JsonProperty("managing-toilet-needs")
    private String managingToiletNeeds = null;
    @Default
    @JsonProperty("managing-toilet-needs-details")
    private String managingToiletNeedsDetails = null;
    @Default
    @JsonProperty("managing-toilet-needs-cya")
    private String managingToiletNeedsCya = null;
    @Default
    @JsonProperty("dressing-and-undressing")
    private String dressingAndUndressing = null;
    @Default
    @JsonProperty("dressing-and-undressing-details")
    private String dressingAndUndressingDetails = null;
    @Default
    @JsonProperty("dressing-and-undressing-cya")
    private String dressingAndUndressingCya = null;
    @Default
    @JsonProperty("talking-and-listening")
    private String talkingAndListening = null;
    @Default
    @JsonProperty("talking-and-listening-details")
    private String talkingAndListeningDetails = null;
    @Default
    @JsonProperty("talking-and-listening-cya")
    private String talkingAndListeningCya = null;
    @Default
    @JsonProperty("reading")
    private String reading = null;
    @Default
    @JsonProperty("reading-details")
    private String readingDetails = null;
    @Default
    @JsonProperty("reading-cya")
    private String readingCya = null;
    @Default
    @JsonProperty("mixing-with-other-people")
    private String mixingWithOtherPeople = null;
    @Default
    @JsonProperty("mixing-with-other-people-cya")
    private String mixingWithOtherPeopleCya = null;
    @Default
    @JsonProperty("managing-money")
    private String managingMoney = null;
    @Default
    @JsonProperty("managing-money-cya")
    private String managingMoneyCya = null;
    @Default
    @JsonProperty("planning-and-following-a-journey")
    private String planningAndFollowingAJourney = null;
    @Default
    @JsonProperty("planning-and-following-a-journey-cya")
    private String planningAndFollowingAJourneyCya = null;
    @Default
    @JsonProperty("moving-around")
    private String movingAround = null;
    @Default
    @JsonProperty("moving-around-cya")
    private String movingAroundCya = null;
    @Default
    @JsonProperty("additional-information")
    private String additionalInformation = null;
    @Default
    @JsonProperty("additional-information-cya")
    private String additionalInformationCya = null;
  }
}
