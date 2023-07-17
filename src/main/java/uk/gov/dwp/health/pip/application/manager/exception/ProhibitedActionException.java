package uk.gov.dwp.health.pip.application.manager.exception;

public class ProhibitedActionException extends RuntimeException {
  public ProhibitedActionException(final String msg) {
    super(msg);
  }
}
