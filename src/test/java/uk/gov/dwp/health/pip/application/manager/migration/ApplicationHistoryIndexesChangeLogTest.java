package uk.gov.dwp.health.pip.application.manager.migration;

import com.github.cloudyrock.mongock.driver.api.lock.LockCheckException;
import com.github.cloudyrock.mongock.driver.api.lock.LockManager;
import com.github.cloudyrock.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import com.github.cloudyrock.mongock.driver.api.lock.guard.invoker.LockGuardInvokerImpl;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import com.mongodb.client.ListIndexesIterable;
import org.bson.Document;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.dwp.health.pip.application.manager.migration.ApplicationHistoryIndexesChangeLog.INDEX_NAME_STATE_CURRENT_STATE;
import static uk.gov.dwp.health.pip.application.manager.migration.ApplicationHistoryIndexesChangeLog.INDEX_NAME_STATE_HISTORY_STATE_TIMESTAMP;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DataMongoTest
@Tag("unit")
class ApplicationHistoryIndexesChangeLogTest {

  @Autowired
  private MongoTemplate template;

  @Test
  void applyHistoryIndexes() {
    final LockGuardInvoker lockGuardInvoker = fakeLockGuardInvoker();
    new ApplicationHistoryIndexesChangeLog().applyHistoryIndexes(new MongockTemplate(template, lockGuardInvoker));
    ListIndexesIterable<Document> indexes = template.getCollection("application").listIndexes();
    final boolean[] foundStateCurrentStateIndex = new boolean[]{false};
    final boolean[] foundStateHistoryStateTimestampIndex = new boolean[]{false};
    indexes.forEach(d -> {
      Object indexName = d.get("name");
      if (INDEX_NAME_STATE_CURRENT_STATE.equals(indexName)) {
        foundStateCurrentStateIndex[0] = true;
      }
      if (INDEX_NAME_STATE_HISTORY_STATE_TIMESTAMP.equals(indexName)) {
        foundStateHistoryStateTimestampIndex[0] = true;
      }
    });
    assertTrue(foundStateCurrentStateIndex[0], "expected index " + INDEX_NAME_STATE_CURRENT_STATE);
    assertTrue(foundStateHistoryStateTimestampIndex[0], "expected index " + INDEX_NAME_STATE_HISTORY_STATE_TIMESTAMP);
  }

  private LockGuardInvokerImpl fakeLockGuardInvoker() {
    return new LockGuardInvokerImpl(
        new LockManager() {
          @Override
          public void acquireLockDefault() throws LockCheckException {}

          @Override
          public void ensureLockDefault() throws LockCheckException {}

          @Override
          public void releaseLockDefault() {}

          @Override
          public LockManager setLockMaxWaitMillis(long l) {
            return null;
          }

          @Override
          public int getLockMaxTries() {
            return 1;
          }

          @Override
          public LockManager setLockMaxTries(int i) {
            return null;
          }

          @Override
          public LockManager setLockAcquiredForMillis(long l) {
            return null;
          }

          @Override
          public String getOwner() {
            return "fake-owner";
          }

          @Override
          public boolean isLockHeld() {
            return false;
          }

          @Override
          public void clean() {
          }

          @Override
          public void close() {}
        });
  }
}
