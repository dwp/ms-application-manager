package uk.gov.dwp.health.pip.application.manager.service.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import support.FileUtils;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.AlternateFormat110;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.AlternateFormat110.FormatType;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.HelperDetails100;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.RegistrationSchema120;
import uk.gov.dwp.health.pip.pipcsapimodeller.Pip1RegistrationForm;
import uk.gov.dwp.health.pip.pipcsapimodeller.registration.type.YesNoDontKnow;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class RegistrationDataMapperForPipcsTest {

  private static final String APPLICATION_ID = "application-id-1";
  private static final LocalDate DATE_REGISTRATION_SUBMITTED = LocalDate.now();

  private RegistrationDataMapperForPipcs registrationDataMapper;
  private RegistrationSchema120 registrationSchema;

  @Mock private HospitalAndOtherAccomDetailsMapper hospitalAndOtherAccomDetailsMapper;
  @Mock private ResidencyDetailsMapper residencyDetailsMapper;

  @Mock private PipcsApiModelValidator pipcsApiModelValidator;

  @BeforeEach
  void beforeEach() {
    registrationDataMapper =
        new RegistrationDataMapperForPipcs(
            hospitalAndOtherAccomDetailsMapper,
            new PostcodeMapper(),
            residencyDetailsMapper,
            pipcsApiModelValidator);
  }

  @Nested
  class RegistrationFormToLegacy {

    @BeforeEach
    void beforeEach() throws IOException {
      Map<String, Object> registrationDataJson =
          FileUtils.readTestFile("mapping/validRegistrationData.json");
      registrationSchema = parseJson(registrationDataJson);
    }

    @Test
    void when_mapping_to_legacy() {
      Pip1RegistrationForm pip1RegistrationForm =
          registrationDataMapper.mapRegistrationData(
              APPLICATION_ID, DATE_REGISTRATION_SUBMITTED, registrationSchema);

      assertThat(pip1RegistrationForm.getPipApplyApplicationId()).isEqualTo(APPLICATION_ID);
      assertThat(pip1RegistrationForm.getDateClaimSubmitted()).isEqualTo(LocalDate.now());
      assertThat(pip1RegistrationForm.getAltFormatRequired()).isEqualTo(YesNoDontKnow.NO.toString());
      assertThat(pip1RegistrationForm.getBankDetails().getAccountName()).isEqualTo("Bank of Yorkshire");
      assertThat(pip1RegistrationForm.getBankDetails().getAccountNumber()).isEqualTo("12341234");
      assertThat(pip1RegistrationForm.getBankDetails().getSortCode()).isEqualTo("123123");
      assertThat(pip1RegistrationForm.getBankDetails().getBuildingSocietyRollNumber()).isEqualTo("123123123");
      assertThat(pip1RegistrationForm.getMotabilitySchemeInfo()).isEqualTo("No");
    }

    @Test
    void when_bank_details_not_present() {
      registrationSchema.getPersonalDetails().getBankDetails().setEnterBankDetails("No");
      Pip1RegistrationForm pip1RegistrationForm =
          registrationDataMapper.mapRegistrationData(
              APPLICATION_ID, DATE_REGISTRATION_SUBMITTED, registrationSchema);

      assertThat(pip1RegistrationForm.getBankDetails()).isNull();
    }

    @Test
    void when_motability_details_not_present() {
      registrationSchema.setMotabilityScheme(null);
      Pip1RegistrationForm pip1RegistrationForm =
          registrationDataMapper.mapRegistrationData(
              APPLICATION_ID, DATE_REGISTRATION_SUBMITTED, registrationSchema);
      assertThat(pip1RegistrationForm.getMotabilitySchemeInfo()).isNull();
    }

    @Test
    void when_date_not_valid() {
      registrationSchema.getPersonalDetails().setDob("01-01-2001");

      assertThatThrownBy(
              () ->
                  registrationDataMapper.mapRegistrationData(
                      APPLICATION_ID, DATE_REGISTRATION_SUBMITTED, registrationSchema))
          .isInstanceOf(DateTimeParseException.class);
    }

    @Test
    void when_help_communicating_true() {
      registrationSchema.getAdditionalSupport().setHelpCommunicating(Boolean.TRUE);

      Pip1RegistrationForm pip1RegistrationForm =
          registrationDataMapper.mapRegistrationData(
              APPLICATION_ID, DATE_REGISTRATION_SUBMITTED, registrationSchema);

      assertThat(pip1RegistrationForm.getDifficultyCommunicating()).isEqualTo("Yes");
    }

    @Test
    void when_help_communicating_false() {
      registrationSchema.getAdditionalSupport().setHelpCommunicating(Boolean.FALSE);

      Pip1RegistrationForm pip1RegistrationForm =
          registrationDataMapper.mapRegistrationData(
              APPLICATION_ID, DATE_REGISTRATION_SUBMITTED, registrationSchema);

      assertThat(pip1RegistrationForm.getDifficultyCommunicating()).isEqualTo("No");
    }

    @Test
    void when_helper_details_present() {
      var helperDetails = new HelperDetails100();
      helperDetails.setFirstname("Azzzam");
      helperDetails.setSurname("Azzzle");
      registrationSchema.getAdditionalSupport().setHelperDetails(helperDetails);

      Pip1RegistrationForm pip1RegistrationForm =
          registrationDataMapper.mapRegistrationData(
              APPLICATION_ID, DATE_REGISTRATION_SUBMITTED, registrationSchema);

      assertThat(pip1RegistrationForm.getHelpCompletingLetters()).isEqualTo("Yes");
      assertThat(pip1RegistrationForm.getHelpCompletingLetterWho()).isEqualTo("Azzzam Azzzle");
    }

    @Test
    void when_helper_details_not_present() {
      registrationSchema.getAdditionalSupport().setHelperDetails(null);

      Pip1RegistrationForm pip1RegistrationForm =
          registrationDataMapper.mapRegistrationData(
              APPLICATION_ID, DATE_REGISTRATION_SUBMITTED, registrationSchema);

      assertThat(pip1RegistrationForm.getHelpCompletingLetters()).isEqualTo("No");
      assertThat(pip1RegistrationForm.getHelpCompletingLetterWho()).isNull();
    }

    @Test
    void when_sms_opt_false_map_to_pipcs_yes_answer() {
      registrationSchema.getPersonalDetails().getContact().setSmsUpdates(Boolean.FALSE);
      Pip1RegistrationForm pip1RegistrationForm =
          registrationDataMapper.mapRegistrationData(
              APPLICATION_ID, DATE_REGISTRATION_SUBMITTED, registrationSchema);
      assertThat(pip1RegistrationForm.getPersonalDetails().getContactDetails().getSmsOptOut())
          .isEqualTo("Yes");
    }

    @Test
    void when_sms_opt_true_map_to_pipcs_no_answer() {
      registrationSchema.getPersonalDetails().getContact().setSmsUpdates(Boolean.TRUE);
      Pip1RegistrationForm pip1RegistrationForm =
          registrationDataMapper.mapRegistrationData(
              APPLICATION_ID, DATE_REGISTRATION_SUBMITTED, registrationSchema);
      assertThat(pip1RegistrationForm.getPersonalDetails().getContactDetails().getSmsOptOut())
          .isEqualTo("No");
    }

    @Test
    void when_sms_opt_null_set_pipcs_sms_opt_null() {
      registrationSchema.getPersonalDetails().getContact().setSmsUpdates(null);
      Pip1RegistrationForm pip1RegistrationForm =
          registrationDataMapper.mapRegistrationData(
              APPLICATION_ID, DATE_REGISTRATION_SUBMITTED, registrationSchema);
      assertThat(pip1RegistrationForm.getPersonalDetails().getContactDetails().getSmsOptOut())
          .isNull();
    }
  }

  @Nested
  class AlternativeFormatTest {

    @BeforeEach
    void beforeEach() throws IOException {
      Map<String, Object> registrationDataJson =
          FileUtils.readTestFile("mapping/alternateFormatBrailleType1.json");
      registrationSchema = parseJson(registrationDataJson);
    }

    @Test
    void when_alternative_format_braille_type_1() {
      Pip1RegistrationForm pip1RegistrationForm =
          registrationDataMapper.mapRegistrationData(
              APPLICATION_ID, DATE_REGISTRATION_SUBMITTED, registrationSchema);

      assertThat(pip1RegistrationForm.getAltFormatRequired())
          .isEqualTo(YesNoDontKnow.YES.toString());
      assertThat(pip1RegistrationForm.getAltFormatType()).isEqualTo("Braille type 1");
    }

    @Test
    void when_alternative_format_braille_type_2() {
      setUpAlternativeFormatAdditionalProperties(
          FormatType.BRAILLE, "brailleOptions", "type2Contracted");

      Pip1RegistrationForm pip1RegistrationForm =
          registrationDataMapper.mapRegistrationData(
              APPLICATION_ID, DATE_REGISTRATION_SUBMITTED, registrationSchema);

      assertThat(pip1RegistrationForm.getAltFormatRequired())
          .isEqualTo(YesNoDontKnow.YES.toString());
      assertThat(pip1RegistrationForm.getAltFormatType()).isEqualTo("Braille type 2");
    }

    @ParameterizedTest
    @CsvSource({
      "britishDvd, British sign language DVD",
      "britishMpeg, British sign language Mpeg",
      "irishDvd, Irish sign language DVD",
      "irishMpeg, Irish sign language Mpeg"
    })
    void when_alternative_format_sign_language(String formValue, String legacyValue) {
      setUpAlternativeFormatAdditionalProperties(
          FormatType.SIGN_LANGUAGE, "signLanguageOptions", formValue);

      Pip1RegistrationForm pip1RegistrationForm =
          registrationDataMapper.mapRegistrationData(
              APPLICATION_ID, DATE_REGISTRATION_SUBMITTED, registrationSchema);

      assertThat(pip1RegistrationForm.getAltFormatRequired())
          .isEqualTo(YesNoDontKnow.YES.toString());
      assertThat(pip1RegistrationForm.getAltFormatType()).isEqualTo(legacyValue);
    }

    @ParameterizedTest
    @CsvSource({
      "cassette, Audio cassette tape",
      "cd, Audio CD",
      "dvd, Audio DVD",
      "mp3, Audio MP3"
    })
    void when_alternative_format_audio(String formValue, String legacyValue) {
      setUpAlternativeFormatAdditionalProperties(
          FormatType.AUDIO, "audioOptions", formValue);

      Pip1RegistrationForm pip1RegistrationForm =
          registrationDataMapper.mapRegistrationData(
              APPLICATION_ID, DATE_REGISTRATION_SUBMITTED, registrationSchema);

      assertThat(pip1RegistrationForm.getAltFormatRequired())
          .isEqualTo(YesNoDontKnow.YES.toString());
      assertThat(pip1RegistrationForm.getAltFormatType()).isEqualTo(legacyValue);
    }
  }

  @Nested
  class AlternativeFormatOtherTest {

    @BeforeEach
    void beforeEach() throws IOException {
      Map<String, Object> registrationDataJson =
          FileUtils.readTestFile("mapping/alternateFormatOther.json");
      registrationSchema = parseJson(registrationDataJson);
    }

    @Test
    void when_coloured_paper_details() {
      Pip1RegistrationForm pip1RegistrationForm =
          registrationDataMapper.mapRegistrationData(
              APPLICATION_ID, DATE_REGISTRATION_SUBMITTED, registrationSchema);

      assertThat(pip1RegistrationForm.getAltFormatRequired())
          .isEqualTo(YesNoDontKnow.YES.toString());
      assertThat(pip1RegistrationForm.getAltFormatType()).isEqualTo("Coloured paper");
      assertThat(pip1RegistrationForm.getAltFormatAdditionalInfo())
          .isEqualTo("alt-format-additional-info");
    }

    @Test
    void when_large_print_16_point_font() {
      setUpAlternativeFormatAdditionalProperties(
          FormatType.OTHER, "otherOptions", "largePrint16Font");

      Pip1RegistrationForm pip1RegistrationForm =
          registrationDataMapper.mapRegistrationData(
              APPLICATION_ID, DATE_REGISTRATION_SUBMITTED, registrationSchema);

      assertThat(pip1RegistrationForm.getAltFormatRequired())
          .isEqualTo(YesNoDontKnow.YES.toString());
      assertThat(pip1RegistrationForm.getAltFormatType()).isEqualTo("Large print");
      assertThat(pip1RegistrationForm.getAltFormatAdditionalInfo()).isNull();
    }

    @ParameterizedTest
    @CsvSource({
      "largePrintCustomFont, Large print Custom font",
      "email, E-mail",
      "other, Other Alternative Format"
    })
    void when_alternate_format_other(String formValue, String legacyValue) {
      setUpAlternativeFormatAdditionalProperties(
          FormatType.OTHER, "otherOptions", formValue);

      registrationSchema
          .getPersonalDetails()
          .getAlternateFormat()
          .setAdditionalProperty("alternateFormatAdditionalInfo", "alt-format-additional-info");

      Pip1RegistrationForm pip1RegistrationForm =
          registrationDataMapper.mapRegistrationData(
              APPLICATION_ID, DATE_REGISTRATION_SUBMITTED, registrationSchema);

      assertThat(pip1RegistrationForm.getAltFormatRequired())
          .isEqualTo(YesNoDontKnow.YES.toString());
      assertThat(pip1RegistrationForm.getAltFormatType()).isEqualTo(legacyValue);
      assertThat(pip1RegistrationForm.getAltFormatAdditionalInfo())
          .isEqualTo("alt-format-additional-info");
    }

    @Test
    void when_accessible_pdf() {
      setUpAlternativeFormatAdditionalProperties(
          FormatType.OTHER, "otherOptions", "accessiblePDF");

      Pip1RegistrationForm pip1RegistrationForm =
          registrationDataMapper.mapRegistrationData(
              APPLICATION_ID, DATE_REGISTRATION_SUBMITTED, registrationSchema);

      assertThat(pip1RegistrationForm.getAltFormatRequired())
          .isEqualTo(YesNoDontKnow.YES.toString());
      assertThat(pip1RegistrationForm.getAltFormatType()).isEqualTo("Web accessible PDF");
      assertThat(pip1RegistrationForm.getAltFormatAdditionalInfo()).isNull();
    }

    @Test
    void when_incomplete() {
      AlternateFormat110 alternateFormat =
          registrationSchema.getPersonalDetails().getAlternateFormat();
      alternateFormat.setFormatType(FormatType.OTHER);
      alternateFormat.getAdditionalProperties().clear();

      Pip1RegistrationForm pip1RegistrationForm =
          registrationDataMapper.mapRegistrationData(
              APPLICATION_ID, DATE_REGISTRATION_SUBMITTED, registrationSchema);

      assertThat(pip1RegistrationForm.getAltFormatRequired())
          .isEqualTo(YesNoDontKnow.YES.toString());
      assertThat(pip1RegistrationForm.getAltFormatType()).isNull();
      assertThat(pip1RegistrationForm.getAltFormatAdditionalInfo()).isNull();
    }
  }

  @Nested
  class OptionalCorrespondenceAddressTest {

    @BeforeEach
    void beforeEach() throws IOException {
      final var registrationDataJson =
          FileUtils.readTestFile("mapping/optionalCorrespondence.json");
      registrationSchema = parseJson(registrationDataJson);
    }

    @Test
    void when_alternative_address_missing_pipcs_api_correspondence_address_is_null() {
      var registrationForm =
          registrationDataMapper.mapRegistrationData(
              APPLICATION_ID, DATE_REGISTRATION_SUBMITTED, registrationSchema);
      assertThat(registrationForm.getPersonalDetails().getCorrespondence()).isNull();
    }
  }

  @Nested
  class OptionalHelperDetailsTest {
    @BeforeEach
    void beforeEach() throws IOException {
      final var registrationDataJson = FileUtils.readTestFile("mapping/optionalHelperDetails.json");
      registrationSchema = parseJson(registrationDataJson);
    }

    @Test
    void when_helper_details_missing_pipcs_api_help_completing_letters_who_null() {
      var registrationForm =
          registrationDataMapper.mapRegistrationData(
              APPLICATION_ID, DATE_REGISTRATION_SUBMITTED, registrationSchema);
      assertThat(registrationForm.getHelpCompletingLetterWho()).isNull();
    }
  }

  @Nested
  class HealthProfessionalTest {

    @Test
    void when_consent_are_true_only_one_health_professional_details_mapped() throws IOException {
      var pip1RegistrationForm =
          readRegistrationFormFixtureFromFile("mapping/healthProfessionalConsentGiven.json");

      assertThat(pip1RegistrationForm.getHealthDetails().getConsent()).isEqualTo("Yes");
      var healthProfessional1 = pip1RegistrationForm.getHealthDetails().getHp1();
      assertThat(healthProfessional1).isNotNull();
      assertThat(healthProfessional1.getFullName()).isEqualTo("HP-DR-1");
      var healthProfessional2 = pip1RegistrationForm.getHealthDetails().getHp2();
      assertThat(healthProfessional2).isNotNull();
      assertThat(healthProfessional2.getFullName()).isEqualTo("HP-DR-2");
    }

    @Test
    void when_shared_consent_false_given_health_professional_details_not_required()
        throws IOException {
      var actual =
          readRegistrationFormFixtureFromFile("mapping/healthProfessionalConsentDecline.json");
      assertThat(actual.getHealthDetails().getConsent()).isEqualTo("No");
      assertThat(actual.getHealthDetails().getHp1()).isNull();
      assertThat(actual.getHealthDetails().getHp2()).isNull();
      assertThat(actual.getHealthDetails().getCondition()).isNotEmpty();
    }

    private Pip1RegistrationForm readRegistrationFormFixtureFromFile(String file)
        throws IOException {
      final var registrationDataJson = FileUtils.readTestFile(file);
      registrationSchema = parseJson(registrationDataJson);
      return registrationDataMapper.mapRegistrationData(
          APPLICATION_ID, DATE_REGISTRATION_SUBMITTED, registrationSchema);
    }
  }

  private void setUpAlternativeFormatAdditionalProperties(
      AlternateFormat110.FormatType type, String key, String value) {
    AlternateFormat110 alternateFormat = registrationSchema.getPersonalDetails().getAlternateFormat();
    alternateFormat.getAdditionalProperties().clear();
    alternateFormat.setFormatType(type);
    alternateFormat.setAdditionalProperty(key, value);
  }

  private RegistrationSchema120 parseJson(Map<String, Object> registrationDataJson) {
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.convertValue(registrationDataJson, RegistrationSchema120.class);
  }
}
