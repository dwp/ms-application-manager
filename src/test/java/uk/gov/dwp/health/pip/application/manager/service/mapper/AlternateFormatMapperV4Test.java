package uk.gov.dwp.health.pip.application.manager.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.AlternateFormat110;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Tag("unit")
class AlternateFormatMapperV4Test {
  
  private AlternateFormat110 alternateFormat;
  private AlternateFormatMapperV4 alternateFormatMapperV4;
  
  @BeforeEach
  void beforeEach() {
    alternateFormat = new AlternateFormat110();
    alternateFormatMapperV4 = new AlternateFormatMapperV4();
  }
  
  @ParameterizedTest
  @CsvSource({"type1Uncontracted, type1Uncontracted", "type2Contracted, type2Contracted"})
  void when_braille(String formValue, String dtoValue) {
    alternateFormat.setFormatType(AlternateFormat110.FormatType.BRAILLE);
    alternateFormat.setAdditionalProperty("brailleOptions", formValue);
    
    var alternateFormatDto = alternateFormatMapperV4.toDto(alternateFormat);
    
    assertThat(alternateFormatDto.getFormatType().getValue()).isEqualTo("braille");
    assertThat(alternateFormatDto.getOption().getValue()).isEqualTo(dtoValue);
    assertThat(alternateFormatDto.getAdditionalInfo()).isNull();
  }
  
  @ParameterizedTest
  @CsvSource({
    "britishDvd, britishDvd",
    "britishMpeg, britishMpeg",
    "irishDvd, irishDvd",
    "irishMpeg, irishMpeg"
  })
  void when_sign_language(String formValue, String dtoValue) {
    alternateFormat.setFormatType(AlternateFormat110.FormatType.SIGN_LANGUAGE);
    alternateFormat.setAdditionalProperty("signLanguageOptions", formValue);
    
    var alternateFormatDto = alternateFormatMapperV4.toDto(alternateFormat);
    
    assertThat(alternateFormatDto.getFormatType().getValue()).isEqualTo("signLanguage");
    assertThat(alternateFormatDto.getOption().getValue()).isEqualTo(dtoValue);
    assertThat(alternateFormatDto.getAdditionalInfo()).isNull();
  }
  
  @ParameterizedTest
  @CsvSource({"cassette, cassette", "cd, cd", "dvd, dvd", "mp3, mp3"})
  void when_audio(String formValue, String dtoValue) {
    alternateFormat.setFormatType(AlternateFormat110.FormatType.AUDIO);
    alternateFormat.setAdditionalProperty("audioOptions", formValue);
    
    var alternateFormatDto = alternateFormatMapperV4.toDto(alternateFormat);
    
    assertThat(alternateFormatDto.getFormatType().getValue()).isEqualTo("audio");
    assertThat(alternateFormatDto.getOption().getValue()).isEqualTo(dtoValue);
    assertThat(alternateFormatDto.getAdditionalInfo()).isNull();
  }
  
  @ParameterizedTest
  @CsvSource({"largePrint16Font, largePrint16Font", "accessiblePDF, accessiblePDF"})
  void when_other(String formValue, String dtoValue) {
    alternateFormat.setFormatType(AlternateFormat110.FormatType.OTHER);
    alternateFormat.setAdditionalProperty("otherOptions", formValue);
    
    var alternateFormatDto = alternateFormatMapperV4.toDto(alternateFormat);
    
    assertThat(alternateFormatDto.getFormatType().getValue()).isEqualTo("other");
    assertThat(alternateFormatDto.getOption().getValue()).isEqualTo(dtoValue);
    assertThat(alternateFormatDto.getAdditionalInfo()).isNull();
  }
  
  @ParameterizedTest
  @CsvSource({
    "colouredPaper, colouredPaper",
    "largePrintCustomFont, largePrintCustomFont",
    "email,email",
    "other, other"
  })
  void when_other_with_additional_info(String formValue, String dtoValue) {
    alternateFormat.setFormatType(AlternateFormat110.FormatType.OTHER);
    alternateFormat.setAdditionalProperty("otherOptions", formValue);
    alternateFormat.setAdditionalProperty(
      "alternateFormatAdditionalInfo", "alt-format-additional-info");
    
    var alternateFormatDto = alternateFormatMapperV4.toDto(alternateFormat);
    
    assertThat(alternateFormatDto.getFormatType().getValue()).isEqualTo("other");
    assertThat(alternateFormatDto.getOption().getValue()).isEqualTo(dtoValue);
    assertThat(alternateFormatDto.getAdditionalInfo()).isEqualTo("alt-format-additional-info");
  }
  
  @Test
  void when_null() {
    var alternateFormatDto = alternateFormatMapperV4.toDto(null);
    
    assertThat(alternateFormatDto.getFormatType()).isNull();
    assertThat(alternateFormatDto.getOption()).isNull();
    assertThat(alternateFormatDto.getAdditionalInfo()).isNull();
  }
  
  @Test
  void when_format_type_null() {
    alternateFormat.setAdditionalProperty("otherOptions", "cassette");
    
    var alternateFormatDto = alternateFormatMapperV4.toDto(alternateFormat);
    
    assertThat(alternateFormatDto.getFormatType()).isNull();
    assertThat(alternateFormatDto.getOption().getValue()).isEqualTo("cassette");
  }
  
  @Test
  void when_format_option_is_null() {
    alternateFormat.setFormatType(AlternateFormat110.FormatType.OTHER);
    
    var alternateFormatDto = alternateFormatMapperV4.toDto(alternateFormat);
    
    assertThat(alternateFormatDto.getFormatType().getValue()).isEqualTo("other");
    assertThat(alternateFormatDto.getOption()).isNull();
  }
}
