package uk.gov.dwp.health.pip.application.manager.exception;

public class ValidationException extends RuntimeException {
  public ValidationException(final String msg) {
    super(msg);
  }
}
