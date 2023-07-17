package uk.gov.dwp.health.pip.application.manager.config;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.dwp.health.pip.application.manager.config.properties.BankDetailsValidationProperties;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(
    classes = {
        BankDetailsValidationProperties.class,
        BankDetailsValidationConfig.class,
        SsmParameterCache.class
    },
    properties = {
        "aws.encryption.messageDataKeyId=mock-message-data-key"
    })
@Tag("unit")
public class BankDetailsValidationConfigWiringTest {

  @Autowired
  private BankDetailsValidationConfig config;

  @Test
  public void checkAutowire() {
    final Object ssmParameterCache = ReflectionTestUtils.getField(config, "ssmParameterCache");
    assertThat(ssmParameterCache).isNotNull();
  }
}
