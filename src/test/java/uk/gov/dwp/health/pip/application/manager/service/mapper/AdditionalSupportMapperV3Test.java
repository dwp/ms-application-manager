package uk.gov.dwp.health.pip.application.manager.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.AdditionalSupportSchema100;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.RegistrationSchema140;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static support.FileUtils.getRegistrationDataFromFile;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Tag("unit")
class AdditionalSupportMapperV3Test {

  private AdditionalSupportMapperV3 additionalSupportMapperV3;
  private AdditionalSupportSchema100 additionalSupport;

  @BeforeEach
  void beforeEach() throws IOException {
    additionalSupportMapperV3 = new AdditionalSupportMapperV3();
    
    RegistrationSchema140 registrationSchema =
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
