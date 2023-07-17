package uk.gov.dwp.health.pip.application.manager.service.mapper;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Tag("unit")
class PostcodeMapperTest {

  @Test
  void when_postcode_with_space() {
    var postcodeMapper = new PostcodeMapper();
    var postcode = postcodeMapper.mapPostcode("AA1 1AA");

    assertThat(postcode).isEqualTo("AA1 1AA");
  }

  @Test
  void when_postcode_with_no_space() {
    var postcodeMapper = new PostcodeMapper();
    var postcode = postcodeMapper.mapPostcode("AA11AA");

    assertThat(postcode).isEqualTo("AA1 1AA");
  }

}
