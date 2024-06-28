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
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v4.dto.PersonalDetailsDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static support.FileUtils.getNewRegistrationDataFromFile;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class PersonalDetailsMapperV4Test {
  
  @Mock
  private AlternateFormatMapperV4 alternateFormatMapperV4;
  private PersonalDetailsMapperV4 personalDetailsMapperV4;
  private RegistrationSchema140 registrationSchema;
  
  @BeforeEach
  void beforeEach() {
    personalDetailsMapperV4 =
      new PersonalDetailsMapperV4(
        new AddressMapperV4(new FormCommonsV2(new ObjectMapper())), alternateFormatMapperV4);
  }
  
  @Nested
  class FormToApiTest {
    
    @BeforeEach
    void beforeEach() throws IOException {
      registrationSchema = getNewRegistrationDataFromFile("mapping/newValidRegistrationData.json");
    }
    
    @Test
    void when_valid_data() {
      var personalDetails = registrationSchema.getPersonalDetails();
      
      var personalDetailsDto = personalDetailsMapperV4.toDto(personalDetails);
      
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
