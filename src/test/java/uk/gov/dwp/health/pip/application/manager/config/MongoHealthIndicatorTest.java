package uk.gov.dwp.health.pip.application.manager.config;

import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class MongoHealthIndicatorTest {

  private MongoHealthIndicator mongoHealthIndicator;

  @Mock
  private MongoTemplate mongoTemplate;

  @BeforeEach
  public void beforeEach() {
    this.mongoHealthIndicator = new MongoHealthIndicator(mongoTemplate);
  }

  @Test
  @DisplayName("Ensure that the mongo health check returns UP")
  void testMongoIsUp() {
    final Document buildInfo = mock(Document.class);
    given(buildInfo.get("ok")).willReturn(1.0D);
    given(mongoTemplate.executeCommand("{ ping: 1 }")).willReturn(buildInfo);
    checkFor(Status.UP);
  }

  @Test
  @DisplayName("Ensure that the mongo health check returns DOWN")
  void testMongoIsDown() {
    final Document buildInfo = mock(Document.class);
    given(buildInfo.get("ok")).willReturn(0D);
    given(mongoTemplate.executeCommand("{ ping: 1 }")).willReturn(buildInfo);
    checkFor(Status.DOWN);
  }

  private void checkFor(Status status) {
    final Health health = mongoHealthIndicator.health();
    assertThat(health.getStatus()).isEqualTo(status);
  }
}