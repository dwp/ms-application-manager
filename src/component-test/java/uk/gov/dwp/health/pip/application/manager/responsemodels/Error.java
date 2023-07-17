package uk.gov.dwp.health.pip.application.manager.responsemodels;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Data
@NoArgsConstructor
public class Error {
  private String message;
}
