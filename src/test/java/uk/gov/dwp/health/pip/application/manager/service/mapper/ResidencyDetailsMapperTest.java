package uk.gov.dwp.health.pip.application.manager.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.health.pip.application.manager.exception.RegistrationDataNotValid;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.RegistrationSchema130;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.ResidenceAndPresenceSchema110.InUkTwoOutOfThreeYears;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.ResidenceAndPresenceSchema110.ResidentBeforeBrexit;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static support.FileUtils.getRegistrationDataFromFile;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Tag("unit")
class ResidencyDetailsMapperTest {

  private RegistrationSchema130 registrationSchema;
  private ResidencyDetailsMapper residencyDetailsMapper;

  @BeforeEach
  void beforeEach() throws IOException {
    residencyDetailsMapper = new ResidencyDetailsMapper();

    registrationSchema = getRegistrationDataFromFile("mapping/validRegistrationData.json");
  }

  @Test
  void when_new_nationality() {
    nationalitiesCanAllBeMappedToPipcsSchema("Filipino");
    nationalitiesCanAllBeMappedToPipcsSchema("Singaporean");
    nationalitiesCanAllBeMappedToPipcsSchema("Palestinian Territories");
    nationalitiesCanAllBeMappedToPipcsSchema("Kosovan");
    nationalitiesCanAllBeMappedToPipcsSchema("North Korean");
    nationalitiesCanAllBeMappedToPipcsSchema("Serbian");
  }

  private void nationalitiesCanAllBeMappedToPipcsSchema(final String nationality) {
    var residenceAndPresence = registrationSchema.getResidenceAndPresence();
    residenceAndPresence.setNationality(nationality);

    var residencyDetails = residencyDetailsMapper.mapResidencyDetails(registrationSchema);

    assertThat(residencyDetails.getNationality()).isEqualTo(nationality);
  }

  @Test
  void when_british_in_uk_two_of_three_years_yes() {
    var residenceAndPresence = registrationSchema.getResidenceAndPresence();
    residenceAndPresence.setNationality("British");
    residenceAndPresence.setResidentBeforeBrexit(null);
    residenceAndPresence.setInUkTwoOutOfThreeYears(InUkTwoOutOfThreeYears.YES);
    residenceAndPresence.setReceivingPensionsOrBenefitsFromEEA(Boolean.TRUE);
    residenceAndPresence.setPayingInsuranceEEA(Boolean.TRUE);

    var residencyDetails = residencyDetailsMapper.mapResidencyDetails(registrationSchema);

    assertThat(residencyDetails.getNationality()).isEqualTo("British");
    assertThat(residencyDetails.getOnOrBefore()).isNull();
    assertThat(residencyDetails.getInUkTwoOutOfThreeYears()).isEqualTo("Yes");
    assertThat(residencyDetails.getPensionsOrBenefitsFromEea()).isEqualTo("Yes");
    assertThat(residencyDetails.getWorkInEea()).isEqualTo("Yes");
  }

  @Test
  void when_british_in_uk_two_of_three_years_yes_not_receiving() {
    var residenceAndPresence = registrationSchema.getResidenceAndPresence();
    residenceAndPresence.setNationality("British");
    residenceAndPresence.setResidentBeforeBrexit(null);
    residenceAndPresence.setInUkTwoOutOfThreeYears(InUkTwoOutOfThreeYears.YES);
    residenceAndPresence.setReceivingPensionsOrBenefitsFromEEA(Boolean.FALSE);
    residenceAndPresence.setPayingInsuranceEEA(Boolean.TRUE);

    var residencyDetails = residencyDetailsMapper.mapResidencyDetails(registrationSchema);

    assertThat(residencyDetails.getNationality()).isEqualTo("British");
    assertThat(residencyDetails.getOnOrBefore()).isNull();
    assertThat(residencyDetails.getInUkTwoOutOfThreeYears()).isEqualTo("Yes");
    assertThat(residencyDetails.getPensionsOrBenefitsFromEea()).isEqualTo("No");
    assertThat(residencyDetails.getWorkInEea()).isEqualTo("Yes");
  }

