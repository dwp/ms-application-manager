package uk.gov.dwp.health.pip.application.manager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.pip.application.manager.constant.ApplicationState;
import uk.gov.dwp.health.pip.application.manager.entity.Application;
import uk.gov.dwp.health.pip.application.manager.exception.ApplicationNotFoundException;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v1.dto.ApplicationStatusDto;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v1.dto.ClaimantIdAndStatusDto;
import uk.gov.dwp.health.pip.application.manager.repository.ApplicationRepository;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class ApplicationStatusGetter {

  private final ApplicationRepository applicationRepository;
  private final RegistrationDataMarshaller registrationDataMarshaller;

  public ApplicationStatusDto getApplicationStatusByClaimantId(String claimantId) {
    var applications = applicationRepository.findAllByClaimantId(claimantId);
    if (applications.isEmpty()) {
      throw new ApplicationNotFoundException(
          String.format("No application found for given claimant id %s", claimantId));
    }

    return toDto(applications.get(0));
  }

  public ClaimantIdAndStatusDto getClaimantIdAndStatus(String applicationId) {

    var application = applicationRepository.findById(applicationId);
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
    claimantIdAndStatusDto.setApplicationStatus(
            ClaimantIdAndStatusDto.ApplicationStatusEnum
                    .fromValue(application.getState().getCurrent()));
    return claimantIdAndStatusDto;
  }

  private ApplicationStatusDto toDto(Application application) {
    var currentApplicationState = application.getState().getCurrent();

    var applicationStatusDto = new ApplicationStatusDto();
    applicationStatusDto.applicationId(application.getId());
    applicationStatusDto.submissionId(application.getSubmissionId());
    applicationStatusDto.setApplicationStatus(
        ApplicationStatusDto.ApplicationStatusEnum.fromValue(currentApplicationState));

    if (ApplicationState.valueOf(currentApplicationState).getValue()
        > ApplicationState.REGISTRATION.getValue()) {

      var registrationSchema =
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
