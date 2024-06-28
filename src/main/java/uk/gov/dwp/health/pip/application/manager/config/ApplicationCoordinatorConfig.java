package uk.gov.dwp.health.pip.application.manager.config;

import java.io.IOException;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import uk.gov.dwp.health.pip.application.manager.config.properties.ApplicationCoordinatorProperties;
import uk.gov.dwp.health.pip.application.manager.exception.ApplicationNotFoundException;
import uk.gov.dwp.health.pip.application.manager.openapi.coordinator.ApiClient;
import uk.gov.dwp.health.pip.application.manager.openapi.coordinator.DefaultMsCoordinatorClient;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ApplicationCoordinatorConfig {

  private final ApplicationCoordinatorProperties applicationCoordinatorProperties;
  private final RestTemplateBuilder builder;
  private final RestTemplate restTemplate;

  @Bean
  public DefaultMsCoordinatorClient defaultMsCoordinatorClient() {
    log.info(
        "Configuring the DefaultMsCoordinatorClient for {}",
        applicationCoordinatorProperties.getBaseUrl());
    var apiClient = new ApiClient(configureRestClient());
    apiClient.setBasePath(applicationCoordinatorProperties.getBaseUrl());
    return new DefaultMsCoordinatorClient(apiClient);
  }

  private RestTemplate configureRestClient() {
    RestTemplate restTemplate = builder
            .errorHandler(new ApplicationCoordinatorResponseErrorHandler())
            .build();
    restTemplate.getMessageConverters().add(jacksonSupportsMoreTypes());
    return restTemplate;
  }

  private HttpMessageConverter jacksonSupportsMoreTypes() {
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    converter.setSupportedMediaTypes(
            Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM));
    return converter;
  }

  private class ApplicationCoordinatorResponseErrorHandler implements ResponseErrorHandler {
    @Override
    public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
      return httpResponse.getStatusCode().is5xxServerError()
              || httpResponse.getStatusCode().is4xxClientError();
    }

    @Override
    public void handleError(ClientHttpResponse httpResponse) throws IOException {
      if (httpResponse.getStatusCode().is5xxServerError()) {
        throw new HttpClientErrorException(httpResponse.getStatusCode());
      }
      if (httpResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
        if (httpResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
          throw new ApplicationNotFoundException("No application found for passed id");
        }
      }
    }

  }
}
