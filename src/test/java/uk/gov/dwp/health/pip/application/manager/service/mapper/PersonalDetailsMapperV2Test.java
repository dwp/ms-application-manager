package uk.gov.dwp.health.pip.application.manager.service.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.RegistrationSchema140;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v2.dto.AlternateFormatDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v2.dto.PersonalDetailsDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static support.FileUtils.getRegistrationDataFromFile;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class PersonalDetailsMapperV2Test {

  @Mock private AlternateFormatMapperV2 alternateFormatMapperV2;

  private PersonalDetailsMapperV2 personalDetailsMapperV2;
  private RegistrationSchema140 registrationSchema;

  @BeforeEach
  void beforeEach() {
    personalDetailsMapperV2 =
        new PersonalDetailsMapperV2(
            new AddressMapperV2(new FormCommons(new ObjectMapper())), alternateFormatMapperV2);
  }

  @Nested
  class FormToApiTest {

    @BeforeEach
    void beforeEach() throws IOException {
      registrationSchema = getRegistrationDataFromFile("mapping/validRegistrationData.json");
    }

    @Test
    void when_valid_data() {
      var personalDetails = registrationSchema.getPersonalDetails();
      var alternateFormatDto = new AlternateFormatDto();

      when(alternateFormatMapperV2.toDto(personalDetails.getAlternateFormat()))
          .thenReturn(alternateFormatDto);

      var personalDetailsDto = personalDetailsMapperV2.toDto(personalDetails);

      assertThat(personalDetailsDto.getAlternateFormat()).isEqualTo(alternateFormatDto);

      verifyPersonalDetails(personalDetailsDto);
    }

    @Test
    void when_sms_opt_false() {
      registrationSchema.getPersonalDetails().getContact().setSmsUpdates(Boolean.FALSE);
      var personalDetailsDto =
          personalDetailsMapperV2.toDto(registrationSchema.getPersonalDetails());

      assertThat(personalDetailsDto.getContact().getSmsUpdates()).isEqualTo("No");
    }

    @Test
    void when_sms_opt_null() {
      registrationSchema.getPersonalDetails().getContact().setSmsUpdates(null);
      var personalDetailsDto =
          personalDetailsMapperV2.toDto(registrationSchema.getPersonalDetails());

      assertThat(personalDetailsDto.getContact().getSmsUpdates()).isNull();
    }

    private void verifyPersonalDetails(PersonalDetailsDto personalDetailsDto) {
      assertThat(personalDetailsDto.getSurname()).isEqualTo("Azzzle");
      assertThat(personalDetailsDto.getFirstName())
          .isEqualTo("1SD6YL153OmQWxuzdoskGVLCToeRamaAzme");
      assertThat(personalDetailsDto.getDateOfBirth()).isEqualTo("2000-01-01");
      assertThat(personalDetailsDto.getNationalInsuranceNumber()).isEqualTo("RN000020D");

      var addressDto = personalDetailsDto.getAddress();
      assertThat(addressDto.getLine1()).isEqualTo("NQC2JhPWurvWRP1ZL7Goz2LKBFDufFjeP3o");
      assertThat(addressDto.getLine2()).isEqualTo("NQC2JhPWurvWRP1ZL7Goz2LKBFDufFjeP3o");
      assertThat(addressDto.getLine3()).isEqualTo("NQC2JhPWurvWRP1ZL7Goz2LKBFDufFjeP3o");
      assertThat(addressDto.getTown()).isEqualTo("NQC2JhPWurvWRP1ZL7Goz2LKBFDufFjeP3o");
      assertThat(addressDto.getCounty()).isEqualTo("QvBQYuxiXXVytGCxzVllpgTJKhRQq");
      assertThat(addressDto.getPostcode()).isEqualTo("AB11AB");
      assertThat(addressDto.getCountry()).isEqualTo("England");

      var alternativeAddress = personalDetailsDto.getAlternativeAddress();
      assertThat(alternativeAddress.getLine1()).isEqualTo("YdvDhtAsLghPXAgtbprXPZkhnfLTBSX");
      assertThat(alternativeAddress.getLine2()).isEqualTo("NQC2JhPWurvWRP1ZL7Goz2LKBFDufFjeP3o");
      assertThat(alternativeAddress.getLine3()).isEqualTo("NQC2JhPWurvWRP1ZL7Goz2LKBFDufFjeP3o");
      assertThat(alternativeAddress.getTown()).isEqualTo("NQC2JhPWurvWRP1ZL7Goz2LKBFDufFjeP3o");
      assertThat(alternativeAddress.getCounty()).isEqualTo("QvBQYuxiXXVytGCxzVllpgTJKhRQq");
      assertThat(alternativeAddress.getPostcode()).isEqualTo("AB1 1AB");
      assertThat(alternativeAddress.getCountry()).isEqualTo("England");

      var contactDto = personalDetailsDto.getContact();
      assertThat(contactDto.getMobileNumber()).isEqualTo("07777777777");
      assertThat(contactDto.getAlternativeNumber()).isEqualTo("01139999999");
      assertThat(contactDto.getTextPhone()).isEqualTo("01139999999");
      assertThat(contactDto.getSmsUpdates()).isEqualTo("Yes");
    }
  }
}
