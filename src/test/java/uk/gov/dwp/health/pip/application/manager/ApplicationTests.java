package uk.gov.dwp.health.pip.application.manager;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import uk.gov.dwp.health.integration.message.aws.AWSFlowConfiguration;
import uk.gov.dwp.health.pip.application.manager.api.v1.HealthApiAdapter;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
    properties = {
      "pip.application.active-duration=93",
      "submission.base-url=https://dwp.gov.uk",
      "submission.create-path: /v1/create",
      "aws.encryption.messageDataKeyId=mock-message-data-key",
    })
@Disabled
class ApplicationTests {

  @MockBean private AWSFlowConfiguration mockAWSFlowConfiguration;

  @Autowired private HealthApiAdapter claimController;

  @Test
  void contextLoads() {
    assertThat(claimController).isNotNull();
  }
}
