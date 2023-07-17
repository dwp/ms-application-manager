package uk.gov.dwp.health.pip.application.manager.constant;

public enum ApplicationState {
  REGISTRATION(100),
  HEALTH_AND_DISABILITY(200),
  SUBMITTED(300);

  private final int value;

  ApplicationState(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }
}
