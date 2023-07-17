package uk.gov.dwp.health.pip.application.manager.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import support.TestFixtures;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.entity.FormData;
import uk.gov.dwp.health.pip.application.manager.entity.History;
import uk.gov.dwp.health.pip.application.manager.entity.State;
import uk.gov.dwp.health.pip.application.manager.entity.enums.FormType;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DataMongoTest
@Tag("unit")
class CustomApplicationRepositoryTest {

  private static final Instant date0 =
      LocalDateTime.of(2001, 9, 11, 15, 30, 0).toInstant(ZoneOffset.UTC);
  private static final LocalDateTime dateRangeFrom = LocalDateTime.of(2001, 9, 11, 17, 30, 0);
  private static final Instant date1 =
      LocalDateTime.of(2001, 9, 12, 15, 30, 0).toInstant(ZoneOffset.UTC);
  private static final LocalDateTime dateRangeTo = LocalDateTime.of(2001, 9, 12, 17, 30, 0);
  private static final Instant date2 =
      LocalDateTime.of(2001, 9, 13, 15, 30, 0).toInstant(ZoneOffset.UTC);
  private static final String state = "REGISTRATION";
  private static final String wrongState = "NOT_REGISTRATION";

  @Resource private MongoTemplate mongoTemplate;
  @Resource private ApplicationRepository applicationRepository;

  @BeforeEach
  void setup() {
    mongoTemplate.dropCollection(Application.class);
  }

  @Test
  void find_claimants_by_state_and_state_change_time() {
    // too early
    createApplication(null, null, State.builder().current(state).build(), state, date0);
    // just right
    createApplication(null, null, State.builder().current(state).build(), state, date1);
    createApplication(null, null, State.builder().current(state).build(), state, date1);
    createApplication(null, null, State.builder().current(state).build(), state, date1);
    createApplication(null, null, State.builder().current(state).build(), state, date1);
    createApplication(null, null, State.builder().current(state).build(), state, date1);
    // wrong state
    createApplication(null, null, State.builder().current(wrongState).build(), wrongState, date1);
    // too late
    createApplication(null, null, State.builder().current(state).build(), state, date2);
    // expecting 5 documents spread across 3 pages of size 2
    assertPageDocumentCount(1, 2);
    assertPageDocumentCount(2, 2);
    assertPageDocumentCount(3, 1);
    assertPageDocumentCount(4, 0);
    assertPageDocumentCount(5, 0);
  }

  @Test
  void when_application_ids_by_nino_exist() {
    createApplication(
        "1234567890", "RN000004A", State.builder().current(state).build(), state, date1);
    createApplication(
        "1234567891", "RN000004B", State.builder().current(state).build(), state, date1);

    var applications = applicationRepository.findApplicationIdsByNino("RN000004A");

    assertThat(applications).hasSize(1);
    assertThat(applications.get(0).getId()).isEqualTo("1234567890");
  }

  @Test
  void when_multiple_application_ids_by_nino_exist() {
    createApplication(
        "1234567890", "RN000004A", State.builder().current(state).build(), state, date1);
    createApplication(
        "1234567891", "RN000004A", State.builder().current(state).build(), state, date1);

    var applications = applicationRepository.findApplicationIdsByNino("RN000004A");

    assertThat(applications).hasSize(2);
    assertThat(applications.get(0).getId()).isEqualTo("1234567890");
    assertThat(applications.get(1).getId()).isEqualTo("1234567891");
  }

  @Test
  void when_application_ids_by_nino_dont_exist() {
    createApplication(
        "1234567890", "RN000004A", State.builder().current(state).build(), state, date1);

    var applications = applicationRepository.findApplicationIdsByNino("RN000004B");

    assertThat(applications).isEmpty();
  }

  private void createApplication(
      String applicationId,
      String nino,
      State state,
      String claimHistoryState,
      Instant claimHistoryTimestamp) {
    final LocalDate currentDate = LocalDate.now();
    state.addHistory(
        History.builder().state(claimHistoryState).timeStamp(claimHistoryTimestamp).build());
    Application.ApplicationBuilder builder =
        Application.builder()
            .claimantId(TestFixtures.CLAIMANT_ID)
            .nino(nino)
            .effectiveFrom(currentDate)
            .benefitCode("PIP")
            .healthDisabilityData(
                FormData.builder()
                    .type(FormType.HEALTH_DISABILITY)
                    .data("{}")
                    .schemaVersion("1.0")
                    .build())
            .effectiveTo(currentDate.plusDays(93))
            .state(state);
    if (applicationId != null) {
      builder.id(applicationId);
    }
    Application application = builder.build();
    mongoTemplate.save(application);
  }

  private void assertPageDocumentCount(final int page, final int expectedDocumentCount) {
    final List<Application> page1 =
        applicationRepository.findAllByStateAndStateTimestampRange(
            2, page, state, dateRangeFrom, dateRangeTo);
    assertNotNull(page1);
    assertEquals(expectedDocumentCount, page1.size());
  }
}
