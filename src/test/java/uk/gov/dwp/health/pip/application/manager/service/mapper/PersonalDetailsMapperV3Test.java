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
import uk.gov.dwp.health.pip.application.manager.model.registration.data.RegistrationSchema120;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v3.dto.PersonalDetailsDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static support.FileUtils.getRegistrationDataFromFile;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class PersonalDetailsMapperV3Test {

  @Mock
  private AlternateFormatMapperV3 alternateFormatMapperV3;
  private PersonalDetailsMapperV3 personalDetailsMapperV3;
  private RegistrationSchema120 registrationSchema;

  @BeforeEach
  void beforeEach() {
    personalDetailsMapperV3 =
        new PersonalDetailsMapperV3(
            new AddressMapperV3(new FormCommons(new ObjectMapper())), alternateFormatMapperV3);
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

      var personalDetailsDto = personalDetailsMapperV3.toDto(personalDetails);

      verifyPersonalDetails(personalDetailsDto);
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
