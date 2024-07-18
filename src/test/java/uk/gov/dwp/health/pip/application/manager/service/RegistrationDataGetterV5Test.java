package uk.gov.dwp.health.pip.application.manager.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.exception.ApplicationNotFoundException;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v5.dto.V5ApplicationStatus;
import uk.gov.dwp.health.pip.application.manager.repository.ApplicationRepository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DataMongoTest
@DisplayName("RegistrationDataGetterV5  tests")
@Tag("unit")
class RegistrationDataGetterV5Test {

  @SpyBean
  ApplicationRepository applicationRepository;

  private static String applicationId;
  private static final String CLAIMANT_ID = "111122223333444455556666";
  private static final String NINO = "AA112233A";
  private static final String SUBMISSION_ID = "666655554444333322221111";

  RegistrationDataGetterV5 registrationDataGetterV5;

  @BeforeEach
  void init() {
    applicationRepository.deleteAll();
    registrationDataGetterV5 = new RegistrationDataGetterV5(applicationRepository);
  }

  @DisplayName("Successful lookup using the application id")
  @Test
  void successfulApplicationIdTest() {
    loadSomeData();
    V5ApplicationStatus result = registrationDataGetterV5.getRegistrationDataById(
        applicationId, null, null, null);
    assertThat(result).isNotNull();
    assertThat(result.getApplicationId()).isEqualTo(applicationId);
    assertThat(result.getSubmissionId()).isEqualTo(SUBMISSION_ID);
    assertThat(result.getClaimantId()).isEqualTo(CLAIMANT_ID);
    assertThat(result.getNino()).isEqualTo(NINO);
    verify(applicationRepository, times(1)).findById(any());
  }

  @DisplayName("Successful lookup using the claimant id")
  @Test
  void successfulClaimantIdTest() {
    loadSomeData();
    V5ApplicationStatus result = registrationDataGetterV5.getRegistrationDataById(
        null, CLAIMANT_ID, null, null);
    assertThat(result).isNotNull();
    assertThat(result.getApplicationId()).isEqualTo(applicationId);
    assertThat(result.getSubmissionId()).isEqualTo(SUBMISSION_ID);
    assertThat(result.getClaimantId()).isEqualTo(CLAIMANT_ID);
    assertThat(result.getNino()).isEqualTo(NINO);
    verify(applicationRepository, times(1)).findAllByClaimantId(any());
  }

  @DisplayName("Successful lookup using the submission id")
  @Test
  void successfulSubmissionIdTest() {
    loadSomeData();
    V5ApplicationStatus result = registrationDataGetterV5.getRegistrationDataById(
        null, null, null, SUBMISSION_ID);
    assertThat(result).isNotNull();
    assertThat(result.getApplicationId()).isEqualTo(applicationId);
    assertThat(result.getSubmissionId()).isEqualTo(SUBMISSION_ID);
    assertThat(result.getClaimantId()).isEqualTo(CLAIMANT_ID);
    assertThat(result.getNino()).isEqualTo(NINO);
    verify(applicationRepository, times(1)).getAllBySubmissionId(any());
  }

  @DisplayName("Successful lookup using the NINO")
  @Test
  void successfulNinoTest() {
    loadSomeData();
    V5ApplicationStatus result = registrationDataGetterV5.getRegistrationDataById(
        null, null, NINO, null);
    assertThat(result).isNotNull();
    assertThat(result.getApplicationId()).isEqualTo(applicationId);
    assertThat(result.getSubmissionId()).isEqualTo(SUBMISSION_ID);
    assertThat(result.getClaimantId()).isEqualTo(CLAIMANT_ID);
    assertThat(result.getNino()).isEqualTo(NINO);
    verify(applicationRepository, times(1)).getAllByNino(any());
  }

  @DisplayName("Test to check for specific exception when no application found using application id")
  @Test
  void noApplicationFoundWithApplicationId() {
    loadSomeData();
    assertThrows(ApplicationNotFoundException.class, () -> registrationDataGetterV5
        .getRegistrationDataById(RandomStringUtils.randomAlphabetic(24),
            null, null, null)
    );
  }

  @DisplayName("Test to check for specific exception when no application found using claimant id")
  @Test
  void noApplicationFoundWithClaimantId() {
    loadSomeData();
    assertThrows(ApplicationNotFoundException.class, () -> registrationDataGetterV5
        .getRegistrationDataById(
            null, RandomStringUtils.randomAlphabetic(24).toLowerCase(), null, null)
    );
  }

  @DisplayName("Test to check for specific exception when no application found using submission id")
  @Test
  void noApplicationFoundWithSubmissionId() {
    loadSomeData();
    assertThrows(ApplicationNotFoundException.class, () -> registrationDataGetterV5
        .getRegistrationDataById(
            null, null, null, RandomStringUtils.randomAlphabetic(24).toLowerCase())
    );
  }

  @DisplayName("Test to check for specific exception when no application found using nino")
  @Test
  void noApplicationFoundWithNino() {
    loadSomeData();
    assertThrows(ApplicationNotFoundException.class, () -> registrationDataGetterV5
        .getRegistrationDataById(
            null, null, RandomStringUtils.randomAlphabetic(9).toLowerCase(), null)
    );
  }

  @DisplayName("Test to check for IllegalstateException (Multiple db entries) with claimant id")
  @Test
  void multipleApplicationsFoundForClaimantId() {
    loadSomeData();
    loadSomeData();
    assertThrows(IllegalStateException.class, () -> registrationDataGetterV5
        .getRegistrationDataById(null, CLAIMANT_ID, null, null
        )
    );
  }

  @DisplayName("Test to check for IllegalstateException (Multiple db entries) with submission id")
  @Test
  void multipleApplicationsFoundForSubmissionId() {
    loadSomeData();
    loadSomeData();
    assertThrows(IllegalStateException.class, () -> registrationDataGetterV5
        .getRegistrationDataById( null, null, null, SUBMISSION_ID
        )
    );
  }

  @DisplayName("Test to check for IllegalstateException (Multiple db entries) with nino")
  @Test
  void multipleApplicationsFoundForNino() {
    loadSomeData();
    loadSomeData();
    assertThrows(IllegalStateException.class, () -> registrationDataGetterV5
        .getRegistrationDataById(null, null, NINO, null
        )
    );
  }

  void loadSomeData() {
    Application application = applicationRepository.save(Application.builder()
        .claimantId(CLAIMANT_ID)
        .nino(NINO)
        .submissionId(SUBMISSION_ID)
        .build());
    applicationId = application.getId();
    for (int i = 0; i < 10; i++) {
      applicationRepository.save(Application.builder()
          .claimantId(RandomStringUtils.randomAlphabetic(24).toLowerCase())
          .nino("AA1122334" + i + "A")
          .submissionId(RandomStringUtils.randomAlphabetic(24).toLowerCase())
          .build());
    }
  }
}
