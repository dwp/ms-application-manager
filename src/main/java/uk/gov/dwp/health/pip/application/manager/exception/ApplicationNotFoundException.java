package uk.gov.dwp.health.pip.application.manager.exception;

public class ApplicationNotFoundException extends RuntimeException {
  public ApplicationNotFoundException(final String msg) {
    super(msg);
  }
}
