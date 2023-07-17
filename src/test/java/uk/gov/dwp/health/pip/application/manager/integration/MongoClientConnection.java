package uk.gov.dwp.health.pip.application.manager.integration;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import org.springframework.data.mongodb.core.MongoTemplate;

import static uk.gov.dwp.health.pip.application.manager.integration.Constants.DB_HOST;

public class MongoClientConnection {

  static MongoTemplate getMongoTemplate() {
    var connectionString = new ConnectionString(DB_HOST + "/pip-apply-application-mgr");

    var mongoClientSettings =
        MongoClientSettings.builder().applyConnectionString(connectionString).build();

    var mongoClient = MongoClients.create(mongoClientSettings);

    return new MongoTemplate(mongoClient, "pip-apply-application-mgr");
  }
}
