package uk.gov.dwp.health.pip.application.manager.entity;

import lombok.Getter;
import uk.gov.dwp.health.pip.application.manager.entity.enums.BankDetailsValidity;

import java.util.LinkedList;
import java.util.List;

@Getter
public class BankDetailsValidityList {

  private BankDetailsValidity[] results = new BankDetailsValidity[0];

  public BankDetailsValidityList addResult(final BankDetailsValidity result) {
    if (newResult(result)) {
      final BankDetailsValidity[] oldResults = results;
      results = new BankDetailsValidity[oldResults.length + 1];
      java.lang.System.arraycopy(oldResults, 0, results, 0, oldResults.length);
      results[oldResults.length] = result;
    }
    return this;
  }

  private boolean newResult(final BankDetailsValidity newResult) {
    boolean isNew = true;
    for (final BankDetailsValidity result : results) {
      if (result == newResult) {
        isNew = false;
        break;
      }
    }
    return isNew;
  }

  public List<String> getResultsAsStrings() {
    final List<String> list = new LinkedList<>();
    for (final BankDetailsValidity result : results) {
      list.add(result.toString());
    }
    return list;
  }
}
