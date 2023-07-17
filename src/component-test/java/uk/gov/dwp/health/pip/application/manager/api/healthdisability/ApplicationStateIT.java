package uk.gov.dwp.health.pip.application.manager.api.healthdisability;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import uk.gov.dwp.health.pip.application.manager.api.ApiTest;
import uk.gov.dwp.health.pip.application.manager.config.MongoClientConnection;
import uk.gov.dwp.health.pip.application.manager.constant.ApplicationState;
import uk.gov.dwp.health.pip.application.manager.entity.History;
import uk.gov.dwp.health.pip.application.manager.entity.State;
import uk.gov.dwp.health.pip.application.manager.responsemodels.Application;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static uk.gov.dwp.health.pip.application.manager.utils.UrlBuilderUtil.buildGetClaimantsWithState;

public class ApplicationStateIT extends ApiTest {

  private static final LocalDateTime time0 = LocalDateTime.of(2001, 1, 1, 10, 30, 0);
  private static final LocalDateTime time1 = LocalDateTime.of(2001, 1, 1, 12, 30, 0);
  private static final LocalDateTime time2 = LocalDateTime.of(2001, 1, 1, 14, 30, 0);
  private static final LocalDateTime time3 = LocalDateTime.of(2001, 1, 2, 12, 30, 0);
  private static final LocalDateTime time4 = LocalDateTime.of(2001, 1, 2, 14, 30, 0);

  private static final String stateName = ApplicationState.REGISTRATION.name();
  private static final String wrongStateName = ApplicationState.HEALTH_AND_DISABILITY.name();

  private MongoTemplate mongoTemplate;

  @BeforeEach
  public void createHealthDisabilityApplication() {
    mongoTemplate = MongoClientConnection.getMongoTemplate();
    mongoTemplate.dropCollection(Application.class);
    setupGetApplicationsByStateData();
  }

  @Test
  void testGetApplicationsByStateClaimant1() throws Exception {
    final Response response = getRequest(buildGetClaimantsWithState(2, 1, stateName, time0, time2));
    final int actualStatusCode = response.getStatusCode();
    assertThat(actualStatusCode).isEqualTo(200);
    assertThat(response.asString()).isEqualTo("[{\"claimant_id\":\"1\"}]");
  }

  @Test
  void testGetApplicationsByStateClaimant3() throws Exception {
    final Response response = getRequest(buildGetClaimantsWithState(2, 1, stateName, time2, time4));
    final int actualStatusCode = response.getStatusCode();
    assertThat(actualStatusCode).isEqualTo(200);
    assertThat(response.asString()).isEqualTo("[{\"claimant_id\":\"3\"}]");
  }

  @Test
  void testGetApplicationsByStateNoMatches() throws Exception {
    final Response response = getRequest(buildGetClaimantsWithState(2, 1, wrongStateName, time2, time4));
    final int actualStatusCode = response.getStatusCode();
    assertThat(actualStatusCode).isEqualTo(200);
    assertThat(response.asString()).isEqualTo("[]");
  }

  @Test
  void testGetApplicationsByStateInvalidState() throws Exception {
    final Response response = getRequest(buildGetClaimantsWithState(2, 1, "123", time2, time4));
    final int actualStatusCode = response.getStatusCode();
    assertThat(actualStatusCode).isEqualTo(400);
  }

  @Test
  void testGetApplicationsByStateInvalidTimestamp() throws Exception {
    final Response response = getRequest(buildGetClaimantsWithState(2, 1, stateName, "fish", "food"));
    final int actualStatusCode = response.getStatusCode();
    assertThat(actualStatusCode).isEqualTo(400);
  }

  private void setupGetApplicationsByStateData() {
    final uk.gov.dwp.health.pip.application.manager.entity.Application application1 = getApplication(stateName, "1", time1);
    final uk.gov.dwp.health.pip.application.manager.entity.Application application2 = getApplication(wrongStateName, "2", time1);
    final uk.gov.dwp.health.pip.application.manager.entity.Application application3 = getApplication(stateName, "3", time3);
    mongoTemplate.save(application1, "application");
    mongoTemplate.save(application2, "application");
    mongoTemplate.save(application3, "application");
  }

  private static uk.gov.dwp.health.pip.application.manager.entity.Application getApplication(String stateName, String claimantId, LocalDateTime dateTime) {
    var application1 =
        uk.gov.dwp.health.pip.application.manager.entity.Application.builder()
            .claimantId(claimantId)
            .state(State.builder()
                .current(stateName)
                .history(Arrays.asList(History.builder().state(stateName).timeStamp(dateTime.toInstant(ZoneOffset.ofHours(0))).build()))
                .build())
            .build();
    return application1;
  }
}
