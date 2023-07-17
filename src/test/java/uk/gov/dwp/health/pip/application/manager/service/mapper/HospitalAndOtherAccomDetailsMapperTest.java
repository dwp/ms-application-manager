package uk.gov.dwp.health.pip.application.manager.service.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.health.pip.application.manager.exception.RegistrationDataNotValid;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.AddressSchema100;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.HospitalHospiceOrCarehome100;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.HospitalHospiceOrCarehome100.AccommodationType;
import uk.gov.dwp.health.pip.pipcsapimodeller.registration.carehome.HospitalAndOtherAccomDetails;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Tag("unit")
class HospitalAndOtherAccomDetailsMapperTest {
  private HospitalAndOtherAccomDetailsMapper hospitalAndOtherAccomDetailsMapper;
  private HospitalHospiceOrCarehome100 hospitalHospiceOrCarehome;

  @BeforeEach
  void beforeEach() {
    hospitalAndOtherAccomDetailsMapper =
        new HospitalAndOtherAccomDetailsMapper(
            new FormCommons(new ObjectMapper()), new PostcodeMapper());

    hospitalHospiceOrCarehome = new HospitalHospiceOrCarehome100();
  }

  @Nested
  class When_accommodation_provided {

    @BeforeEach
    void beforeEach() {
      hospitalHospiceOrCarehome.setAdditionalProperty("admissionDate", LocalDate.now().toString());
      hospitalHospiceOrCarehome.setAdditionalProperty("accommodationName", "accommodation-name");
      hospitalHospiceOrCarehome.setAdditionalProperty("address", getCareProviderAddress());
    }

    @Test
    void when_accommodation_type_care_home() {
      hospitalHospiceOrCarehome.setAccommodationType(AccommodationType.CAREHOME);

      HospitalAndOtherAccomDetails hospitalAndOtherAccomDetails =
          hospitalAndOtherAccomDetailsMapper.getHospitalAndOtherAccomDetails(
              hospitalHospiceOrCarehome);

      assertThat(hospitalAndOtherAccomDetails.getAccommodationType())
          .isEqualTo("Care or Nursing Home");
      verifyAdditionalProperties(hospitalAndOtherAccomDetails);
    }

    @Test
    void when_accommodation_type_hospice() {
      hospitalHospiceOrCarehome.setAccommodationType(AccommodationType.HOSPICE);

      HospitalAndOtherAccomDetails hospitalAndOtherAccomDetails =
          hospitalAndOtherAccomDetailsMapper.getHospitalAndOtherAccomDetails(
              hospitalHospiceOrCarehome);

      assertThat(hospitalAndOtherAccomDetails.getAccommodationType()).isEqualTo("Hospice");
      verifyAdditionalProperties(hospitalAndOtherAccomDetails);
    }

    @Test
    void when_accommodation_type_hospital() {
      hospitalHospiceOrCarehome.setAccommodationType(AccommodationType.HOSPITAL);

      HospitalAndOtherAccomDetails hospitalAndOtherAccomDetails =
          hospitalAndOtherAccomDetailsMapper.getHospitalAndOtherAccomDetails(
              hospitalHospiceOrCarehome);

      assertThat(hospitalAndOtherAccomDetails.getAccommodationType()).isEqualTo("Hospital");
      verifyAdditionalProperties(hospitalAndOtherAccomDetails);
    }

    @Test
    void when_accommodation_type_other() {
      hospitalHospiceOrCarehome.setAccommodationType(AccommodationType.OTHER);

      HospitalAndOtherAccomDetails hospitalAndOtherAccomDetails =
          hospitalAndOtherAccomDetailsMapper.getHospitalAndOtherAccomDetails(
              hospitalHospiceOrCarehome);

      assertThat(hospitalAndOtherAccomDetails.getAccommodationType()).isEqualTo("Other");
      verifyAdditionalProperties(hospitalAndOtherAccomDetails);
    }

    private AddressSchema100 getCareProviderAddress() {
      AddressSchema100 addressSchema100 = new AddressSchema100();
      addressSchema100.setLine1("address-line-1");
      addressSchema100.setLine2("address-line-2");
      addressSchema100.setTown("town");
      addressSchema100.setPostcode("ls1 1ab");
      addressSchema100.setCounty("county");
      addressSchema100.setCountry(AddressSchema100.CountrySchema100.ENGLAND);
      return addressSchema100;
    }

    private void verifyAdditionalProperties(
        HospitalAndOtherAccomDetails hospitalAndOtherAccomDetails) {
      assertThat(hospitalAndOtherAccomDetails.getAdmissionDate()).isEqualTo(LocalDate.now());
      assertThat(hospitalAndOtherAccomDetails.getHospitalAndOtherAccomAddressDetails().getLine1())
          .isEqualTo("accommodation-name");
      assertThat(hospitalAndOtherAccomDetails.getHospitalAndOtherAccomAddressDetails().getLine2())
          .isEqualTo("address-line-1");
      assertThat(hospitalAndOtherAccomDetails.getHospitalAndOtherAccomAddressDetails().getLine3())
          .isEqualTo("address-line-2");
      assertThat(
              hospitalAndOtherAccomDetails.getHospitalAndOtherAccomAddressDetails().getTownOrCity())
          .isEqualTo("town");
      assertThat(
              hospitalAndOtherAccomDetails.getHospitalAndOtherAccomAddressDetails().getPostcode())
          .isEqualTo("ls1 1ab");
      assertThat(hospitalAndOtherAccomDetails.getHospitalAndOtherAccomAddressDetails().getCounty())
          .isEqualTo("county");
      assertThat(hospitalAndOtherAccomDetails.getHospitalAndOtherAccomAddressDetails().getCountry())
          .isEqualTo("England");
    }
  }

  @Test
  void when_accommodation_type_none() {
    hospitalHospiceOrCarehome.setAccommodationType(AccommodationType.NONE);

    HospitalAndOtherAccomDetails hospitalAndOtherAccomDetails =
        hospitalAndOtherAccomDetailsMapper.getHospitalAndOtherAccomDetails(
            hospitalHospiceOrCarehome);

    assertThat(hospitalAndOtherAccomDetails).isNull();
  }

  @Test
  void when_accommodation_type_null() {
    hospitalHospiceOrCarehome.setAccommodationType(null);

    assertThatThrownBy(
            () ->
                hospitalAndOtherAccomDetailsMapper.getHospitalAndOtherAccomDetails(
                    hospitalHospiceOrCarehome))
        .isInstanceOf(RegistrationDataNotValid.class)
        .hasMessage("Registration data not valid. Accommodation type not provided.");
  }

  @Test
  void when_accommodation_type_hospital_no_additional_properties() {
    hospitalHospiceOrCarehome.setAccommodationType(AccommodationType.HOSPITAL);

    HospitalAndOtherAccomDetails hospitalAndOtherAccomDetails =
        hospitalAndOtherAccomDetailsMapper.getHospitalAndOtherAccomDetails(
            hospitalHospiceOrCarehome);

    assertThat(hospitalAndOtherAccomDetails.getAccommodationType()).isEqualTo("Hospital");
    assertThat(hospitalAndOtherAccomDetails.getAdmissionDate()).isNull();
    assertThat(hospitalAndOtherAccomDetails.getHospitalAndOtherAccomAddressDetails()).isNull();
  }
}
