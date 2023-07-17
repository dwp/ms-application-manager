package uk.gov.dwp.health.pip.application.manager.service.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.AddressSchema100;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.BankDetails100;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.Contact100;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.HealthProfessionalsDetails100;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.HospitalHospiceOrCarehome100;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.MotabilitySchemeSchema100;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.PersonalDetailsSchema100;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.RegistrationSchema110;
import uk.gov.dwp.health.pip.pipcsapimodeller.Pip1RegistrationForm;
import uk.gov.dwp.health.pip.pipcsapimodeller.registration.carehome.HospitalAndOtherAccomDetails;
import uk.gov.dwp.health.pip.pipcsapimodeller.registration.common.Address;
import uk.gov.dwp.health.pip.pipcsapimodeller.registration.common.ContactDetails;
import uk.gov.dwp.health.pip.pipcsapimodeller.registration.health.HealthDetails;
import uk.gov.dwp.health.pip.pipcsapimodeller.registration.health.Professional;
import uk.gov.dwp.health.pip.pipcsapimodeller.registration.pii.BankDetails;
import uk.gov.dwp.health.pip.pipcsapimodeller.registration.pii.PersonalDetails;
import uk.gov.dwp.health.pip.pipcsapimodeller.registration.type.AltFormat;
import uk.gov.dwp.health.pip.pipcsapimodeller.registration.type.YesNoDontKnow;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
@Service
@Slf4j
public class RegistrationDataMapperForPipcs {

  private final HospitalAndOtherAccomDetailsMapper hospitalAndOtherAccomDetailsMapper;
  private final PostcodeMapper postcodeMapper;
  private final ResidencyDetailsMapper residencyDetailsMapper;

  private final PipcsApiModelValidator pipcsApiModelValidator;

  public Pip1RegistrationForm mapRegistrationData(
      String applicationId,
      LocalDate dateRegistrationSubmitted,
      RegistrationSchema110 registrationSchema) {

    YesNoDontKnow altFormatRequiredValue = YesNoDontKnow.NO;
    AltFormat alternateFormatType = null;
    String altFormatAdditionalInfo = null;
    if (registrationSchema.getPersonalDetails().getAlternateFormat() != null) {
      altFormatRequiredValue = YesNoDontKnow.YES;
      alternateFormatType = getAlternateFormatType(registrationSchema);
      altFormatAdditionalInfo = getAltFormatAdditionalInfo(registrationSchema);
    }

    Pip1RegistrationForm pip1RegistrationForm =
        Pip1RegistrationForm.builder()
            .pipApplyApplicationId(applicationId)
            .dateClaimSubmitted(dateRegistrationSubmitted)
            .personalDetails(getPersonalDetails(registrationSchema))
            .bankDetails(getBankDetails(registrationSchema))
            .motabilitySchemeInfo(getMotabilitySchemeInfo(registrationSchema))
            .difficultyCommunicating(getHelpWithCommunication(registrationSchema))
            .helpCompletingLetters(getHelpCompletingLetters(registrationSchema))
            .helpCompletingLetterWho(getHelpCompletingLettersWho(registrationSchema))
            .residencyDetails(residencyDetailsMapper.mapResidencyDetails(registrationSchema))
            .altFormatRequired(altFormatRequiredValue)
            .altFormatType(alternateFormatType)
            .altFormatAdditionalInfo(altFormatAdditionalInfo)
            .healthDetails(getHealthDetails(registrationSchema))
            .hospitalAndOtherAccomDetails(getHospitalAndOtherAccomDetails(registrationSchema))
            .build();

    pipcsApiModelValidator.validate(pip1RegistrationForm);

    return pip1RegistrationForm;
  }

  private static String getMotabilitySchemeInfo(
      final RegistrationSchema110 registrationSchema) {
    final MotabilitySchemeSchema100 scheme = registrationSchema.getMotabilityScheme();
    return scheme != null && YesNoDontKnow.YES.toString().equalsIgnoreCase(
        scheme.getReceiveMotabilityInformation()
    ) ? YesNoDontKnow.YES.toString()
        : scheme != null && YesNoDontKnow.NO.toString().equalsIgnoreCase(
        scheme.getReceiveMotabilityInformation()
    ) ? YesNoDontKnow.NO.toString() : null;
  }

  private BankDetails getBankDetails(final RegistrationSchema110 registrationSchema) {
    final BankDetails100 bankDetails = registrationSchema.getPersonalDetails().getBankDetails();
    return bankDetails != null
        && YesNoDontKnow.YES.toString().equalsIgnoreCase(bankDetails.getEnterBankDetails())
        ? BankDetails.builder()
            .accountName((String) bankDetails.getAdditionalProperties().get("accountName"))
            .accountNumber((String) bankDetails.getAdditionalProperties().get("accountNumber"))
            .sortCode((String) bankDetails.getAdditionalProperties().get("sortCode"))
            .buildingSocietyRollNumber(
                (String) bankDetails.getAdditionalProperties().get("rollNumber")
            )
            .build()
        : null;
  }

  private YesNoDontKnow getHelpWithCommunication(RegistrationSchema110 registrationSchema) {
    return toYesNoDontKnow(registrationSchema.getAdditionalSupport().getHelpCommunicating());
  }

