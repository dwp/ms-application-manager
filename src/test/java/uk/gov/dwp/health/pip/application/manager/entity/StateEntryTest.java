package uk.gov.dwp.health.pip.application.manager.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Tag("unit")
class StateEntryTest {

  @Test
  @DisplayName("test builder creates state entry")
  void testBuilderCreatesStateEntry() {
    var timeStamp = Instant.now();
    var cut = History.builder().state("STATUS").timeStamp(timeStamp).build();
    assertAll(
        "assert state values",
        () -> {
          assertThat(cut.getState()).isEqualTo("STATUS");
          assertThat(cut.getTimeStamp()).isEqualTo(timeStamp);
        });
  }
}
