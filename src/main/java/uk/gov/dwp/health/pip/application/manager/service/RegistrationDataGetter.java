package uk.gov.dwp.health.pip.application.manager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.pip.application.manager.constant.ApplicationState;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.exception.ApplicationNotFoundException;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v1.dto.RegistrationDto;
import uk.gov.dwp.health.pip.application.manager.repository.ApplicationRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class RegistrationDataGetter {

  private final ApplicationRepository repository;

  public RegistrationDto getRegistrationDataByClaimantId(String claimantId) {
    List<Application> applications = repository.findAllByClaimantId(claimantId);
    var registrationDtos =
        applications.stream()
            .filter(
                application -> {
                  var current = application.getState().getCurrent();
                  return ApplicationState.valueOf(current).getValue()
                      < ApplicationState.HEALTH_AND_DISABILITY.getValue();
                })
            .map(this::mapToDto)
            .collect(Collectors.toList());
    if (registrationDtos.isEmpty()) {
      throw new ApplicationNotFoundException("No registration data found for provided Claimant ID");
    }
    if (registrationDtos.size() > 1) {
      throw new IllegalStateException("ERROR: multiple registration data found for claimant");
    }
    return registrationDtos.get(0);
  }

  public RegistrationDto getRegistrationDataByApplicationId(String applicationId) {
    log.info("About to get registration data for application {}", applicationId);

    var registrationDto =
        repository
            .findById(applicationId)
            .map(this::mapToDto)
            .orElseThrow(
                () -> {
                  throw new ApplicationNotFoundException(
                      "No registration data found for provided application id");
                });

    log.info("Got registration data for application {}", applicationId);

    return registrationDto;
  }

  private RegistrationDto mapToDto(Application application) {
    var registrationDto = new RegistrationDto();
    registrationDto.setFormData(application.getRegistrationData().getData());
    registrationDto.meta(application.getRegistrationData().getMeta());
    registrationDto.setApplicationId(application.getId());
    registrationDto.setApplicationStatus(
        RegistrationDto.ApplicationStatusEnum.valueOf(application.getState().getCurrent()));
    registrationDto.setSubmissionDate(
        Optional.ofNullable(application.getDateRegistrationSubmitted())
            .map(LocalDate::toString)
            .orElse(""));
    return registrationDto;
  }
}
