package uk.gov.dwp.health.pip.application.manager.requestmodels.healthdisability;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

import java.util.List;

@Getter
@Builder(toBuilder = true)
public class FormData {
  @Default private String submissionDate = "2020-11-11";

  @Default private final Details details = Details.builder().build();

  @Getter
  @Builder(toBuilder = true)
  public static class Details {
    @Default private String forename = "first name";
    @Default private String surname = "last name";
    @Default private String title = "none";
    @Default private String nino = "RN000010A";
    @Default private String dob = "1980-12-13";
    @Default private String postcode = "LS1 1AB";
  }

  @Default private final Health health = Health.builder().build();

  @Getter
  @Builder(toBuilder = true)
  public static class Health {
    @Default private List<Conditions> conditions = List.of(Conditions.builder().build());
    @Getter
    @Builder(toBuilder = true)
    public static class Conditions {
      @Default private String healthCondition = "Illness";
      @Default private String conditionDescription = "very poorly";
      @Default private String approxStartDate = "2 years ago";
    }

    @Default private List<HealthProfessionalsDetails> healthProfessionalsDetails = List.of(HealthProfessionalsDetails.builder().build());
    @Getter
    @Builder(toBuilder = true)
    public static class HealthProfessionalsDetails {
      @Default private String fullName = "Dr Steve";
      @Default private String profession = "GP";
      @Default private String phoneNumber = "07777777777";
      @Default private Address address = Address.builder().build();

      @Getter
      @Builder(toBuilder = true)
      public static class Address {
        @Default private String line1 = "line1test";
        @Default private String line2 = "line2test";
        @Default private String line3 = "line3test";
        @Default private String town = "Leeds";
        @Default private String county = "W Yorkshire";
        @Default private String country = "England";
        @Default private String postcode = "LS1 1AB";
      }
      @Default private String lastSeen = "10-2022";
    }
  }

  @Default private final DailyLivingActivity dailyLivingActivity = DailyLivingActivity.builder().build();
  @Getter
  @Builder(toBuilder = true)
  public static class DailyLivingActivity {
    @Default private final PreparingFood preparingFood = PreparingFood.builder().build();
    @Getter
    @Builder(toBuilder = true)
    public static class PreparingFood {
      @Default private Boolean conditionAffected = true;
      @Default private String description = "description";
    }
    @Default private final EatingDrinking eatingDrinking = EatingDrinking.builder().build();
    @Getter
    @Builder(toBuilder = true)
    public static class EatingDrinking {
      @Default private Boolean conditionAffected = true;
      @Default private String useFeedingTube = "Yes";
      @Default private String description = "description";
    }
    @Default private final ManageTreatment manageTreatment = ManageTreatment.builder().build();
    @Getter
    @Builder(toBuilder = true)
    public static class ManageTreatment {
      @Default private Boolean conditionAffected = true;
      @Default private String description = "description";
    }
    @Default private final WashingBathing washingBathing = WashingBathing.builder().build();
    @Getter
    @Builder(toBuilder = true)
    public static class WashingBathing {
      @Default private Boolean conditionAffected = true;
      @Default private String description = "description";
    }
    @Default private final ToiletIncontinence toiletIncontinence = ToiletIncontinence.builder().build();
    @Getter
    @Builder(toBuilder = true)
    public static class ToiletIncontinence {
      @Default private Boolean conditionAffected = true;
      @Default private String description = "description";
    }
    @Default private final DressingUndressing dressingUndressing = DressingUndressing.builder().build();
    @Getter
    @Builder(toBuilder = true)
    public static class DressingUndressing {
      @Default private Boolean conditionAffected = true;
      @Default private String description = "description";
    }
    @Default private final CommCognitive commCognitive = CommCognitive.builder().build();
    @Getter
    @Builder(toBuilder = true)
    public static class CommCognitive {
      @Default private Boolean conditionAffected = true;
      @Default private String description = "description";
    }
    @Default private final Reading reading = Reading.builder().build();
    @Getter
    @Builder(toBuilder = true)
    public static class Reading {
      @Default private Boolean conditionAffected = true;
      @Default private String description = "description";
    }
    @Default private final Social social = Social.builder().build();
    @Getter
    @Builder(toBuilder = true)
    public static class Social {
      @Default private Boolean conditionAffected = true;
      @Default private String description = "description";
    }
    @Default private final ManageMoney manageMoney = ManageMoney.builder().build();
    @Getter
    @Builder(toBuilder = true)
    public static class ManageMoney {
      @Default private Boolean conditionAffected = true;
      @Default private String description = "description";
    }
  }

  @Default private final Mobility mobility = Mobility.builder().build();
  @Getter
  @Builder(toBuilder = true)
  public static class Mobility {
    @Default private final PlanningNavigate planningNavigate = PlanningNavigate.builder().build();
    @Getter
    @Builder(toBuilder = true)
    public static class PlanningNavigate {
      @Default private Boolean conditionAffected = true;
      @Default private String description = "description";
    }
    @Default private final MovingAround movingAround = MovingAround.builder().build();
    @Getter
    @Builder(toBuilder = true)
    public static class MovingAround {
      @Default private Boolean conditionAffected = true;
      @Default private String description = "description";
      @Default private Severity severity = Severity.builder().build();
      @Getter
      @Builder(toBuilder = true)
      public static class Severity {
        @Default private String grade = "lessThan20m";
        @Default private String note = "severity note";
      }
    }
  }

  @Default private final Other other = Other.builder().build();
  @Getter
  @Builder(toBuilder = true)
  public static class Other {
    @Default private Boolean conditionAffected = true;
    @Default private String description = "description";
  }
}
