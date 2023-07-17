package uk.gov.dwp.health.pip.application.manager.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.health.pip.application.manager.entity.enums.FormType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Tag("unit")
class FormDataTest {

  @Test
  @DisplayName("test form data builder create form")
  void testFormDataBuilderCreateForm() {
    var cut = FormData.builder()
        .data("{}")
        .schemaVersion("1.0")
        .type(FormType.HEALTH_DISABILITY).build();

    assertAll(
        "form data fields",
        () -> {
          assertThat(cut.getData()).isEqualTo("{}");
          assertThat(cut.getType()).isEqualTo(FormType.HEALTH_DISABILITY);
          assertThat(cut.getSchemaVersion()).isEqualTo("1.0");
        });
  }

}