  private PersonalDetails getPersonalDetails(RegistrationSchema110 registrationSchema) {
    PersonalDetailsSchema100 personalDetails = registrationSchema.getPersonalDetails();

    return PersonalDetails.builder()
        .firstName(personalDetails.getFirstname())
        .surname(personalDetails.getSurname())
        .nino(personalDetails.getNino())
        .residential(getAddress(personalDetails.getAddress()))
        .correspondence(
            personalDetails.getAlternativeAddress() != null
                ? getAddress(personalDetails.getAlternativeAddress())
                : null)
        .contactDetails(getContactDetails(registrationSchema))
        .dob(LocalDate.parse(personalDetails.getDob()))
        .build();
  }

  private Address getAddress(AddressSchema100 addressSchema) {
    if (addressSchema == null) {
      return null;
    }
    var postcode = postcodeMapper.mapPostcode(addressSchema.getPostcode());
    return Address.builder()
        .line1(addressSchema.getLine1())
        .line2(addressSchema.getLine2())
        .townOrCity(addressSchema.getTown())
        .postcode(postcode)
        .county(addressSchema.getCounty())
        .country(addressSchema.getCountry().toString())
        .build();
  }

  private YesNoDontKnow getHelpCompletingLetters(RegistrationSchema110 registrationSchema) {
    return registrationSchema.getAdditionalSupport().getHelperDetails() != null
        ? YesNoDontKnow.YES
        : YesNoDontKnow.NO;
  }

  private String getHelpCompletingLettersWho(RegistrationSchema110 registrationSchema) {
    AtomicReference<String> who = new AtomicReference<>();
    Optional.ofNullable(registrationSchema.getAdditionalSupport().getHelperDetails())
        .ifPresent(
            helperDetails ->
                who.set(helperDetails.getFirstname() + " " + helperDetails.getSurname()));
    return who.get();
  }

  private ContactDetails getContactDetails(RegistrationSchema110 registrationSchema) {
    Contact100 contactDetails = registrationSchema.getPersonalDetails().getContact();
    YesNoDontKnow smsOptAnswer = toYesNoDontKnow(contactDetails.getSmsUpdates());
    return ContactDetails.builder()
        .mobilePhoneNumber(contactDetails.getMobileNumber())
        .homePhoneNumber(contactDetails.getAlternativeNumber())
        .textPhoneNumber(contactDetails.getTextphone())
        .smsOptOut(
            smsOptAnswer == null
                ? null
                : (YesNoDontKnow.YES == smsOptAnswer ? YesNoDontKnow.NO : YesNoDontKnow.YES))
        .build();
  }

  private AltFormat getAlternateFormatType(RegistrationSchema110 registrationSchema) {
    Map<String, Object> additionalProperties =
        registrationSchema.getPersonalDetails().getAlternateFormat().getAdditionalProperties();

    Optional<String> alternateFormatLegacyValue =
        additionalProperties.entrySet().stream()
            .filter(entry -> !entry.getKey().equals("alternateFormatAdditionalInfo"))
            .map(entry -> FormToLegacyMap.getAlternateFormatLegacyValue((String) entry.getValue()))
            .findFirst();

    return alternateFormatLegacyValue.map(AltFormat::fromValue).orElse(null);
  }

  private String getAltFormatAdditionalInfo(RegistrationSchema110 registrationSchema) {
    return (String)
        registrationSchema
            .getPersonalDetails()
            .getAlternateFormat()
            .getAdditionalProperties()
            .get("alternateFormatAdditionalInfo");
  }

  private HealthDetails getHealthDetails(RegistrationSchema110 registrationSchema) {
    final var yourHealth = registrationSchema.getAboutYourHealth();
    final var consent =
        yourHealth.getHcpContactConsent()
            && (yourHealth.getHcpContactConsent() ? yourHealth.getHcpShareConsent() : false);
    final var condition = getHealthConditions(yourHealth.getHealthConditions());
    final var healthProfessionalsDetails = yourHealth.getHealthProfessionalsDetails();

    if (consent) {
      return HealthDetails.builder()
          .hp1(
              healthProfessionalsDetails.size() > 0
                  ? getProfessional(healthProfessionalsDetails.get(0))
                  : null)
          .hp2(
              healthProfessionalsDetails.size() > 1
                  ? getProfessional(healthProfessionalsDetails.get(1))
                  : null)
          .consent(YesNoDontKnow.YES)
          .condition(condition)
          .build();
    } else {
      return HealthDetails.builder().consent(YesNoDontKnow.NO).condition(condition).build();
    }
  }

  private Professional getProfessional(HealthProfessionalsDetails100 healthProfessionals) {
    if (healthProfessionals != null) {
      return Professional.builder()
          .fullName(healthProfessionals.getName())
          .phoneNumber(healthProfessionals.getPhoneNumber())
          .address(getAddress(healthProfessionals.getAddress()))
          .build();
    }
    return null;
  }

  private String getHealthConditions(List<String> healthConditions) {
    if (healthConditions.isEmpty()) {
      return null;
    }
    return healthConditions.get(0);
  }

  private YesNoDontKnow toYesNoDontKnow(Boolean bool) {
    if (bool == Boolean.TRUE) {
      return YesNoDontKnow.YES;
    } else if (bool == Boolean.FALSE) {
      return YesNoDontKnow.NO;
    }
    return null;
  }

  private HospitalAndOtherAccomDetails getHospitalAndOtherAccomDetails(
      RegistrationSchema110 registrationSchema) {
    HospitalHospiceOrCarehome100 hospitalHospiceOrCarehome =
        registrationSchema.getAboutYourHealth().getHospitalHospiceOrCarehome();

    return hospitalAndOtherAccomDetailsMapper.getHospitalAndOtherAccomDetails(
        hospitalHospiceOrCarehome);
  }
}
