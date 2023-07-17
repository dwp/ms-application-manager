package uk.gov.dwp.health.pip.application.manager.entity;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.health.pip.application.manager.entity.enums.BankDetailsValidity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unit")
public class BankDetailsValidityListTest {

  @Test
  public void addResult() {
    final BankDetailsValidityList list = new BankDetailsValidityList();
    list.addResult(BankDetailsValidity.VALID);
    list.addResult(BankDetailsValidity.ROLL_NUMBER_REQUIRED);
    list.addResult(BankDetailsValidity.ROLL_NUMBER_REQUIRED);
    assertEquals(2, list.getResults().length);
    assertEquals(BankDetailsValidity.VALID, list.getResults()[0]);
    assertEquals(BankDetailsValidity.ROLL_NUMBER_REQUIRED, list.getResults()[1]);
  }

  @Test
  public void getResultsAsStrings() {
    final BankDetailsValidityList list = new BankDetailsValidityList();
    list.addResult(BankDetailsValidity.VALID);
    list.addResult(BankDetailsValidity.ROLL_NUMBER_REQUIRED);
    final List<String> resultsAsStrings = list.getResultsAsStrings();
    assertEquals(BankDetailsValidity.VALID.toString(), resultsAsStrings.get(0));
    assertEquals(BankDetailsValidity.ROLL_NUMBER_REQUIRED.toString(), resultsAsStrings.get(1));
  }

}
