package uk.gov.dwp.health.pip.application.manager.repository;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import uk.gov.dwp.health.mongo.changestream.config.properties.WatcherConfigProperties;
import uk.gov.dwp.health.pip.application.manager.entity.Application;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class MongoEventListenerPipApplicationTest {

  private static final String COLLECTION = "application";

  @Mock
  private WatcherConfigProperties watcherConfigProperties;

  @Test
  void listener_updates_channel_for_application() {
    var application = Application.builder().build();
    var event = new BeforeConvertEvent<>(application, COLLECTION);
    var eventListener = new MongoEventListenerPipApplication(watcherConfigProperties);

    eventListener.onBeforeConvert(event);

    verify(watcherConfigProperties, times(1)).setChangeStreamChannel(application, COLLECTION);
  }
}