  @Test
  void when_british_in_uk_two_of_three_years_no() {
    var residenceAndPresence = registrationSchema.getResidenceAndPresence();
    residenceAndPresence.setNationality("British");
    residenceAndPresence.setResidentBeforeBrexit(null);
    residenceAndPresence.setInUkTwoOutOfThreeYears(InUkTwoOutOfThreeYears.NO);
    residenceAndPresence.setReceivingPensionsOrBenefitsFromEEA(Boolean.TRUE);
    residenceAndPresence.setPayingInsuranceEEA(Boolean.TRUE);

    var residencyDetails = residencyDetailsMapper.mapResidencyDetails(registrationSchema);

    assertThat(residencyDetails.getNationality()).isEqualTo("British");
    assertThat(residencyDetails.getOnOrBefore()).isNull();
    assertThat(residencyDetails.getInUkTwoOutOfThreeYears()).isEqualTo("No");
    assertThat(residencyDetails.getPensionsOrBenefitsFromEea()).isEqualTo("Yes");
    assertThat(residencyDetails.getWorkInEea()).isEqualTo("Yes");
  }

  @Test
  void when_british_in_uk_two_of_three_years_dont_know() {
    var residenceAndPresence = registrationSchema.getResidenceAndPresence();
    residenceAndPresence.setNationality("British");
    residenceAndPresence.setResidentBeforeBrexit(null);
    residenceAndPresence.setInUkTwoOutOfThreeYears(InUkTwoOutOfThreeYears.DON_T_KNOW);
    residenceAndPresence.setReceivingPensionsOrBenefitsFromEEA(Boolean.TRUE);
    residenceAndPresence.setPayingInsuranceEEA(Boolean.TRUE);

    var residencyDetails = residencyDetailsMapper.mapResidencyDetails(registrationSchema);

    assertThat(residencyDetails.getNationality()).isEqualTo("British");
    assertThat(residencyDetails.getOnOrBefore()).isNull();
    assertThat(residencyDetails.getInUkTwoOutOfThreeYears()).isEqualTo("No");
    assertThat(residencyDetails.getPensionsOrBenefitsFromEea()).isEqualTo("Yes");
    assertThat(residencyDetails.getWorkInEea()).isEqualTo("Yes");
  }

  @Test
  void when_austrian_before_brexit_yes() {
    var residenceAndPresence = registrationSchema.getResidenceAndPresence();
    residenceAndPresence.setNationality("Austrian");
    residenceAndPresence.setResidentBeforeBrexit(ResidentBeforeBrexit.YES);
    residenceAndPresence.setInUkTwoOutOfThreeYears(InUkTwoOutOfThreeYears.YES);
    residenceAndPresence.setReceivingPensionsOrBenefitsFromEEA(Boolean.TRUE);
    residenceAndPresence.setPayingInsuranceEEA(Boolean.TRUE);

    var residencyDetails = residencyDetailsMapper.mapResidencyDetails(registrationSchema);

    assertThat(residencyDetails.getNationality()).isEqualTo("Austrian");
    assertThat(residencyDetails.getOnOrBefore()).isEqualTo("Yes");
    assertThat(residencyDetails.getInUkTwoOutOfThreeYears()).isEqualTo("Yes");
    assertThat(residencyDetails.getPensionsOrBenefitsFromEea()).isEqualTo("Yes");
    assertThat(residencyDetails.getWorkInEea()).isEqualTo("Yes");
  }

  @Test
  void when_austrian_before_brexit_no() {
    var residenceAndPresence = registrationSchema.getResidenceAndPresence();
    residenceAndPresence.setNationality("Austrian");
    residenceAndPresence.setResidentBeforeBrexit(ResidentBeforeBrexit.NO);
    residenceAndPresence.setInUkTwoOutOfThreeYears(InUkTwoOutOfThreeYears.YES);
    residenceAndPresence.setReceivingPensionsOrBenefitsFromEEA(Boolean.TRUE);
    residenceAndPresence.setPayingInsuranceEEA(Boolean.TRUE);

    var residencyDetails = residencyDetailsMapper.mapResidencyDetails(registrationSchema);

    assertThat(residencyDetails.getNationality()).isEqualTo("Austrian");
    assertThat(residencyDetails.getOnOrBefore()).isEqualTo("No");
    assertThat(residencyDetails.getInUkTwoOutOfThreeYears()).isEqualTo("Yes");
    assertThat(residencyDetails.getPensionsOrBenefitsFromEea()).isEqualTo("Yes");
    assertThat(residencyDetails.getWorkInEea()).isEqualTo("Yes");
  }

