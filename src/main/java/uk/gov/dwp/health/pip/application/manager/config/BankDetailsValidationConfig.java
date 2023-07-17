package uk.gov.dwp.health.pip.application.manager.config;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.dwp.health.pip.application.manager.config.properties.BankDetailsValidationProperties;
import uk.gov.dwp.health.pip.application.manager.external.bankdetails.ApiClient;
import uk.gov.dwp.health.pip.application.manager.external.bankdetails.v2.DefaultApi;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;

import static okhttp3.ConnectionSpec.COMPATIBLE_TLS;
import static okhttp3.ConnectionSpec.MODERN_TLS;

@Configuration
@Slf4j
public class BankDetailsValidationConfig {

  @Autowired
  private SsmParameterCache ssmParameterCache;

  @Bean
  public DefaultApi defaultApi(final BankDetailsValidationProperties properties) {
    // Configure proxy
    OkHttpClient.Builder builder = new OkHttpClient.Builder();

    if (properties.isProxyEnabled() && properties.getProxyPort() > 0
        && properties.getProxyHost() != null
        && !properties.getProxyHost().isEmpty()) {
      final InetSocketAddress socketAddress = new InetSocketAddress(
          properties.getProxyHost(), properties.getProxyPort()
      );
      builder.proxy(new Proxy(Proxy.Type.HTTP, socketAddress));
    }
    if (log.isDebugEnabled()) {
      final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
      interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
      builder.addInterceptor(interceptor);
    }
    if (properties.isTlsEnabled()) {
      builder.connectionSpecs(Arrays.asList(MODERN_TLS, COMPATIBLE_TLS));
      builder.hostnameVerifier(NoopHostnameVerifier.INSTANCE);
      try {
        final SSLContext sslContext = SSLContext.getInstance("TLS");
        final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
            TrustManagerFactory.getDefaultAlgorithm()
        );

        char[] trustStorePassword = getPasswordCharArray(properties.getTrustStorePassword());
        final KeyStore trustStore = getKeyStore(trustStorePassword, properties.getTrustStore());
        trustManagerFactory.init(trustStore);
        final TrustManager trustManagerObject = trustManagerFactory.getTrustManagers()[0];
        final X509TrustManager trustManager = (X509TrustManager) trustManagerObject;

        char[] keystorePasswordCharArray = getPasswordCharArray(properties.getKeyStorePassword());
        KeyStore identityStore = getKeyStore(keystorePasswordCharArray, properties.getKeyStore());
        final String defaultAlgorithm = KeyManagerFactory.getDefaultAlgorithm();
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(defaultAlgorithm);
        keyManagerFactory.init(identityStore, keystorePasswordCharArray);
        KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();

        sslContext.init(keyManagers, trustManagerFactory.getTrustManagers(), null);
        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        builder.sslSocketFactory(sslSocketFactory, trustManager);
      } catch (final NoSuchAlgorithmException | KeyManagementException
                     | CertificateException | KeyStoreException | IOException
                     | UnrecoverableKeyException e) {
        log.error("Cannot configure TLS", e);
        throw new IllegalStateException("Cannot configure TLS", e);
      }
    }
    final ApiClient apiClient = new ApiClient(builder.build());
    apiClient.setBasePath(properties.getBaseUrl());
    apiClient.addDefaultHeader("Content-type", "application/json");

    return new DefaultApi(apiClient);
  }

  private KeyStore getKeyStore(final char[] password, final String keyStoreFilePathString)
      throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
    final KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
    try (FileInputStream fis = new FileInputStream(keyStoreFilePathString)) {
      trustStore.load(fis, password);
    }
    return trustStore;
  }

  private char[] getPasswordCharArray(final String passwordKey) {
    final String parameterValue = ssmParameterCache.getParameterValue(passwordKey, true);
    char[] result = null;
    if (parameterValue == null) {
      log.error("No SSM parameter found for key {}", passwordKey);
    } else {
      result = parameterValue.toCharArray();
    }
    return result;
  }

}
