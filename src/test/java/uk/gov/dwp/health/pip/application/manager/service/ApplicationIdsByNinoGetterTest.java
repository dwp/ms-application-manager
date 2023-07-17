package uk.gov.dwp.health.pip.application.manager.service;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.repository.ApplicationRepository;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class ApplicationIdsByNinoGetterTest {

  @Mock private ApplicationRepository applicationRepository;

  @Test
  void when_application_id_by_nino_exists() {
    when(applicationRepository.findApplicationIdsByNino("RN000004A"))
        .thenReturn(List.of(Application.builder().id("application-id-1").build()));

    var applicationIdsByNinoGetter = new ApplicationIdsByNinoGetter(applicationRepository);

    var applicationId = applicationIdsByNinoGetter.getApplicationIdByNino("RN000004A");

    assertThat(applicationId.getApplicationId()).isEqualTo("application-id-1");
  }

  @Test
  void when_application_ids_by_nino_exist() {
    when(applicationRepository.findApplicationIdsByNino("RN000004A"))
        .thenReturn(
            List.of(
                Application.builder().id("application-id-1").build(),
                Application.builder().id("application-id-2").build()));

    var applicationIdsByNinoGetter = new ApplicationIdsByNinoGetter(applicationRepository);

    assertThatThrownBy(() -> applicationIdsByNinoGetter.getApplicationIdByNino("RN000004A"))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Multiple applications found for given NINO");
  }

  @Test
  void when_application_ids_by_nino_dont_exist() {
    when(applicationRepository.findApplicationIdsByNino("RN000004A"))
        .thenReturn(Collections.emptyList());

    var applicationIdsByNinoGetter = new ApplicationIdsByNinoGetter(applicationRepository);

    var applicationId = applicationIdsByNinoGetter.getApplicationIdByNino("RN000004A");

    assertThat(applicationId.getApplicationId()).isNull();
  }
}
