package uk.gov.dwp.health.pip.application.manager.requestmodels.registration;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
@Builder(toBuilder = true)
public class FormData {
  @Default private final AdditionalSupport additionalSupport = AdditionalSupport.builder().build();

  @Getter
  @Builder(toBuilder = true)
  public static class AdditionalSupport {
    @Default private Boolean helpCommunicating = true;
    @Default private Boolean helpUnderstandingLetters = true;
    @Default private HelperDetails helperDetails = HelperDetails.builder().build();

    @Getter
    @Builder(toBuilder = true)
    public static class HelperDetails {
      @Default private String firstname = "Florenzzze";
      @Default private String surname = "Nightingalezzz";
    }
  }

  @Default private PersonalDetails personalDetails = PersonalDetails.builder().build();

  @Getter
  @Builder(toBuilder = true)
  public static class PersonalDetails {
    @Default private String firstname = "Alanzzz";
    @Default private String surname = "Smithzzz";
    @Default private String nino = "RN000009C";
    @Default private String dob = "2000-01-01";

    @Default private BankDetails bankDetails = BankDetails.builder().build();

    @Getter
    @Builder(toBuilder = true)
    public static class BankDetails {
      @Default private String enterBankDetail = "No";
      @Default private String accountNumber = "12341234";
      @Default private String sortCode = "123123";
    }

    @Default private Address address = Address.builder().build();

    @Getter
    @Builder(toBuilder = true)
    public static class Address {
      @Default private String line1 = "123";
      @Default private String line2 = "Headrow";
      @Default private String town = "Leeds";
      @Default private String county = "West Yorkshire";
      @Default private String postcode = "LS1 1AB";
      @Default private String country = "England";
    }

    @Default private AlternativeAddress alternativeAddress = AlternativeAddress.builder().build();

    @Getter
    @Builder(toBuilder = true)
    public static class AlternativeAddress {
      @Default private String line1 = "123";
      @Default private String line2 = "Headrow";
      @Default private String town = "Leeds";
      @Default private String county = "West Yorkshire";
      @Default private String postcode = "LS1 1AB";
      @Default private String country = "England";
    }

    @Default private Contact contact = Contact.builder().build();

    @Getter
    @Builder(toBuilder = true)
    public static class Contact {
      @Default private String mobileNumber = "07777777777";
      @Default private String alternativeNumber = "07777777777";
      @Default private String textphone = "07777777777";
      @Default private boolean smsUpdates = true;
    }

    @Default private AlternativeFormat alternativeFormat = AlternativeFormat.builder().build();

    @Getter
    @Builder(toBuilder = true)
    public static class AlternativeFormat {
      @Default private String formatType = "other";
      @Default private String otherOptions = "colouredPaper";
      @Default private String alternateFormatAdditionalInfo = "abcDEFklmnO";
    }
  }

  @Default private MotabilityScheme motabilityScheme = MotabilityScheme.builder().build();

  @Getter
  @Builder(toBuilder = true)
  public static class MotabilityScheme {
    @Default private String receiveMotabilityInformation = "No";
  }

  @Default
  private ResidenceAndPresence residenceAndPresence = ResidenceAndPresence.builder().build();

  @Getter
  @Builder(toBuilder = true)
  public static class ResidenceAndPresence {
    @Default private String nationality = "Dutch";
    @Default private String inUkTwoOutOfThreeYears = "Yes";
    @Default private String residentBeforeBrexit = "Yes";
    @Default private boolean receivingPensionsOrBenefitsFromEEA = true;
    @Default private boolean payingInsuranceEEA = true;
  }

  @Default private AboutYourHealth aboutYourHealth = AboutYourHealth.builder().build();

  @Getter
  @Builder(toBuilder = true)
  public static class AboutYourHealth {
    @Default
    private List<String> healthConditions = new ArrayList<>(Collections.singleton("Illness."));

    @Default private boolean hcpContactConsent = true;
    @Default private boolean hcpShareConsent = true;

    @Default
    private List<HealthProfessionalsDetails> healthProfessionalsDetails =
        Arrays.asList(
            HealthProfessionalsDetails.builder().build(),
            HealthProfessionalsDetails.builder().name("Dr Stevvve").build());

    @Getter
    @Builder(toBuilder = true)
    public static class HealthProfessionalsDetails {
      @Default private String name = "Dr Alazzz";
      @Default private String profession = "Doctor";
      @Default private String phoneNumber = "07777777777";
      @Default private Address address = Address.builder().build();

      @Getter
      @Builder(toBuilder = true)
      public static class Address {
        @Default private String line1 = "123";
        @Default private String line2 = "Headrow";
        @Default private String town = "Leeds";
        @Default private String county = "West Yorkshire";
        @Default private String postcode = "LS1 1AB";
        @Default private String country = "England";
      }

      @Default private String lastContact = "Last spoken to them on the last Fri of last month.";
    }

    @Default
    private HospitalHospiceOrCarehome hospitalHospiceOrCarehome =
        HospitalHospiceOrCarehome.builder().build();

    @Getter
    @Builder(toBuilder = true)
    public static class HospitalHospiceOrCarehome {
      @Default private String accommodationType = "other";
      @Default private String admissionDate = "2022-05-01";
      @Default private String accommodationName = "Hostel Accommodation";
      @Default private Address address = Address.builder().build();

      @Getter
      @Builder(toBuilder = true)
      public static class Address {
        @Default private String line1 = "123";
        @Default private String line2 = "Headrow";
        @Default private String town = "Leeds";
        @Default private String county = "West Yorkshire";
        @Default private String postcode = "LS1 1AB";
        @Default private String country = "England";
      }
    }
  }
}
