package uk.gov.dwp.health.pip.application.manager.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.dwp.health.mongo.changestream.extension.MongoChangeStreamIdentifier;
import uk.gov.dwp.health.pip.application.manager.entity.enums.Language;
import uk.gov.dwp.health.pip.application.manager.exception.ProhibitedActionException;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Document(collection = "application")
public class Application extends MongoChangeStreamIdentifier {

  @Id private String id;

  @Field(value = "claimant_id")
  @Indexed
  private String claimantId;

  @Field(value = "forename")
  private String forename;

  @Field(value = "surname")
  private String surname;

  @Field(value = "nino")
  @Indexed
  private String nino;

  @Field(value = "benefit_code")
  private String benefitCode;

  @Field(value = "effective_from")
  private LocalDate effectiveFrom;

  @Field(value = "effective_to")
  private LocalDate effectiveTo;

  @Field(value = "health_disability_data")
  private FormData healthDisabilityData;

  @Field(value = "registration_data")
  private FormData registrationData;

  @Field(value = "motability_data")
  private FormData motabilityData;

  @Field(value = "submission_id")
  private String submissionId;

  @Field(value = "drs_request_ids")
  private List<DrsRequestId> drsRequestId;

  @Field(value = "pipcs_motability_state")
  private State pipcsMotabilityState;

  @Field(value = "health_disability_state")
  private State healthDisabilityState;

  @Field(value = "pipcs_registration_state")
  private State pipcsRegistrationState;

  @Field(value = "language")
  private Language language;

  @Field(value = "audit")
  private Audit audit;

  @Field(value = "state")
  private State state;

  @Field(value = "legacy_application_reference")
  private LegacyApplicationReference legacyApplicationReference;

  @Field(value = "date_registration_submitted")
  private LocalDate dateRegistrationSubmitted;

  public FormData getRegistrationData() {
    if (registrationData == null) {
      registrationData = FormData.builder().build();
    }
    return registrationData;
  }

  public Audit getAudit() {
    if (audit == null) {
      audit = Audit.builder().build();
    }
    return audit;
  }

  public void setState(State state) {
    throw new ProhibitedActionException("Application State to be track in Coordinator");
  }
}
