package uk.gov.dwp.health.pip.application.manager.config;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterResult;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SsmParameterCache {

  public static final String DEFAULT_REGION = "eu-west-2";
  public static final String REGION_ENV_VAR = "UK_GOV_DWP_HEALTH_INTEGRATION_AWS_REGION";

  private AWSSimpleSystemsManagement client = null;

  private Map<String, String> cache = new HashMap<>();

  public String getParameterValue(final String key, final boolean decryptValue) {
    String result = cache.get(key);
    if (result == null) {
      final GetParameterRequest request = new GetParameterRequest();
      request.setName(key);
      request.setWithDecryption(decryptValue);
      final GetParameterResult parameterResult = getClient().getParameter(request);
      result = parameterResult.getParameter().getValue();
      cache.put(key, result);
    }
    return result;
  }

  private AWSSimpleSystemsManagement getClient() {
    if (client == null) {
      client = AWSSimpleSystemsManagementClientBuilder.standard()
          .withRegion(System.getenv().getOrDefault(REGION_ENV_VAR, DEFAULT_REGION))
          .build();
    }
    return client;
  }

}
