package uk.gov.dwp.health.pip.application.manager.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.RegistrationSchema130;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.ResidenceAndPresenceSchema110;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static support.FileUtils.getRegistrationDataFromFile;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Tag("unit")
class ResidenceAndPresenceMapperV3Test {

  private ResidenceAndPresenceSchema110 residenceAndPresence;
  private ResidenceAndPresenceMapperV3 residenceAndPresenceMapperV3;

  @BeforeEach
  void beforeEach() throws IOException {
    residenceAndPresenceMapperV3 = new ResidenceAndPresenceMapperV3();

    RegistrationSchema130 registrationSchema =
        getRegistrationDataFromFile("mapping/validRegistrationData.json");

    residenceAndPresence = registrationSchema.getResidenceAndPresence();
  }

  @Test
  void when_british_in_uk_two_of_three_years_yes() {
    residenceAndPresence.setNationality("British");
    residenceAndPresence.setResidentBeforeBrexit(null);
    residenceAndPresence.setInUkTwoOutOfThreeYears(ResidenceAndPresenceSchema110.InUkTwoOutOfThreeYears.YES);
    residenceAndPresence.setReceivingPensionsOrBenefitsFromEEA(Boolean.TRUE);
    residenceAndPresence.setPayingInsuranceEEA(Boolean.TRUE);

    var residenceAndPresenceDto = residenceAndPresenceMapperV3.toDto(residenceAndPresence);

    assertThat(residenceAndPresenceDto.getNationality()).isEqualTo("British");
    assertThat(residenceAndPresenceDto.getResidentBeforeBrexit()).isNull();
    assertThat(residenceAndPresenceDto.getInUkTwoOutOfThreeYears()).isEqualTo("Yes");
    assertThat(residenceAndPresenceDto.isReceivingPensionsOrBenefitsFromEEA()).isTrue();
    assertThat(residenceAndPresenceDto.isPayingInsuranceEEA()).isTrue();
  }

  @Test
  void when_british_in_uk_two_of_three_years_yes_not_receiving() {
    residenceAndPresence.setNationality("British");
    residenceAndPresence.setResidentBeforeBrexit(null);
    residenceAndPresence.setInUkTwoOutOfThreeYears(ResidenceAndPresenceSchema110.InUkTwoOutOfThreeYears.YES);
    residenceAndPresence.setReceivingPensionsOrBenefitsFromEEA(Boolean.FALSE);
    residenceAndPresence.setPayingInsuranceEEA(Boolean.TRUE);

    var residenceAndPresenceDto = residenceAndPresenceMapperV3.toDto(residenceAndPresence);

    assertThat(residenceAndPresenceDto.getNationality()).isEqualTo("British");
    assertThat(residenceAndPresenceDto.getResidentBeforeBrexit()).isNull();
    assertThat(residenceAndPresenceDto.getInUkTwoOutOfThreeYears()).isEqualTo("Yes");
    assertThat(residenceAndPresenceDto.isReceivingPensionsOrBenefitsFromEEA()).isFalse();
    assertThat(residenceAndPresenceDto.isPayingInsuranceEEA()).isTrue();
  }

  @Test
  void when_british_in_uk_two_of_three_years_no() {
    residenceAndPresence.setNationality("British");
    residenceAndPresence.setResidentBeforeBrexit(null);
    residenceAndPresence.setInUkTwoOutOfThreeYears(ResidenceAndPresenceSchema110.InUkTwoOutOfThreeYears.NO);
    residenceAndPresence.setReceivingPensionsOrBenefitsFromEEA(Boolean.TRUE);
    residenceAndPresence.setPayingInsuranceEEA(Boolean.TRUE);

    var residenceAndPresenceDto = residenceAndPresenceMapperV3.toDto(residenceAndPresence);

    assertThat(residenceAndPresenceDto.getNationality()).isEqualTo("British");
    assertThat(residenceAndPresenceDto.getResidentBeforeBrexit()).isNull();
    assertThat(residenceAndPresenceDto.getInUkTwoOutOfThreeYears()).isEqualTo("No");
    assertThat(residenceAndPresenceDto.isReceivingPensionsOrBenefitsFromEEA()).isTrue();
    assertThat(residenceAndPresenceDto.isPayingInsuranceEEA()).isTrue();
  }

