package uk.gov.dwp.health.pip.application.manager.api;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientException;
import uk.gov.dwp.health.pip.application.manager.exception.ApplicationNotFoundException;
import uk.gov.dwp.health.pip.application.manager.exception.MessagingEventException;
import uk.gov.dwp.health.pip.application.manager.exception.ProhibitedActionException;
import uk.gov.dwp.health.pip.application.manager.exception.RegistrationDataNotValid;
import uk.gov.dwp.health.pip.application.manager.openapi.healthdisability.v1.dto.ErrorResponseObject;

import java.time.format.DateTimeParseException;

@Component
@ControllerAdvice
public class AppControllerAdvise {

  private static Logger log = LoggerFactory.getLogger(AppControllerAdvise.class);

  @ExceptionHandler({ProhibitedActionException.class})
  public ResponseEntity<Void> handleProhibitedActionException(ProhibitedActionException ex) {
    log.warn("Requested action not allowed. {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
  }

  @ExceptionHandler({ApplicationNotFoundException.class})
  public ResponseEntity<Void> handleApplicationNotFoundException(ApplicationNotFoundException ex) {
    log.warn(ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }

  @ExceptionHandler(
      value = {
        ConstraintViolationException.class,
        IllegalArgumentException.class,
        MethodArgumentNotValidException.class,
        HttpMessageNotReadableException.class,
        MissingServletRequestParameterException.class
      })
  public final ResponseEntity<ErrorResponseObject> handleBadRequestException(Exception ex) {
    final var message = "Request validation failed on input";
    log.debug("{} {}", message, ex.getMessage());
    log.warn(message);
    var body = new ErrorResponseObject();
    body.setMessage(message);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
  }

  @ExceptionHandler({IllegalStateException.class})
  public ResponseEntity<Void> handleIllegalStateException(IllegalStateException ex) {
    log.error("Illegal state error {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).build();
  }

  @ExceptionHandler({RegistrationDataNotValid.class, DateTimeParseException.class})
  public ResponseEntity<String> handleRegistrationDataNotValid(Exception ex) {
    log.error(ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ex.getMessage());
  }

  @ExceptionHandler({MessagingEventException.class})
  public ResponseEntity<Void> handleMessageEventException(MessagingEventException ex) {
    final var message = "Failed to publish message";
    log.debug("{} {}", message, ex.getMessage());
    log.error(message);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
  }

  @ExceptionHandler(value = {Exception.class})
  public ResponseEntity<String> handleUnknown(Exception ex) {
    log.info("Unknown server error {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
  }
}
