package uk.gov.dwp.health.pip.application.manager.repository;

import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import uk.gov.dwp.health.pip.application.manager.entity.Application;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@RequiredArgsConstructor
public class ApplicationRepositoryCustomImpl implements ApplicationRepositoryCustom {

  private final MongoTemplate mongoTemplate;

  public List<Application> findAllByStateAndStateTimestampRange(
      final Integer pageSize,
      final Integer page,
      final String state,
      final LocalDateTime timestampFrom,
      final LocalDateTime timestampTo) {
    final Document queryDocument = new Document("state.current_state", state);
    final Document historyCriteria = new Document();
    final Document elemMatch = new Document();
    queryDocument.append("state.history", historyCriteria);
    historyCriteria.append("$elemMatch", elemMatch);
    elemMatch.append("state", state);
    Document timestampCriteria = new Document("$gte", timestampFrom);
    timestampCriteria.append("$lte", timestampTo);
    elemMatch.append("timestamp", timestampCriteria);
    final boolean invalidPagingParameters =
        page == null || pageSize == null || page < 1 || pageSize < 1;
    final int skipCount = invalidPagingParameters ? 0 : (page - 1) * pageSize;
    final int limit = invalidPagingParameters ? 0 : pageSize;
    final Query query =
        new BasicQuery(queryDocument).allowDiskUse(true).skip(skipCount).limit(limit);
    return mongoTemplate.find(query, Application.class);
  }

  public List<Application> findApplicationIdsByNino(String nino) {
    var query = new Query();
    query.fields().include("_id");
    query.addCriteria(where("nino").is(nino));

    return mongoTemplate.find(query, Application.class);
  }
}
