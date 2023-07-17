package uk.gov.dwp.health.pip.application.manager.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("unit")
class DrsRequestIdTest {

  @Test
  @DisplayName("Test create DRS request entity")
  void testCreateDrsRequestEntity() {
    LocalDateTime now = LocalDateTime.now();
    DrsRequestId actual =
        DrsRequestId.builder().drsRequestId("mock_drs_request_id").timestamp(now).build();
    assertAll(
        "test drs entity values",
        () -> assertEquals("mock_drs_request_id", actual.getDrsRequestId()),
        () -> assertEquals(now, actual.getTimestamp()));
  }
}
