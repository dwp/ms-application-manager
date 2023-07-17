package uk.gov.dwp.health.pip.application.manager.api.v1;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.dwp.health.pip.application.manager.openapi.v1.dto.ApplicationCreateDto;
import uk.gov.dwp.health.pip.application.manager.openapi.v1.dto.ApplicationDto;
import uk.gov.dwp.health.pip.application.manager.openapi.v1.dto.ApplicationId;
import uk.gov.dwp.health.pip.application.manager.openapi.v1.dto.NinoDto;
import uk.gov.dwp.health.pip.application.manager.service.ApplicationCreator;
import uk.gov.dwp.health.pip.application.manager.service.ApplicationIdsByNinoGetter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class ApplicationApiAdapterTest {

  @Mock private ApplicationCreator applicationCreator;
  @Mock private ApplicationIdsByNinoGetter applicationIdsByNinoGetter;

  @InjectMocks private ApplicationApiAdapter applicationApiAdapter;

  @Test
  void when_new_application_then_create_application() {
    var applicationCreateDto = new ApplicationCreateDto();

    when(applicationCreator.createApplication(applicationCreateDto))
        .thenReturn(new ApplicationDto());

    ResponseEntity<ApplicationDto> responseEntity =
        applicationApiAdapter.createApplication(applicationCreateDto);

    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseEntity.getBody()).isNotNull();
  }

  @Test
  void when_getting_application_ids_by_nino_then_ok() {
    var applicationId = new ApplicationId();

    when(applicationIdsByNinoGetter.getApplicationIdByNino("test-nino")).thenReturn(applicationId);

    ResponseEntity<ApplicationId> responseEntity =
        applicationApiAdapter.getApplicationIdByNino(new NinoDto().nino("test-nino"));

    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseEntity.getBody()).isNotNull();
    assertThat(responseEntity.getBody()).isEqualTo(applicationId);
  }
}
