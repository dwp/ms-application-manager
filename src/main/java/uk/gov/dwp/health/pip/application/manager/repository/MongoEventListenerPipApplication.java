package uk.gov.dwp.health.pip.application.manager.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;
import uk.gov.dwp.health.mongo.changestream.config.properties.WatcherConfigProperties;
import uk.gov.dwp.health.pip.application.manager.entity.Application;

@Component
@RequiredArgsConstructor
@Slf4j
public class MongoEventListenerPipApplication extends AbstractMongoEventListener<Application> {

  private final WatcherConfigProperties watcherConfigProperties;

  // Intercepts when the collection is about to be updated and sets a change stream event
  @Override
  public void onBeforeConvert(BeforeConvertEvent<Application> event) {
    log.info(
        "Set change stream channel.  changeStreamClass is {}, collectionName is application",
        event.getSource());
    watcherConfigProperties.setChangeStreamChannel(event.getSource(), "application");
  }
}
