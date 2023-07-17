package uk.gov.dwp.health.pip.application.manager.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class State {

  @Field(value = "current_state")
  private String current;

  @Field(value = "history")
  private List<History> history;

  public void addHistory(History history) {
    current = history.getState();
    if (this.history == null) {
      this.history = new LinkedList<>();
    }
    this.history.add(history);
  }
}
