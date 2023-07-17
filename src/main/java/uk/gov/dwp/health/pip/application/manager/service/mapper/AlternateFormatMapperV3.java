package uk.gov.dwp.health.pip.application.manager.service.mapper;

import org.springframework.stereotype.Component;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.AlternateFormat;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v3.dto.AlternateFormatDto;

import java.util.Map;

@Component
class AlternateFormatMapperV3 {

  AlternateFormatDto toDto(AlternateFormat alternateFormat) {
    AlternateFormatDto.FormatTypeEnum alternateFormatType = null;
    AlternateFormatDto.OptionEnum alternateFormatOption = null;
    String alternateFormatAdditionalInfo = null;
    if (alternateFormat != null) {
      alternateFormatType = getAlternateFormatType(alternateFormat);
      alternateFormatOption = getAlternateFormatOption(alternateFormat);
      alternateFormatAdditionalInfo = getAlternateFormatAdditionalInfo(alternateFormat);
    }

    return new AlternateFormatDto()
        .formatType(alternateFormatType)
        .option(alternateFormatOption)
        .additionalInfo(alternateFormatAdditionalInfo);
  }

  private AlternateFormatDto.FormatTypeEnum getAlternateFormatType(
      AlternateFormat alternateFormat) {
    if (alternateFormat.getFormatType() == null) {
      return null;
    }
    var alternateFormatTypeValue = alternateFormat.getFormatType().value();
    return AlternateFormatDto.FormatTypeEnum.fromValue(alternateFormatTypeValue);
  }

  private AlternateFormatDto.OptionEnum getAlternateFormatOption(AlternateFormat alternateFormat) {
    Map<String, Object> additionalProperties = alternateFormat.getAdditionalProperties();

    var alternateFormatOption =
        additionalProperties.entrySet().stream()
            .filter(entry -> !entry.getKey().equals("alternateFormatAdditionalInfo"))
            .map(entry -> (String) entry.getValue())
            .findFirst()
            .orElse(null);

    if (alternateFormatOption == null) {
      return null;
    }
    return AlternateFormatDto.OptionEnum.fromValue(alternateFormatOption);
  }

  private String getAlternateFormatAdditionalInfo(AlternateFormat alternateFormat) {
    return (String) alternateFormat.getAdditionalProperties().get("alternateFormatAdditionalInfo");
  }
}
