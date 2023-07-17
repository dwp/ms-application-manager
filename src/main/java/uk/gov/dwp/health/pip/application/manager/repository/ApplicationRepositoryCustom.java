package uk.gov.dwp.health.pip.application.manager.repository;

import uk.gov.dwp.health.pip.application.manager.entity.Application;

import java.time.LocalDateTime;
import java.util.List;

public interface ApplicationRepositoryCustom {

  List<Application> findAllByStateAndStateTimestampRange(
      final Integer pageSize,
      final Integer page,
      final String state,
      final LocalDateTime timestampFrom,
      final LocalDateTime timestampTo);

  List<Application> findApplicationIdsByNino(String nino);
}
