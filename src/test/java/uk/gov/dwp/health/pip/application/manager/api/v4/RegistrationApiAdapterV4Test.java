package uk.gov.dwp.health.pip.application.manager.api.v4;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v4.dto.RegistrationDto;
import uk.gov.dwp.health.pip.application.manager.service.RegistrationDataGetterV4;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class RegistrationApiAdapterV4Test {
  
  @Mock private RegistrationDataGetterV4 registrationDataGetterV4;
  
  @InjectMocks private RegistrationApiAdapterV4 registrationApiAdapterV4;
  
  @Test
  void when_getting_registration_data_by_application_id() {
    var registrationDto = new RegistrationDto();
    when(registrationDataGetterV4.getRegistrationDataByApplicationId("application-id-1"))
      .thenReturn(registrationDto);
    
    var response = registrationApiAdapterV4.getRegistrationDataByApplicationId("application-id-1");
    
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo(registrationDto);
  }
}

