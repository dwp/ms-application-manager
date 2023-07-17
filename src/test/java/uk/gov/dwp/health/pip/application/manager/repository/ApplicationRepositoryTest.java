package uk.gov.dwp.health.pip.application.manager.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import support.TestFixtures;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.entity.FormData;
import uk.gov.dwp.health.pip.application.manager.entity.History;
import uk.gov.dwp.health.pip.application.manager.entity.State;
import uk.gov.dwp.health.pip.application.manager.entity.enums.FormType;
import uk.gov.dwp.health.pip.application.manager.openapi.healthdisability.v1.dto.ClaimRecordReturn;
import uk.gov.dwp.health.pip.application.manager.openapi.healthdisability.v1.dto.ClaimStatus;

import java.time.Instant;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DataMongoTest
@Tag("unit")
class ApplicationRepositoryTest {

  @Autowired private ApplicationRepository cut;

  @BeforeEach
  void setup() {
    cut.deleteAll();
  }

  private void createFixture() {
    final LocalDate currentDate = LocalDate.now();
    State state = State.builder().build();
    state.addHistory(
        History.builder()
            .state(ClaimStatus.ClaimStatusEnum.CLAIM_STARTED.toString())
            .timeStamp(Instant.now())
            .build());
    Application application =
        Application.builder()
            .claimantId(TestFixtures.CLAIMANT_ID)
            .id(TestFixtures.APPLICATION_ID)
            .effectiveFrom(currentDate)
            .benefitCode("PIP")
            .healthDisabilityData(
                FormData.builder()
                    .type(FormType.HEALTH_DISABILITY)
                    .data("{}")
                    .schemaVersion("1.0")
                    .build())
            .effectiveTo(currentDate.plusDays(93))
            .state(state)
            .build();
    cut.save(application);
  }

  @Nested
  @DisplayName("Tests for query application")
  class QueryApplication {

    @Test
    void test_find_list_of_applications_by_claimant_id() {
      createFixture();
      var actual = cut.findAllByClaimantId(TestFixtures.CLAIMANT_ID);
      assertThat(actual).hasSize(1);
    }

    private void assertClaim(Application actual) {
      assertAll(
          "assert claim found",
          () -> assertEquals(TestFixtures.CLAIMANT_ID, actual.getClaimantId()),
          () -> assertEquals(TestFixtures.APPLICATION_ID, actual.getId()),
          () ->
              assertEquals(
                  ClaimRecordReturn.ClaimStatusEnum.CLAIM_STARTED.toString(),
                  actual.getState().getCurrent()),
          () -> assertEquals(LocalDate.now(), actual.getEffectiveFrom()),
          () -> assertEquals("PIP", actual.getBenefitCode()),
          () -> assertEquals("{}", actual.getHealthDisabilityData().getData()),
          () -> assertEquals(LocalDate.now().plusDays(93), actual.getEffectiveTo()));
    }

    @Test
    @DisplayName("Test find a claim by claim id")
    void testFindAClaimByClaimId() throws Exception {
      createFixture();
      Application actual =
          cut.findById(TestFixtures.APPLICATION_ID).orElseThrow(() -> new Exception("Test failed"));
      assertClaim(actual);
    }
  }
}
