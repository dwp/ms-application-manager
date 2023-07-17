package uk.gov.dwp.health.pip.application.manager.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.dwp.health.pip.application.manager.entity.enums.FormType;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FormData {

  @Field(value = "schema_version")
  private String schemaVersion;

  @Field(value = "type")
  private FormType type;

  @Field(value = "data")
  private Object data;

  @Field(value = "meta")
  private Object meta;
}
