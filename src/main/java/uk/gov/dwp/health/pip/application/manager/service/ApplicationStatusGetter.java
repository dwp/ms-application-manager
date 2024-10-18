package uk.gov.dwp.health.pip.application.manager.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import uk.gov.dwp.health.pip.application.manager.constant.ApplicationState;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.exception.ApplicationNotFoundException;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.RegistrationSchema140;
import uk.gov.dwp.health.pip.application.manager.openapi.coordinator.dto.ApplicationCoordinatorDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v1.dto.ApplicationCoordinatorStatusDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v1.dto.ClaimantIdAndApplicationStatus;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v2.dto.ClaimantIdAndStatusDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v2.dto.HistoryDto;
import uk.gov.dwp.health.pip.application.manager.repository.ApplicationRepository;

@RequiredArgsConstructor
@Service
@Slf4j
public class ApplicationStatusGetter {

  private final ApplicationRepository applicationRepository;
  private final RegistrationDataMarshaller registrationDataMarshaller;
  private final ApplicationCoordinatorService applicationCoordinatorService;

  @NotNull
  private static ClaimantIdAndStatusDto toClaimantIdAndStatusDto(Application application) {
    ClaimantIdAndStatusDto claimantIdAndStatusDto = new ClaimantIdAndStatusDto();
    claimantIdAndStatusDto.setClaimantId(application.getClaimantId());

    claimantIdAndStatusDto.setCurrentState(
        ClaimantIdAndStatusDto.CurrentStateEnum.fromValue(application.getState().getCurrent()));

    claimantIdAndStatusDto.setHistory(
        application.getState().getHistory().stream()
            .map(
                (history ->
                    new HistoryDto()
                        .state(HistoryDto.StateEnum.fromValue(history.getState()))
                        .timestamp(history.getTimeStamp().toString())))
            .toList());

    return claimantIdAndStatusDto;
  }

  public ApplicationCoordinatorStatusDto getApplicationStatusByClaimantId(String claimantId) {
    List<Application> applications = applicationRepository.findAllByClaimantId(claimantId);
    if (applications.isEmpty()) {
      throw new ApplicationNotFoundException(
          String.format("No application found for given claimant id %s", claimantId));
    }

    return toDto(applications.get(0));
  }

  public ClaimantIdAndApplicationStatus getClaimantIdAndStatus(String applicationId) {

    Optional<Application> application = applicationRepository.findById(applicationId);

    if (application.isEmpty()) {
      throw new ApplicationNotFoundException(
          String.format("No application found for given application id %s", applicationId));
    }

    ClaimantIdAndApplicationStatus claimantIdAndApplicationStatus =
        new ClaimantIdAndApplicationStatus();
    claimantIdAndApplicationStatus.setClaimantId(application.get().getClaimantId());
    claimantIdAndApplicationStatus.setApplicationStatus(
        ClaimantIdAndApplicationStatus.ApplicationStatusEnum.fromValue(
            applicationCoordinatorService
                .getApplicationState(application.get().getId())
                .getCurrent()));
    return claimantIdAndApplicationStatus;
  }

  public ClaimantIdAndStatusDto getClaimantIdAndStatusHistory(String applicationId) {

    Optional<Application> application = applicationRepository.findById(applicationId);

    if (application.isEmpty()) {
      throw new ApplicationNotFoundException(
          String.format("No application found for given application id %s", applicationId));
    }

    return toClaimantIdAndStatusDto(application.get());
  }

  private ApplicationCoordinatorStatusDto toDto(Application application) {
    ApplicationCoordinatorDto dto = applicationCoordinatorService
        .getApplicationCoordinatorDto(application.getId());
    String currentApplicationState =
        applicationCoordinatorService.getApplicationState(dto).getCurrent();

    var applicationStatusDto = new ApplicationCoordinatorStatusDto();
    applicationStatusDto.applicationId(application.getId());
    applicationStatusDto.submissionId(StringUtils.hasLength(dto.getSubmissionId())
        ? dto.getSubmissionId()
        : application.getSubmissionId());
    applicationStatusDto.setApplicationStatus(
        ApplicationCoordinatorStatusDto.ApplicationStatusEnum.fromValue(currentApplicationState));

    if (ApplicationState.valueOf(currentApplicationState).getValue()
        > ApplicationState.REGISTRATION.getValue()) {

      RegistrationSchema140 registrationSchema =
          registrationDataMarshaller.marshallRegistrationData(
              application.getRegistrationData().getData());
      applicationStatusDto.surname(registrationSchema.getPersonalDetails().getSurname());
      applicationStatusDto.forename(registrationSchema.getPersonalDetails().getFirstname());
      applicationStatusDto.dateOfBirth(registrationSchema.getPersonalDetails().getDob());
      applicationStatusDto.nationalInsuranceNumber(
          registrationSchema.getPersonalDetails().getNino());
      applicationStatusDto.postcode(
          registrationSchema.getPersonalDetails().getAddress().getPostcode());
    }

    return applicationStatusDto;
  }
}
