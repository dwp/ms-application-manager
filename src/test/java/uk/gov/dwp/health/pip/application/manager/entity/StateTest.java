package uk.gov.dwp.health.pip.application.manager.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@Tag("unit")
class StateTest {

  @Test
  @DisplayName("test state builder create state")
  void testStateBuilderCreateState() {
    var cut = State.builder().current("STARTED").build();
    assertThat(cut.getCurrent()).isEqualTo("STARTED");
  }

  @Test
  @DisplayName("test add new state entry and set current state")
  void testAddNewStateEntryAndSetCurrentState() {
    var started = History.builder().state("STARTED").timeStamp(Instant.now()).build();
    var cut = new State();
    cut.addHistory(started);
    assertThat(cut.getCurrent()).isEqualTo("STARTED");
    assertThat(cut.getHistory()).containsSequence(List.of(started));
  }

  @Test
  @DisplayName("test add new state entry to history and set current state")
  void testAddNewStateEntryToHistoryAndSetCurrentState() {
    var history = spy(new LinkedList<History>());
    var started = History.builder().state("STARTED").timeStamp(Instant.now()).build();
    history.add(started);

    var inFlight = History.builder().state("IN-PROGRESS").timeStamp(Instant.now()).build();
    var cut = State.builder().current("STARTED").history(history).build();
    cut.addHistory(inFlight);

    verify(history).add(inFlight);
    assertThat(cut.getHistory()).containsSequence(List.of(started, inFlight));
    assertThat(cut.getCurrent()).isEqualTo("IN-PROGRESS");
  }
}
