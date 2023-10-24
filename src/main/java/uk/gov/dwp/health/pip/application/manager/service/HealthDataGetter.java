package uk.gov.dwp.health.pip.application.manager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.pip.application.manager.constant.ApplicationState;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.exception.ApplicationNotFoundException;
import uk.gov.dwp.health.pip.application.manager.openapi.healthdisability.v1.dto.ClaimantObject;
import uk.gov.dwp.health.pip.application.manager.openapi.healthdisability.v1.dto.HealthDisabilityDto;
import uk.gov.dwp.health.pip.application.manager.repository.ApplicationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class HealthDataGetter {

  private final ApplicationRepository applicationRepository;

  public HealthDisabilityDto getHealthData(String claimantId) {
    var applications = applicationRepository.findAllByClaimantId(claimantId);
    var applicationsInHealthAndDisability =
        applications.stream()
            .filter(
                application -> {
                  var current = application.getState().getCurrent();
                  return ApplicationState.valueOf(current).getValue()
                          < ApplicationState.SUBMITTED.getValue()
                      && ApplicationState.valueOf(current).getValue()
                          > ApplicationState.REGISTRATION.getValue();
                })
            .collect(Collectors.toList());
    if (applicationsInHealthAndDisability.isEmpty()) {
      throw new ApplicationNotFoundException(
          "No health and disability data found for provided Claimant Id");
    }
    if (applicationsInHealthAndDisability.size() > 1) {
      throw new IllegalStateException(
          "ERROR: multiple health and disability data found for claimant");
    }
    return mapToDto(applicationsInHealthAndDisability.get(0));
  }

  public HealthDisabilityDto getHealthDataByApplicationId(String applicationId) {
    log.info("About to get health data for application {}", applicationId);

    var healthDisabilityDto =
        applicationRepository
            .findById(applicationId)
            .map(this::mapToDto)
            .orElseThrow(
                () -> {
                  throw new ApplicationNotFoundException(
                      "No health and disability data found for provided application id");
                });

    log.info("Got health data for application {}", applicationId);

    return healthDisabilityDto;
  }

  public List<ClaimantObject> getHealthDataByStateAndTimestamp(
      final Integer batchSize,
      final Integer page,
      final String state,
      final LocalDateTime timestampFrom,
      final LocalDateTime timestampTo) {
    List<Application> allByStateAndStateTimestampRange =
        applicationRepository.findAllByStateAndStateTimestampRange(
            batchSize, page, state, timestampFrom, timestampTo);
    return mapToDto(allByStateAndStateTimestampRange);
  }

  private List<ClaimantObject> mapToDto(List<Application> all) {
    return all.stream()
        .map(
            application -> {
              ClaimantObject claimantObject = new ClaimantObject();
              claimantObject.claimantId(application.getClaimantId());
              return claimantObject;
            })
        .collect(Collectors.toList());
  }

  private HealthDisabilityDto mapToDto(Application application) {
    var healthDisabilityDto = new HealthDisabilityDto();
    healthDisabilityDto.setFormData(application.getHealthDisabilityData().getData());
    healthDisabilityDto.meta(application.getHealthDisabilityData().getMeta());
    healthDisabilityDto.setApplicationId(application.getId());
    healthDisabilityDto.setApplicationStatus(
        HealthDisabilityDto.ApplicationStatusEnum.valueOf(application.getState().getCurrent()));
    return healthDisabilityDto;
  }
}
