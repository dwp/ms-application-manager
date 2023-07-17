package uk.gov.dwp.health.pip.application.manager.config;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterResult;
import com.amazonaws.services.simplesystemsmanagement.model.Parameter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@Tag("unit")
public class SsmParameterCacheTest {

  public static final String PARAMETER_KEY = "The parameter key";
  public static final String PARAMETER_VALUE = "The parameter value";
  private MockedStatic<AWSSimpleSystemsManagementClientBuilder> awsSimpleSystemsManagementClientBuilderMockedStatic;

  @AfterEach
  public void teardown() {
    awsSimpleSystemsManagementClientBuilderMockedStatic.close();
  }

  @BeforeEach
  public void setup() {
    awsSimpleSystemsManagementClientBuilderMockedStatic = mockStatic(AWSSimpleSystemsManagementClientBuilder.class);
  }

  @Test
  public void getParameter() {
    final SsmParameterCache cache = new SsmParameterCache();

    final Parameter parameter = new Parameter();
    parameter.setValue(PARAMETER_VALUE);
    final GetParameterResult result = new GetParameterResult();
    result.withParameter(parameter);
    final AWSSimpleSystemsManagement mockClient = mock(AWSSimpleSystemsManagement.class);
    when(mockClient.getParameter(any())).thenReturn(result);
    final AWSSimpleSystemsManagementClientBuilder mockBuilder = mock(AWSSimpleSystemsManagementClientBuilder.class);
    when(mockBuilder.withRegion(anyString())).thenReturn(mockBuilder);
    when(mockBuilder.build()).thenReturn(mockClient);
    when(AWSSimpleSystemsManagementClientBuilder.standard()).thenReturn(mockBuilder);

    final String parameterValue = cache.getParameterValue(PARAMETER_KEY, true);
    assertThat(parameterValue).isEqualTo(PARAMETER_VALUE);
  }
}
