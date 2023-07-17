package uk.gov.dwp.health.pip.application.manager.entity;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Tag("unit")
class ApplicationTest {

  @Test
  void when_registration_doesnt_exist() {
    var application = Application.builder().build();
    assertThat(application.getRegistrationData()).isNotNull();
    assertThat((application.getRegistrationData().getData())).isNull();
  }

  @Test
  void when_registration_exists() {
    var application =
        Application.builder()
            .registrationData(FormData.builder().data("form data").build())
            .build();
    assertThat(application.getRegistrationData()).isNotNull();
    assertThat((application.getRegistrationData().getData())).isEqualTo("form data");
  }
}
