package support;

import java.util.UUID;

public class TestFixtures {

  public static final String CLAIMANT_ID = UUID.randomUUID().toString();
  public static final String CLAIM_PATH = "/v1/claim";
  public static final String ENCODING = "utf-8";
  public static final String BENEFIT_TYPE = "PIP";
  public static final String APPLICATION_ID = UUID.randomUUID().toString();
  public static final String FORM_DATA = "FORM_DATA";
  public static final String SUBMISSION_ID = UUID.randomUUID().toString();
  public static final String DOCUMENT_ID = UUID.randomUUID().toString();
  public static final String CLAIM_COMPLETE_PATH = "/v1/claim/complete";
  public static final String DRS_REQUEST_ID = UUID.randomUUID().toString();
}
