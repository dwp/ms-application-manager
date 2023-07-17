package uk.gov.dwp.health.pip.application.manager.api;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.dwp.health.pip.application.manager.exception.ApplicationNotFoundException;
import uk.gov.dwp.health.pip.application.manager.exception.MessagingEventException;
import uk.gov.dwp.health.pip.application.manager.exception.ProhibitedActionException;
import uk.gov.dwp.health.pip.application.manager.exception.RegistrationDataNotValid;
import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import java.time.format.DateTimeParseException;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Tag("unit")
class AppControllerAdviseTest {

  private static AppControllerAdvise controllerAdvise;
  private static TestLogger testLogger;

  @BeforeAll
  static void setupSpec() {
    testLogger = TestLoggerFactory.getTestLogger(AppControllerAdvise.class);
    controllerAdvise = new AppControllerAdvise();
    ReflectionTestUtils.setField(controllerAdvise, "log", testLogger);
    MDC.remove("correlationId");
  }

  @Test
  void when_application_not_found_exception_raised_unauthorised_statue_returned() {
    var exp = new ApplicationNotFoundException("given claim not found");
    var actual = controllerAdvise.handleApplicationNotFoundException(exp);
    assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(actual.getBody()).isNull();
  }

  @Test
  void when_http_call_validation_exception_raised_fail_bad_request_status_returned() {
    var exp = new Exception("detail error message");
    var actual = controllerAdvise.handleBadRequestException(exp);
    assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(testLogger.getLoggingEvents())
        .containsExactlyInAnyOrder(
            new LoggingEvent(Level.WARN, "Request validation failed on input"),
            new LoggingEvent(
                Level.DEBUG,
                "{} {}",
                "Request validation failed on input",
                "detail error message"));
  }

  @Test
  void when_illegal_state_exception_raised_conflict_status_returned() {
    var exp = new IllegalStateException("conflict conflict");
    var actual = controllerAdvise.handleIllegalStateException(exp);
    assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    assertThat(testLogger.getLoggingEvents())
        .contains(new LoggingEvent(Level.ERROR, "Illegal state error {}", "conflict conflict"));
  }

  @Test
  void when_prohibited_action_exception_raised_forbidden_status_returned() {
    var exp = new ProhibitedActionException("action not allowed");
    var actual = controllerAdvise.handleProhibitedActionException(exp);
    assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    assertThat(testLogger.getLoggingEvents())
        .contains(
            new LoggingEvent(Level.WARN, "Requested action not allowed. {}", "action not allowed"));
  }

  @Test
  void when_messaging_event_exception_raised_internal_server_error_returned() {
    var exp = new MessagingEventException("error details");
    var actual = controllerAdvise.handleMessageEventException(exp);
    assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(testLogger.getLoggingEvents())
        .containsExactlyInAnyOrder(
            new LoggingEvent(Level.ERROR, "Failed to publish message"),
            new LoggingEvent(Level.DEBUG, "{} {}", "Failed to publish message", "error details"));
  }

  @Test
  void when_registration_data_validation_failed_unprocessable_error_returned() {
    var exp = new RegistrationDataNotValid("registration data not valid");
    var actual = controllerAdvise.handleRegistrationDataNotValid(exp);
    assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    assertThat(testLogger.getLoggingEvents())
        .contains(new LoggingEvent(Level.ERROR, "registration data not valid"));
  }

  @Test
  void when_date_time_parse_failed_then_unprocessable_error_returned() {
    DateTimeParseException exp =
        new DateTimeParseException("Unable to parse date time", "01-01-2000", 0);
    ResponseEntity<String> responseEntity = controllerAdvise.handleRegistrationDataNotValid(exp);
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    assertThat(testLogger.getLoggingEvents())
        .contains(new LoggingEvent(Level.ERROR, "Unable to parse date time"));
  }

  @Test
  void when_unknown_exception_thrown_return_internal_server_error() {
    var exp = new Exception("unknown server error");
    var actual = controllerAdvise.handleUnknown(exp);
    assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(testLogger.getLoggingEvents())
        .containsExactlyInAnyOrder(
            new LoggingEvent(Level.INFO, "Unknown server error {}", "unknown server error"));
  }

  @AfterEach
  void cleanUp() {
    testLogger.clearAll();
  }
}
