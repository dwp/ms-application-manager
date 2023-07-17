package uk.gov.dwp.health.pip.application.manager.messaging;

import uk.gov.dwp.health.integration.message.events.Event;

import java.time.Instant;
import java.util.Map;

public class PipcsApiEvent extends Event {

  private static final String DEFAULT_VERSION = "0.1";

  public PipcsApiEvent(
      String topic,
      String routingKey,
      Map<String, Object> payload,
      Instant timestamp,
      String version) {
    setTopic(topic);
    setRoutingKey(routingKey);
    setPayload(payload);
    setMetaData(new TransformationCompletedEventMetaData(timestamp));
    if (version == null || version.isBlank()) {
      setVersion(DEFAULT_VERSION);
    } else {
      setVersion(version);
    }
  }

  public static class TransformationCompletedEventMetaData extends MetaData {
    public TransformationCompletedEventMetaData(Instant timeStamp) {
      super();
      if (timeStamp != null) {
        this.time = timeStamp.toString();
      }
    }
  }
}
