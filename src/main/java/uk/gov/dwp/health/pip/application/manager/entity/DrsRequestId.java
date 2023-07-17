package uk.gov.dwp.health.pip.application.manager.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DrsRequestId {

  @Field(value = "drs_request_id")
  private String drsRequestId;

  @Field(value = "timestamp")
  private LocalDateTime timestamp;
}
