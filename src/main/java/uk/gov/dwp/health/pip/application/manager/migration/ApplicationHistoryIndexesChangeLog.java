package uk.gov.dwp.health.pip.application.manager.migration;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import com.mongodb.client.model.IndexOptions;
import lombok.extern.java.Log;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;

@ChangeLog(order = "001")
@Log
public class ApplicationHistoryIndexesChangeLog {

  public static final String INDEX_NAME_STATE_CURRENT_STATE = "state_current_state_idx";
  public static final String INDEX_NAME_STATE_HISTORY_STATE_TIMESTAMP =
      "state_history_state_timestamp_idx";

  private static final String COLLECTION_NAME_APPLICATION = "application";
  private static final String PROPERTY_NAME_CURRENT_STATE = "state.current_state";
  private static final String PROPERTY_NAME_HISTORY_STATE = "state.history.state";
  private static final String PROPERTY_NAME_HISTORY_TIMESTAMP = "state.history.timestamp";

  @ChangeSet(
      order = "001",
      author = "PIP-apply ms-application-manager",
      id = "addHistoryIndexes")
  public void applyHistoryIndexes(@Autowired final MongockTemplate mongockTemplate) {
    addStateCurrentStateIndex(mongockTemplate);
    addStateHistoryStateTimestampIndex(mongockTemplate);
  }

  private static void addStateCurrentStateIndex(
      final MongockTemplate mongockTemplate
  ) {
    final Document bson = new Document()
        .append(PROPERTY_NAME_CURRENT_STATE, 1);
    addApplicationIndex(mongockTemplate, INDEX_NAME_STATE_CURRENT_STATE, bson);
  }

  private static void addStateHistoryStateTimestampIndex(
      final MongockTemplate mongockTemplate
  ) {
    final Document bson = new Document()
        .append(PROPERTY_NAME_HISTORY_STATE, 1)
        .append(PROPERTY_NAME_HISTORY_TIMESTAMP, 1);
    addApplicationIndex(mongockTemplate, INDEX_NAME_STATE_HISTORY_STATE_TIMESTAMP, bson);
  }

  private static void addApplicationIndex(
      final MongockTemplate mongockTemplate, final String indexName, final Document bson
  ) {
    final IndexOptions indexOptions = new IndexOptions();
    indexOptions.unique(false);
    indexOptions.name(indexName);
    mongockTemplate.getCollection(COLLECTION_NAME_APPLICATION)
        .createIndex(bson, indexOptions);
  }

}
