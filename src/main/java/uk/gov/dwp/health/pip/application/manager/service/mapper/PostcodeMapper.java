package uk.gov.dwp.health.pip.application.manager.service.mapper;

import org.springframework.stereotype.Component;

@Component
class PostcodeMapper {

  String mapPostcode(String postcode) {
    var firstPart = postcode.substring(0, postcode.length() - 3).trim();
    var secondPart = postcode.substring(postcode.length() - 3);

    return String.join(" ", firstPart, secondPart);
  }
}
