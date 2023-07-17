package uk.gov.dwp.health.pip.application.manager.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.health.pip.application.manager.entity.enums.System;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Tag("unit")
class LegacyApplicationReferenceTest {

  @Test
  @DisplayName("test builder create legacy application reference")
  void testBuilderCreateLegacyApplicationReference() {
    var entry = History.builder().state("START").timeStamp(Instant.now()).build();
    var state = State.builder().build();
    state.addHistory(entry);
    var cut =
        LegacyApplicationReference.builder()
            .applicationReference("APP_REF")
            .system(System.PIPCS)
            .build();

    assertAll(
        "assert legacy application reference",
        () -> {
          assertThat(cut.getSystem()).isEqualTo(System.PIPCS);
          assertThat(cut.getApplicationReference()).isEqualTo("APP_REF");
        });
  }
}
