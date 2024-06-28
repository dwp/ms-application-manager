package uk.gov.dwp.health.pip.application.manager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import uk.gov.dwp.health.pip.application.manager.config.properties.ApplicationProperties;
import uk.gov.dwp.health.pip.application.manager.constant.ApplicationState;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.entity.State;
import uk.gov.dwp.health.pip.application.manager.entity.enums.Language;
import uk.gov.dwp.health.pip.application.manager.openapi.coordinator.dto.ApplicationStateDto;
import uk.gov.dwp.health.pip.application.manager.openapi.v1.dto.ApplicationCreateDto;
import uk.gov.dwp.health.pip.application.manager.openapi.v1.dto.ApplicationDto;
import uk.gov.dwp.health.pip.application.manager.repository.ApplicationRepository;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class ApplicationCreatorTest {

  private static final Instant DATE_TIME_1 = Instant.parse("2021-03-02T08:00:00.000Z");

  @Mock private ApplicationProperties applicationProperties;
  @Mock private ApplicationRepository applicationRepository;
  @Mock private ApplicationCoordinatorService applicationCoordinatorService;
  @Mock private Clock clock;

  @InjectMocks private ApplicationCreator applicationCreator;

  @Test
  void when_creating_application_and_none_exist_for_claimant() {
    ApplicationCreateDto applicationCreateDto =
        new ApplicationCreateDto()
            .benefitType(ApplicationCreateDto.BenefitTypeEnum.PIP)
            .claimantId("claimant-id")
            .language(ApplicationCreateDto.LanguageEnum.EN);

    when(clock.instant())
            .thenReturn(DATE_TIME_1);
    when(applicationProperties.getActiveDuration())
            .thenReturn(10);
    when(applicationRepository.save(any(Application.class)))
        .thenReturn(
            Application.builder()
                .id("application-id")
                .state(State.builder().current("REGISTRATION").build())
                .build());
    when(applicationCoordinatorService.postApplicationId("application-id"))
        .thenReturn(State.builder().build());

    ApplicationDto applicationDto = applicationCreator.createApplication(applicationCreateDto);

    verifySave();

    assertThat(applicationDto.getApplicationId()).isEqualTo("application-id");
  }

  @Test
  void when_creating_application_and_one_exists_for_claimant() {

    String id1 = "id1";
    Application application1 = Application.builder()
            .id(id1)
            .state(
                    State.builder().current(ApplicationState.REGISTRATION.toString()).build())
            .build();

    when(applicationRepository.findAllByClaimantId("claimant-id-1"))
        .thenReturn(List.of(application1));

    when(applicationCoordinatorService.hasActiveApplications(List.of(id1)))
            .thenReturn(true);

    ApplicationCreateDto applicationCreateDto =
        new ApplicationCreateDto().claimantId("claimant-id-1");

    assertThatThrownBy(() -> applicationCreator.createApplication(applicationCreateDto))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("ERROR: claimant already has active application");
  }

  private void verifySave() {
    ArgumentCaptor<Application> applicationArgumentCaptor =
        ArgumentCaptor.forClass(Application.class);
    verify(applicationRepository, times(1)).save(applicationArgumentCaptor.capture());

    Application application = applicationArgumentCaptor.getValue();

    assertThat(application).isNotNull();
    assertThat(application.getAudit().getCreated()).isEqualTo(DATE_TIME_1);
    assertThat(application.getAudit().getLastModified()).isEqualTo(DATE_TIME_1);
    assertThat(application.getBenefitCode()).isEqualTo("PIP");
    assertThat(application.getClaimantId()).isEqualTo("claimant-id");
    assertThat(application.getEffectiveFrom()).isEqualTo(LocalDate.now());
    assertThat(application.getEffectiveTo()).isEqualTo(LocalDate.now().plusDays(10));
    assertThat(application.getLanguage()).isEqualTo(Language.EN);
    assertThat(application.getPipcsRegistrationState()).isNotNull();
    assertThat(application.getState().getCurrent()).isEqualTo("REGISTRATION");
    assertThat(application.getRegistrationData().getData()).isNull();
    assertThat(application.getRegistrationData().getSchemaVersion()).isNull();
    assertThat(application.getSubmissionId()).isNull();
    assertThat(application.getDrsRequestId()).isNull();
    assertThat(application.getHealthDisabilityData()).isNull();
    assertThat(application.getHealthDisabilityState()).isNull();
    assertThat(application.getLegacyApplicationReference()).isNull();
    assertThat(application.getMotabilityData()).isNull();
    assertThat(application.getPipcsMotabilityState()).isNull();
  }
}
