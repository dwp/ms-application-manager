package uk.gov.dwp.health.pip.application.manager.api.v5;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.dwp.health.pip.application.manager.exception.ApplicationNotFoundException;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v5.dto.V5ApplicationStatus;
import uk.gov.dwp.health.pip.application.manager.service.RegistrationDataGetterV5;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@DisplayName("Test for the RegistrationApiAdapterV5 class")
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class RegistrationApiAdapterV5Test {

  @Mock
  RegistrationDataGetterV5 registrationDataGetterV5;
  RegistrationApiAdapterV5 registrationApiAdapterV5;

  @BeforeEach
  void init() {
    registrationApiAdapterV5 = new RegistrationApiAdapterV5(registrationDataGetterV5);
  }

  @DisplayName("Successful request for status test")
  @Test
  void successfulTest() {
    doReturn(new V5ApplicationStatus()
        .applicationId("123456")).when(registrationDataGetterV5).getRegistrationDataById(
        any(), any(), any(), any());

    registrationApiAdapterV5.getRegistrationStatusByIds(
        RandomStringUtils.randomAlphanumeric(24).toLowerCase(), null, null, null);
  }

  @DisplayName("Duplicate application throws exception test")
  @Test
  void duplicateApplicationTest() {
    doThrow(new IllegalStateException("Ah damn")).when(registrationDataGetterV5)
        .getRegistrationDataById(any(), any(), any(), any());

    assertThrows(IllegalStateException.class, () -> registrationApiAdapterV5
        .getRegistrationStatusByIds(
            null, RandomStringUtils.randomAlphanumeric(24).toLowerCase(), null, null));
  }

  @DisplayName("No application found throws exception test")
  @Test
  void noApplicationFoundTest() {
    doThrow(new ApplicationNotFoundException("Ah damn")).when(registrationDataGetterV5)
        .getRegistrationDataById(any(), any(), any(), any());

    assertThrows(ApplicationNotFoundException.class, () -> registrationApiAdapterV5
        .getRegistrationStatusByIds(
            RandomStringUtils.randomAlphanumeric(24).toLowerCase(), null, null, null));
  }
}