  @Test
  void when_british_in_uk_two_of_three_years_dont_know() {
    residenceAndPresence.setNationality("British");
    residenceAndPresence.setResidentBeforeBrexit(null);
    residenceAndPresence.setInUkTwoOutOfThreeYears(
        ResidenceAndPresenceSchema110.InUkTwoOutOfThreeYears.DON_T_KNOW);
    residenceAndPresence.setReceivingPensionsOrBenefitsFromEEA(Boolean.TRUE);
    residenceAndPresence.setPayingInsuranceEEA(Boolean.TRUE);

    var residenceAndPresenceDto = residenceAndPresenceMapperV3.toDto(residenceAndPresence);

    assertThat(residenceAndPresenceDto.getNationality()).isEqualTo("British");
    assertThat(residenceAndPresenceDto.getResidentBeforeBrexit()).isNull();
    assertThat(residenceAndPresenceDto.getInUkTwoOutOfThreeYears()).isEqualTo("Don't know");
    assertThat(residenceAndPresenceDto.isReceivingPensionsOrBenefitsFromEEA()).isTrue();
    assertThat(residenceAndPresenceDto.isPayingInsuranceEEA()).isTrue();
  }

  @Test
  void when_austrian_before_brexit_yes() {
    residenceAndPresence.setNationality("Austrian");
    residenceAndPresence.setResidentBeforeBrexit(ResidenceAndPresenceSchema110.ResidentBeforeBrexit.YES);
    residenceAndPresence.setInUkTwoOutOfThreeYears(ResidenceAndPresenceSchema110.InUkTwoOutOfThreeYears.YES);
    residenceAndPresence.setReceivingPensionsOrBenefitsFromEEA(Boolean.TRUE);
    residenceAndPresence.setPayingInsuranceEEA(Boolean.TRUE);

    var residenceAndPresenceDto = residenceAndPresenceMapperV3.toDto(residenceAndPresence);

    assertThat(residenceAndPresenceDto.getNationality()).isEqualTo("Austrian");
    assertThat(residenceAndPresenceDto.getResidentBeforeBrexit()).isEqualTo("Yes");
    assertThat(residenceAndPresenceDto.getInUkTwoOutOfThreeYears()).isEqualTo("Yes");
    assertThat(residenceAndPresenceDto.isReceivingPensionsOrBenefitsFromEEA()).isTrue();
    assertThat(residenceAndPresenceDto.isPayingInsuranceEEA()).isTrue();
  }

  @Test
  void when_austrian_before_brexit_no() {
    residenceAndPresence.setNationality("Austrian");
    residenceAndPresence.setResidentBeforeBrexit(ResidenceAndPresenceSchema110.ResidentBeforeBrexit.NO);
    residenceAndPresence.setInUkTwoOutOfThreeYears(ResidenceAndPresenceSchema110.InUkTwoOutOfThreeYears.YES);
    residenceAndPresence.setReceivingPensionsOrBenefitsFromEEA(Boolean.TRUE);
    residenceAndPresence.setPayingInsuranceEEA(Boolean.TRUE);

    var residenceAndPresenceDto = residenceAndPresenceMapperV3.toDto(residenceAndPresence);

    assertThat(residenceAndPresenceDto.getNationality()).isEqualTo("Austrian");
    assertThat(residenceAndPresenceDto.getResidentBeforeBrexit()).isEqualTo("No");
    assertThat(residenceAndPresenceDto.getInUkTwoOutOfThreeYears()).isEqualTo("Yes");
    assertThat(residenceAndPresenceDto.isReceivingPensionsOrBenefitsFromEEA()).isTrue();
    assertThat(residenceAndPresenceDto.isPayingInsuranceEEA()).isTrue();
  }

