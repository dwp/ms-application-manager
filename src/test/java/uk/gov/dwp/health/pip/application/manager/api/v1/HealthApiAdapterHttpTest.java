package uk.gov.dwp.health.pip.application.manager.api.v1;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import support.TestFixtures;
import uk.gov.dwp.health.pip.application.manager.api.AppControllerAdvise;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.entity.History;
import uk.gov.dwp.health.pip.application.manager.entity.State;
import uk.gov.dwp.health.pip.application.manager.openapi.healthdisability.v1.dto.ClaimId;
import uk.gov.dwp.health.pip.application.manager.repository.ApplicationRepository;
import uk.gov.dwp.health.pip.application.manager.service.HealthDataGetter;
import uk.gov.dwp.health.pip.application.manager.service.HealthDataSubmitter;
import uk.gov.dwp.health.pip.application.manager.service.HealthDataUpdater;

import java.time.Instant;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.dwp.health.pip.application.manager.openapi.healthdisability.v1.dto.ClaimRecordReturn.ClaimStatusEnum.CLAIM_STARTED;

@AutoConfigureMockMvc
@ContextConfiguration(
    classes = {HealthApiAdapter.class, AppControllerAdvise.class, ApplicationRepository.class})
@WebMvcTest
@Tag("unit")
class HealthApiAdapterHttpTest {

  @Autowired private MockMvc mockMvc;
  @MockBean private ApplicationRepository applicationRepository;
  @MockBean private HealthDataGetter healthDataGetter;
  @MockBean private HealthDataUpdater healthDataUpdater;
  @MockBean private HealthDataSubmitter healthDataSubmitter;

  @Test
  @DisplayName("Test get claim status")
  @Disabled
  void testGetClaimStatus() throws Exception {
    ClaimId claimId = new ClaimId();
    claimId.setClaimId(TestFixtures.APPLICATION_ID);
    Application application = new Application();
    var state = State.builder().build();
    var history =
        History.builder().state(CLAIM_STARTED.getValue()).timeStamp(Instant.now()).build();
    state.addHistory(history);
    application.setState(state);
    when(applicationRepository.findById(TestFixtures.APPLICATION_ID))
        .thenReturn(Optional.of(application));
    mockMvc
        .perform(get("/v1/claim/status/" + TestFixtures.APPLICATION_ID))
        .andExpect(status().isOk());
  }

  @Test
  void testGetApplicationsByState() throws Exception {
    mockMvc
        .perform(get(
            "/v1/application/healthdisability/REGISTRATION?pageSize=9&page=1&timestampFrom=2001-01-01T00:00:00.0&timestampTo=2001-01-01T00:00:00.0"
        ))
        .andExpect(status().isOk());
  }

  @Test
  void testGetApplicationsByStateWithMissingParameter() throws Exception {
    mockMvc
        .perform(get("/v1/application/healthdisability/REGISTRATION?pageSize=9&page=1&timestampFrom=2001-01-01T00:00:00.0"))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void testGetApplicationsByStateWithInvalidState() throws Exception {
    mockMvc
        .perform(get("/v1/application/healthdisability/123456?pageSize=2&page=1&timestampFrom=2001-01-01T00:00:00.0"))
        .andExpect(status().is4xxClientError());
  }
}
