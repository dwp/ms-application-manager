package uk.gov.dwp.health.pip.application.manager.service.mapper;

import java.util.Map;

class FormToLegacyMap {

  private static final Map<String, String> ALTERNATE_FORMAT_MAP;
  private static final Map<String, String> ACCOMMODATION_TYPE_MAP;

  static {
    ALTERNATE_FORMAT_MAP =
        Map.ofEntries(
            Map.entry("type1Uncontracted", "Braille type 1"),
            Map.entry("type2Contracted", "Braille type 2"),
            Map.entry("britishDvd", "British sign language DVD"),
            Map.entry("britishMpeg", "British sign language Mpeg"),
            Map.entry("irishDvd", "Irish sign language DVD"),
            Map.entry("irishMpeg", "Irish sign language Mpeg"),
            Map.entry("cassette", "Audio cassette tape"),
            Map.entry("cd", "Audio CD"),
            Map.entry("dvd", "Audio DVD"),
            Map.entry("mp3", "Audio MP3"),
            Map.entry("colouredPaper", "Coloured paper"),
            Map.entry("largePrint16Font", "Large print"),
            Map.entry("largePrintCustomFont", "Large print Custom font"),
            Map.entry("accessiblePDF", "Web accessible PDF"),
            Map.entry("email", "E-mail"),
            Map.entry("other", "Other Alternative Format"));
    ACCOMMODATION_TYPE_MAP =
        Map.ofEntries(
            Map.entry("hospital", "Hospital"),
            Map.entry("hospice", "Hospice"),
            Map.entry("carehome", "Care or Nursing Home"),
            Map.entry("other", "Other"));
  }

  static String getAlternateFormatLegacyValue(String formValue) {
    return ALTERNATE_FORMAT_MAP.get(formValue);
  }

  static String getAccommodationTypeLegacyValue(String formValue) {
    return ACCOMMODATION_TYPE_MAP.get(formValue);
  }

  private FormToLegacyMap() {}
}
