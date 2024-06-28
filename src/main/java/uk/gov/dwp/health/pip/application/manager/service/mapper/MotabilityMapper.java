package uk.gov.dwp.health.pip.application.manager.service.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.MotabilitySchemeSchema100;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v4.dto.MotabilityDto;

@Component
@RequiredArgsConstructor
public class MotabilityMapper {
  public MotabilityDto toDto(MotabilitySchemeSchema100 motabilityScheme) {
    return new MotabilityDto()
      .receiveMotabilityInformation(getMotabilityInformation(motabilityScheme));
  }
  
  private MotabilityDto.ReceiveMotabilityInformationEnum getMotabilityInformation(
      MotabilitySchemeSchema100 motabilityScheme) {
    String motabilitySchemeValue = motabilityScheme.getReceiveMotabilityInformation();
    return motabilitySchemeValue != null
      ? MotabilityDto.ReceiveMotabilityInformationEnum.fromValue(motabilitySchemeValue) : null;
  }
}
