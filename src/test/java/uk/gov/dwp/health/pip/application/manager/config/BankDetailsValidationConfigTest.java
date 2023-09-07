package uk.gov.dwp.health.pip.application.manager.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.dwp.health.pip.application.manager.config.properties.BankDetailsValidationProperties;
import uk.gov.dwp.health.pip.application.manager.external.bankdetails.v3.DefaultApi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Tag("unit")
public class BankDetailsValidationConfigTest {

  public static final boolean TLS_ENABLED = true;
  public static final String KEYSTORE_JKS = "src/test/resources/keystore.jks";
  public static final String TRUSTSTORE_JKS = "src/test/resources/truststore.jks";
  public static final String PASSWORD = "changeit";
  public static final String WRONG_PASSWORD = "thisisthewrongpassword";
  public static final String BASE_URL = "https://localhost:8443";
  public static final String CONSUMER_ID = "123123";
  public static final boolean PROXY_ENABLED = true;
  public static final int PROXY_PORT = 1234;
  public static final String PROXY_HOST = "Patrick";
  private final SsmParameterCache mockCache = mock(SsmParameterCache.class);

  @BeforeEach
  void setup() {
    when(mockCache.getParameterValue(PASSWORD, true)).thenReturn(PASSWORD);
    when(mockCache.getParameterValue(WRONG_PASSWORD, true)).thenReturn(WRONG_PASSWORD);
  }

  @Test
  void defaultApi() {
    final BankDetailsValidationProperties properties = getProperties();
    final DefaultApi defaultApi = getDefaultApi(properties);
    assertThat(defaultApi).isNotNull();
    assertThat(defaultApi.getApiClient()).isNotNull();
    assertThat(defaultApi.getApiClient().getBasePath()).isEqualTo(BASE_URL);
  }

  @Test
  void defaultApiWithWrongPassword() {
    final BankDetailsValidationProperties properties = getProperties(WRONG_PASSWORD);
    try {
      getDefaultApi(properties);
      fail();
    } catch (final IllegalStateException e) {
      assertThat(e.getMessage()).isEqualTo("Cannot configure TLS");
    }
  }

  private BankDetailsValidationProperties getProperties() {
    return getProperties(PASSWORD);
  }

  private BankDetailsValidationProperties getProperties(final String password) {
    final BankDetailsValidationProperties properties = new BankDetailsValidationProperties();
    properties.setTlsEnabled(TLS_ENABLED);
    properties.setBaseUrl(BASE_URL);
    properties.setConsumerId(CONSUMER_ID);
    properties.setKeyStore(KEYSTORE_JKS);
    properties.setKeyStorePassword(password);
    properties.setTrustStore(TRUSTSTORE_JKS);
    properties.setTrustStorePassword(password);
    properties.setProxyEnabled(PROXY_ENABLED);
    properties.setProxyPort(PROXY_PORT);
    properties.setProxyHost(PROXY_HOST);
    assertPropertiesAsExpected(properties, password);
    return properties;
  }

  private void assertPropertiesAsExpected(final BankDetailsValidationProperties properties, final String password) {
    assertThat(properties.isTlsEnabled()).isEqualTo(TLS_ENABLED);
    assertThat(properties.getBaseUrl()).isEqualTo(BASE_URL);
    assertThat(properties.getConsumerId()).isEqualTo(CONSUMER_ID);
    assertThat(properties.getKeyStore()).isEqualTo(KEYSTORE_JKS);
    assertThat(properties.getKeyStorePassword()).isEqualTo(password);
    assertThat(properties.getTrustStore()).isEqualTo(TRUSTSTORE_JKS);
    assertThat(properties.getTrustStorePassword()).isEqualTo(password);
    assertThat(properties.isProxyEnabled()).isEqualTo(PROXY_ENABLED);
    assertThat(properties.getProxyPort()).isEqualTo(PROXY_PORT);
    assertThat(properties.getProxyHost()).isEqualTo(PROXY_HOST);
  }

  private DefaultApi getDefaultApi(final BankDetailsValidationProperties properties) {
    final BankDetailsValidationConfig config = new BankDetailsValidationConfig();
    ReflectionTestUtils.setField(config, "ssmParameterCache", mockCache);
    return config.defaultApi(properties);
  }

}
