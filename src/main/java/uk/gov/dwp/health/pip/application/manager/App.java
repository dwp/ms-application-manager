package uk.gov.dwp.health.pip.application.manager;

import lombok.Generated;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Generated
@EnableMongoRepositories(basePackages = {"uk.gov.dwp.health.pip.application.manager.repository"})
@SpringBootApplication
public class App {

  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }
}
