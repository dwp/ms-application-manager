package uk.gov.dwp.health.pip.application.manager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class Config {

  @Bean
  public Clock clock() {
    return Clock.systemDefaultZone();
  }
}
