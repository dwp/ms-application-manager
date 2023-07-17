package uk.gov.dwp.health.pip.application.manager.service.mapper;

import org.junit.jupiter.api.*;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.AdditionalSupport;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.RegistrationSchema100;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static support.FileUtils.getRegistrationDataFromFile;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Tag("unit")
class AdditionalSupportMapperV3Test {

  private AdditionalSupportMapperV3 additionalSupportMapperV3;
  private AdditionalSupport additionalSupport;

  @BeforeEach
  void beforeEach() throws IOException {
    additionalSupportMapperV3 = new AdditionalSupportMapperV3();

    RegistrationSchema100 registrationSchema =
        getRegistrationDataFromFile("mapping/validRegistrationData.json");

    additionalSupport = registrationSchema.getAdditionalSupport();
  }

  @Test
  void when_form_to_api() {
    additionalSupport.setHelpCommunicating(Boolean.TRUE);

    var additionalSupportDto = additionalSupportMapperV3.toDto(additionalSupport);

    assertThat(additionalSupportDto.isHelpCommunicating()).isTrue();
    assertThat(additionalSupportDto.isHelpUnderstandingLetters()).isTrue();
    assertThat(additionalSupportDto.getHelper().getFirstName())
        .isEqualTo("T7vQ1SD6YL153OmQWxuzdoskGVLCToeTest");
    assertThat(additionalSupportDto.getHelper().getSurname())
        .isEqualTo("T7vQ1SD6YL153OmQWxuzdoskGVLCToeTest");
  }

  @Test
  void when_helper_details_not_present() {
    additionalSupport.setHelperDetails(null);

    var additionalSupportDto = additionalSupportMapperV3.toDto(additionalSupport);

    assertThat(additionalSupportDto.isHelpCommunicating()).isTrue();
    assertThat(additionalSupportDto.isHelpUnderstandingLetters()).isTrue();
    assertThat(additionalSupportDto.getHelper()).isNull();
  }
}
