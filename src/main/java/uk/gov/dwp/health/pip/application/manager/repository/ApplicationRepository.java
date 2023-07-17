package uk.gov.dwp.health.pip.application.manager.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.dwp.health.pip.application.manager.entity.Application;

import java.util.List;

@Repository
public interface ApplicationRepository
    extends CrudRepository<Application, String>, ApplicationRepositoryCustom {

  List<Application> findAllByClaimantId(String claimantId);
}
