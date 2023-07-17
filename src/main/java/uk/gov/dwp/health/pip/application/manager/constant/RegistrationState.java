package uk.gov.dwp.health.pip.application.manager.constant;

import uk.gov.dwp.health.pip.application.manager.exception.RegistrationDataNotValid;

import java.util.Arrays;

public enum RegistrationState {

  VALIDATION_FAILED(-1, "Validation failed"),
  PENDING(100, "Pending"),
  SUBMITTING(200, "Submitting"),
  DISALLOWED(300, "Disallowed"),
  WITHDRAWN(400, "Withdrawn"),
  RETRIES_EXHAUSTED(500, "Retries exhausted"),
  SUBMITTED(600, "Submitted");

  private final int value;
  private final String label;

  RegistrationState(int value, String label) {
    this.value = value;
    this.label = label;
  }

  public static RegistrationState getRegistrationStateByLabel(String label) {
    return Arrays.stream(values()).filter(v ->
        v.getLabel().equalsIgnoreCase(label)).findFirst().orElseThrow(() -> {
          throw new RegistrationDataNotValid("Registration state is not recognised");
        });
  }

  public String getLabel() {
    return label;
  }

  public int getValue() {
    return value;
  }
}
