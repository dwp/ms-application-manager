package uk.gov.dwp.health.pip.application.manager.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

public class MongoClientConnection {

  public static MongoTemplate getMongoTemplate() {

    ConnectionString connectionString =
        new ConnectionString(
            "mongodb://"
                + getEnv("MONGODB_HOST", "localhost")
                + ":"
                + getEnv("MONGODB_PORT", "27017")
                + "/pip-apply-application-mgr");

    MongoClientSettings mongoClientSettings =
        MongoClientSettings.builder().applyConnectionString(connectionString).build();

    MongoClient mongoClient = MongoClients.create(mongoClientSettings);

    return new MongoTemplate(mongoClient, "pip-apply-application-mgr");
  }

  private static String getEnv(String name, String defaultValue) {
    String env = System.getenv(name);
    return env == null ? defaultValue : env;
  }

  public static void emptyMongoCollection() {
    getMongoTemplate().remove(new Query(), "application");
  }
}