  @Test
  void when_austrian_before_brexit_dont_know() {
    var residenceAndPresence = registrationSchema.getResidenceAndPresence();
    residenceAndPresence.setNationality("Austrian");
    residenceAndPresence.setResidentBeforeBrexit(ResidentBeforeBrexit.DON_T_KNOW);
    residenceAndPresence.setInUkTwoOutOfThreeYears(InUkTwoOutOfThreeYears.YES);
    residenceAndPresence.setReceivingPensionsOrBenefitsFromEEA(Boolean.TRUE);
    residenceAndPresence.setPayingInsuranceEEA(Boolean.TRUE);

    var residencyDetails = residencyDetailsMapper.mapResidencyDetails(registrationSchema);

    assertThat(residencyDetails.getNationality()).isEqualTo("Austrian");
    assertThat(residencyDetails.getOnOrBefore()).isEqualTo("Don't Know");
    assertThat(residencyDetails.getInUkTwoOutOfThreeYears()).isEqualTo("Yes");
    assertThat(residencyDetails.getPensionsOrBenefitsFromEea()).isEqualTo("Yes");
    assertThat(residencyDetails.getWorkInEea()).isEqualTo("Yes");
  }

  @Test
  void when_non_eea() {
    var residenceAndPresence = registrationSchema.getResidenceAndPresence();
    residenceAndPresence.setNationality("Indian");
    residenceAndPresence.setResidentBeforeBrexit(null);
    residenceAndPresence.setInUkTwoOutOfThreeYears(InUkTwoOutOfThreeYears.YES);
    residenceAndPresence.setReceivingPensionsOrBenefitsFromEEA(null);
    residenceAndPresence.setPayingInsuranceEEA(null);

    var residencyDetails = residencyDetailsMapper.mapResidencyDetails(registrationSchema);

    assertThat(residencyDetails.getNationality()).isEqualTo("Indian");
    assertThat(residencyDetails.getOnOrBefore()).isNull();
    assertThat(residencyDetails.getInUkTwoOutOfThreeYears()).isEqualTo("Yes");
    assertThat(residencyDetails.getPensionsOrBenefitsFromEea()).isNull();
    assertThat(residencyDetails.getWorkInEea()).isNull();
  }

  @Test
  void when_only_british_nationality_present() {
    var residenceAndPresence = registrationSchema.getResidenceAndPresence();
    residenceAndPresence.setNationality("British");
    residenceAndPresence.setResidentBeforeBrexit(null);
    residenceAndPresence.setInUkTwoOutOfThreeYears(null);
    residenceAndPresence.setReceivingPensionsOrBenefitsFromEEA(null);
    residenceAndPresence.setPayingInsuranceEEA(null);

    var residencyDetails = residencyDetailsMapper.mapResidencyDetails(registrationSchema);

    assertThat(residencyDetails.getNationality()).isEqualTo("British");
    assertThat(residencyDetails.getOnOrBefore()).isNull();
    assertThat(residencyDetails.getInUkTwoOutOfThreeYears()).isEqualTo("No");
    assertThat(residencyDetails.getPensionsOrBenefitsFromEea()).isNull();
    assertThat(residencyDetails.getWorkInEea()).isNull();
  }

  @Test
  void when_only_non_eea_nationality_present() {
    var residenceAndPresence = registrationSchema.getResidenceAndPresence();
    residenceAndPresence.setNationality("Indian");
    residenceAndPresence.setResidentBeforeBrexit(null);
    residenceAndPresence.setInUkTwoOutOfThreeYears(null);
    residenceAndPresence.setReceivingPensionsOrBenefitsFromEEA(null);
    residenceAndPresence.setPayingInsuranceEEA(null);

    var residencyDetails = residencyDetailsMapper.mapResidencyDetails(registrationSchema);

    assertThat(residencyDetails.getNationality()).isEqualTo("Indian");
    assertThat(residencyDetails.getOnOrBefore()).isNull();
    assertThat(residencyDetails.getInUkTwoOutOfThreeYears()).isEqualTo("No");
    assertThat(residencyDetails.getPensionsOrBenefitsFromEea()).isNull();
    assertThat(residencyDetails.getWorkInEea()).isNull();
  }

  @Test
  void when_nationality_not_valid() {
    registrationSchema.getResidenceAndPresence().setNationality("India");

    assertThatThrownBy(() -> residencyDetailsMapper.mapResidencyDetails(registrationSchema))
        .isInstanceOf(RegistrationDataNotValid.class)
        .hasMessage("Registration data not valid. Problem when mapping nationality to legacy.");
  }
}
