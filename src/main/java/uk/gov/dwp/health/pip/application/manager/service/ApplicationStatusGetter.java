package uk.gov.dwp.health.pip.application.manager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.pip.application.manager.constant.ApplicationState;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.exception.ApplicationNotFoundException;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.RegistrationSchema140;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v1.dto.ApplicationStatusDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v1.dto.ClaimantIdAndApplicationStatus;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v2.dto.ClaimantIdAndStatusDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v2.dto.HistoryDto;
import uk.gov.dwp.health.pip.application.manager.repository.ApplicationRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class ApplicationStatusGetter {

  private final ApplicationRepository applicationRepository;
  private final RegistrationDataMarshaller registrationDataMarshaller;
  private final ApplicationCoordinatorService applicationCoordinatorService;

  public ApplicationStatusDto getApplicationStatusByClaimantId(String claimantId) {
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

  private ApplicationStatusDto toDto(Application application) {
    String currentApplicationState =
        applicationCoordinatorService.getApplicationState(application.getId()).getCurrent();

    var applicationStatusDto = new ApplicationStatusDto();
    applicationStatusDto.applicationId(application.getId());
    applicationStatusDto.submissionId(application.getSubmissionId());
    applicationStatusDto.setApplicationStatus(
        ApplicationStatusDto.ApplicationStatusEnum.fromValue(currentApplicationState));

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
