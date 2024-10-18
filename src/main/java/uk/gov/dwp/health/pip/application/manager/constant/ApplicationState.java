package uk.gov.dwp.health.pip.application.manager.constant;

public enum ApplicationState {
  REGISTRATION(100),
  HEALTH_AND_DISABILITY(200),
  SUBMITTED(300),
  // new states from coordinator
  DISALLOWED_NON_RETURN(400),
  DISALLOWED_NOT_ELIGIBLE(500),
  WITHDRAWN(600),
  REFERRED_TO_ASSESSMENT(700),
  RETURNED_FROM_ASSESSMENT(800),
  READY_FOR_DECISION(900),
  DECISION_AWARDED(1000),
  DECISION_NOT_AWARDED(1100),
  ROUTED_TO_PIPCS(1200),
  REBUILT_IN_PIPCS(1300);

  private final int value;

  ApplicationState(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }
}
