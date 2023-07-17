package uk.gov.dwp.health.pip.application.manager.service;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.entity.FormData;
import uk.gov.dwp.health.pip.application.manager.openapi.healthdisability.v2.dto.HealthDisabilityDto;
import uk.gov.dwp.health.pip.application.manager.repository.ApplicationRepository;
import uk.gov.dwp.health.pip.application.manager.service.mapper.HealthDataMapperV2;
import uk.gov.dwp.health.pip2.common.Pip2HealthDisabilityForm;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Tag("unit")
class HealthDataGetterV2Test {

  @Mock private ApplicationRepository applicationRepository;
  @Mock private HealthDataMapperV2 healthDataMapperV2;
  @Mock private HealthDataMarshaller healthDataMarshaller;
  @InjectMocks private HealthDataGetterV2 healthDataGetterV2;

  @Test
  void getHealthDataByApplicationId() {
    final var applicationId = "application-id-1";

    var application =
        Application.builder()
            .id(applicationId)
            .healthDisabilityData(FormData.builder().data("{health form data}").build())
            .build();
    var pip2HealthDisabilityForm = Pip2HealthDisabilityForm.builder().build();

    when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
    when(healthDataMarshaller.marshallHealthData("{health form data}"))
        .thenReturn(pip2HealthDisabilityForm);
    when(healthDataMapperV2.toDto(applicationId, pip2HealthDisabilityForm))
        .thenReturn(new HealthDisabilityDto());

    var healthDisabilityDto = healthDataGetterV2.getHealthDataByApplicationId(applicationId);

    assertThat(healthDisabilityDto).isNotNull();
  }
}
