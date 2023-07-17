package uk.gov.dwp.health.pip.application.manager.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Audit {

  @Field(value = "created")
  private Instant created;

  @Field(value = "last_modified")
  private Instant lastModified;
}
