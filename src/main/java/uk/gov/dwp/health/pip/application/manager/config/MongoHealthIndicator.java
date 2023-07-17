package uk.gov.dwp.health.pip.application.manager.config;

import org.bson.Document;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class MongoHealthIndicator extends AbstractHealthIndicator {
  private final MongoTemplate mongoTemplate;

  public MongoHealthIndicator(MongoTemplate mongoTemplate) {
    super("MongoDB health check failed");
    Assert.notNull(mongoTemplate, "MongoTemplate must not be null");
    this.mongoTemplate = mongoTemplate;
  }

  protected void doHealthCheck(Health.Builder builder) {
    Document result = this.mongoTemplate.executeCommand("{ ping: 1 }");
    if (Double.valueOf(1.0D).equals(result.get("ok"))) {
      builder.up();
    } else {
      builder.down();
    }
  }
}