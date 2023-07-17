package uk.gov.dwp.health.pip.application.manager.exception;

public class RegistrationDataNotValid extends RuntimeException {
  public RegistrationDataNotValid(final String msg) {
    super(msg);
  }
}
