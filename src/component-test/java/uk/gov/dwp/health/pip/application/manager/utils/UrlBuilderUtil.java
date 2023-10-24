package uk.gov.dwp.health.pip.application.manager.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static io.restassured.RestAssured.baseURI;

public class UrlBuilderUtil {

  public static String buildPostApplicationUrl() {
    return baseURI + "/v1/application";
  }

  public static String buildGetRegistrationUrl(String claimantId) {
    return baseURI + "/v1/application/registration?claimantId=" + claimantId;
  }

  public static String buildGetRegistrationByIdUrl(String applicationId) {
    return baseURI + "/v1/application/" + applicationId + "/registration";
  }

  public static String buildGetClaimantIdAndStatusByIdUrl(String applicationId) {
    return baseURI + "/v1/application/" + applicationId + "/status";
  }

  public static String buildGetRegistrationByIdV2Url(String applicationId) {
    return baseURI + "/v2/application/" + applicationId + "/registration";
  }

  public static String buildGetRegistrationByIdV3Url(String applicationId) {
    return baseURI + "/v3/application/" + applicationId + "/registration";
  }

  public static String buildGetApplicationStatusUrl(String claimantId) {
    return baseURI + "/v1/application/status?claimantId=" + claimantId;
  }

  public static String buildGetApplicationIdByNinoUrl() {
    return baseURI + "/v1/application/matcher";
  }

  public static String buildPutRegistrationUrl(String applicationId) {
    return baseURI + "/v1/application/" + applicationId + "/registration";
  }

  public static String buildPutRegistrationSubmissionUrl(String applicationId) {
    return baseURI + "/v1/application/" + applicationId + "/registration/submission";
  }

  public static String buildPutHealthDisabilityUrl(String applicationId) {
    return baseURI + "/v1/application/" + applicationId + "/healthdisability";
  }

  public static String buildPostBankDetailsUrl() {
    return baseURI + "/v1/validate";
  }

  public static String buildGetClaimantsWithState(
      int batchSize,
      int page,
      final String state,
      final LocalDateTime timeFrom,
      final LocalDateTime timeTo) {
    return buildGetClaimantsWithState(
        batchSize, page, state, formatIsoDateTime(timeFrom), formatIsoDateTime(timeTo));
  }

  public static String buildGetClaimantsWithState(
      int batchSize, int page, String state, String timeFromString, String timeToString) {
    return baseURI
        + "/v1/application/healthdisability/"
        + state
        + "?pageSize="
        + batchSize
        + "&page="
        + page
        + "&timestampFrom="
        + timeFromString
        + "&timestampTo="
        + timeToString;
  }

  public static String formatIsoDateTime(final LocalDateTime time) {
    return DateTimeFormatter.ISO_DATE_TIME.format(time);
  }

  public static String buildPutHealthDisabilitySubmissionV2Url(
      String applicationId, String submissionId) {
    return baseURI
        + "/v2/application/"
        + applicationId
        + "/healthdisability/submission/"
        + submissionId;
  }
}
