package uk.gov.dwp.health.pip.application.manager.messaging;

import uk.gov.dwp.health.integration.message.events.Event;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

class WorkflowEvent extends Event {

  WorkflowEvent(
      String topic,
      String applicationId,
      String name,
      String nino,
      Date submissionDate,
      String routingKey) {
    Map<String, Object> map = new HashMap<>();
    map.put("applicationId", applicationId);
    map.put("name", name);
    map.put("nino", nino);
    map.put("submissionDate", submissionDate);

    setPayload(map);
    setTopic(topic);
    setRoutingKey(routingKey);
  }
}
