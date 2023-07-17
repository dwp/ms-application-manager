package uk.gov.dwp.health.pip.application.manager.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.dwp.health.pip.application.manager.entity.enums.System;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LegacyApplicationReference {

  @Field(value = "system")
  private System system;

  @Field(value = "application_reference")
  private String applicationReference;
}