  @Test
  void when_austrian_before_brexit_dont_know() {
    residenceAndPresence.setNationality("Austrian");
    residenceAndPresence.setResidentBeforeBrexit(
        ResidenceAndPresenceSchema110.ResidentBeforeBrexit.DON_T_KNOW);
    residenceAndPresence.setInUkTwoOutOfThreeYears(ResidenceAndPresenceSchema110.InUkTwoOutOfThreeYears.YES);
    residenceAndPresence.setReceivingPensionsOrBenefitsFromEEA(Boolean.TRUE);
    residenceAndPresence.setPayingInsuranceEEA(Boolean.TRUE);

    var residenceAndPresenceDto = residenceAndPresenceMapperV3.toDto(residenceAndPresence);

    assertThat(residenceAndPresenceDto.getNationality()).isEqualTo("Austrian");
    assertThat(residenceAndPresenceDto.getResidentBeforeBrexit()).isEqualTo("Don't know");
    assertThat(residenceAndPresenceDto.getInUkTwoOutOfThreeYears()).isEqualTo("Yes");
    assertThat(residenceAndPresenceDto.isReceivingPensionsOrBenefitsFromEEA()).isTrue();
    assertThat(residenceAndPresenceDto.isPayingInsuranceEEA()).isTrue();
  }

  @Test
  void when_non_eea() {
    residenceAndPresence.setNationality("Indian");
    residenceAndPresence.setResidentBeforeBrexit(null);
    residenceAndPresence.setInUkTwoOutOfThreeYears(ResidenceAndPresenceSchema110.InUkTwoOutOfThreeYears.YES);
    residenceAndPresence.setReceivingPensionsOrBenefitsFromEEA(null);
    residenceAndPresence.setPayingInsuranceEEA(null);

    var residenceAndPresenceDto = residenceAndPresenceMapperV3.toDto(residenceAndPresence);

    assertThat(residenceAndPresenceDto.getNationality()).isEqualTo("Indian");
    assertThat(residenceAndPresenceDto.getResidentBeforeBrexit()).isNull();
    assertThat(residenceAndPresenceDto.getInUkTwoOutOfThreeYears()).isEqualTo("Yes");
    assertThat(residenceAndPresenceDto.isReceivingPensionsOrBenefitsFromEEA()).isNull();
    assertThat(residenceAndPresenceDto.isPayingInsuranceEEA()).isNull();
  }

  @Test
  void when_only_british_nationality_present() {
    residenceAndPresence.setNationality("British");
    residenceAndPresence.setResidentBeforeBrexit(null);
    residenceAndPresence.setInUkTwoOutOfThreeYears(null);
    residenceAndPresence.setReceivingPensionsOrBenefitsFromEEA(null);
    residenceAndPresence.setPayingInsuranceEEA(null);

    var residenceAndPresenceDto = residenceAndPresenceMapperV3.toDto(residenceAndPresence);

    assertThat(residenceAndPresenceDto.getNationality()).isEqualTo("British");
    assertThat(residenceAndPresenceDto.getResidentBeforeBrexit()).isNull();
    assertThat(residenceAndPresenceDto.getInUkTwoOutOfThreeYears()).isNull();
    assertThat(residenceAndPresenceDto.isReceivingPensionsOrBenefitsFromEEA()).isNull();
    assertThat(residenceAndPresenceDto.isPayingInsuranceEEA()).isNull();
  }

  @Test
  void when_only_non_eea_nationality_present() {
    residenceAndPresence.setNationality("Indian");
    residenceAndPresence.setResidentBeforeBrexit(null);
    residenceAndPresence.setInUkTwoOutOfThreeYears(null);
    residenceAndPresence.setReceivingPensionsOrBenefitsFromEEA(null);
    residenceAndPresence.setPayingInsuranceEEA(null);

    var residenceAndPresenceDto = residenceAndPresenceMapperV3.toDto(residenceAndPresence);

    assertThat(residenceAndPresenceDto.getNationality()).isEqualTo("Indian");
    assertThat(residenceAndPresenceDto.getResidentBeforeBrexit()).isNull();
    assertThat(residenceAndPresenceDto.getInUkTwoOutOfThreeYears()).isNull();
    assertThat(residenceAndPresenceDto.isReceivingPensionsOrBenefitsFromEEA()).isNull();
    assertThat(residenceAndPresenceDto.isPayingInsuranceEEA()).isNull();
  }
}
