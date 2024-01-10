package uk.gov.dwp.health.pip.application.manager.api.v3;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v3.dto.RegistrationDto;
import uk.gov.dwp.health.pip.application.manager.service.RegistrationDataGetterV3;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class RegistrationApiAdapterV3Test {

  @Mock private RegistrationDataGetterV3 registrationDataGetterV3;

  @InjectMocks private RegistrationApiAdapterV3 registrationApiAdapterV3;

  @Test
  void when_getting_registration_data_by_application_id() {
    var registrationDto = new RegistrationDto();
    when(registrationDataGetterV3.getRegistrationDataByApplicationId("application-id-1"))
        .thenReturn(registrationDto);

    var response = registrationApiAdapterV3.getRegistrationDataByApplicationId("application-id-1");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo(registrationDto);
  }
}
