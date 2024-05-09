package uk.gov.dwp.health.pip.application.manager.api.bankdetails;

import org.junit.jupiter.api.Test;
import uk.gov.dwp.health.pip.application.manager.api.ApiTest;
import uk.gov.dwp.health.pip.application.manager.entity.BankDetailsValidityList;
import uk.gov.dwp.health.pip.application.manager.external.bankdetails.v3.DefaultApi;
import uk.gov.dwp.health.pip.application.manager.service.BankDetailsValidatorV2;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static uk.gov.dwp.health.pip.application.manager.entity.enums.BankDetailsValidity.INVALID_SORT_CODE_ACCOUNT_NUMBER_DATA_FORMAT;
import static uk.gov.dwp.health.pip.application.manager.entity.enums.BankDetailsValidity.ROLL_NUMBER_FORMAT;
import static uk.gov.dwp.health.pip.application.manager.entity.enums.BankDetailsValidity.ROLL_NUMBER_REQUIRED;
import static uk.gov.dwp.health.pip.application.manager.entity.enums.BankDetailsValidity.VALID;

/** Uses the validator component to call the wiremock endpoints directly */
public class BankDetailsValidator2IT extends ApiTest {

    public static final String INVALID_DETAILS_ACCOUNT_NUMBER = "20000091";
    public static final String INVALID_ACCOUNT_NUMBER = "20000004";
    public static final String INVALID_ACCOUNT_NUMBER2 = "20000265";
    public static final String INVALID_ACCOUNT_NUMBER_UNEXPECTED_WARNING = "22099999";
    public static final String ACCOUNT_NUMBER = "12341234";
    public static final String SORT_CODE = "123123";
    public static final String ROLL_NUMBER = "32233223322332";
    public static final String CORRELATION_ID = "43322334455443";
    public static final String CONSUMER_ID = "5";

    @Test
    public void invalidBankDetails() {
        final BankDetailsValidatorV2 bankDetailsValidator = getBankDetailsValidator();
        final BankDetailsValidityList validate = bankDetailsValidator.validate(INVALID_DETAILS_ACCOUNT_NUMBER, SORT_CODE, ROLL_NUMBER, CONSUMER_ID, CORRELATION_ID);
        assertThat(validate.getResults().length).isEqualTo(1);
        assertThat(validate.getResults()[0]).isEqualTo(ROLL_NUMBER_FORMAT);
    }

    @Test
    public void invalidAccountNumber() {
        final BankDetailsValidatorV2 bankDetailsValidator = getBankDetailsValidator();
        final BankDetailsValidityList validate = bankDetailsValidator.validate(INVALID_ACCOUNT_NUMBER, SORT_CODE, ROLL_NUMBER, CONSUMER_ID, CORRELATION_ID);
        assertThat(validate.getResults().length).isEqualTo(1);
        assertThat(validate.getResults()[0]).isEqualTo(INVALID_SORT_CODE_ACCOUNT_NUMBER_DATA_FORMAT);
    }

    @Test
    public void validAccountNumberUnexpectedWarningReturned() {
        final BankDetailsValidatorV2 bankDetailsValidator = getBankDetailsValidator();
        final BankDetailsValidityList validate = bankDetailsValidator.validate(INVALID_ACCOUNT_NUMBER_UNEXPECTED_WARNING, SORT_CODE, ROLL_NUMBER, CONSUMER_ID, CORRELATION_ID);
        assertThat(validate.getResults().length).isEqualTo(1);
        assertThat(validate.getResults()[0]).isEqualTo(VALID);
    }

    @Test //htgt-3481 add warning/002 to the ignore list
    public void invalidAccountNumberReturningMultipleWarnings() {
        final BankDetailsValidatorV2 bankDetailsValidator = getBankDetailsValidator();
        final BankDetailsValidityList validate = bankDetailsValidator.validate(INVALID_ACCOUNT_NUMBER2, SORT_CODE, ROLL_NUMBER, CONSUMER_ID, CORRELATION_ID);
        // this account num 20000265 returns 2 warnings - a 2 and a 65. We ignore the 2 so...:
        assertThat(validate.getResults().length).isEqualTo(1);
        // warnings 65 => ROLL_NUMBER_REQUIRED
        assertThat(validate.getResults()[0]).isEqualTo(ROLL_NUMBER_REQUIRED);
    }

    @Test
    public void shouldReturn200StatusCodeAndCorrectResponseBody() {
        final BankDetailsValidatorV2 bankDetailsValidator = getBankDetailsValidator();
        final BankDetailsValidityList validate = bankDetailsValidator.validate(ACCOUNT_NUMBER, SORT_CODE, ROLL_NUMBER, CONSUMER_ID, CORRELATION_ID);
        assertThat(validate.getResults()).isNotNull();
        assertThat(validate.getResults().length).isEqualTo(1);
        assertThat(validate.getResults()[0]).isEqualTo(VALID);
    }

    private static BankDetailsValidatorV2 getBankDetailsValidator() {
        final DefaultApi api = new DefaultApi();
        api.getApiClient().setBasePath(System.getenv().getOrDefault("PIP_BANK_VALIDATION_BASE_URL", "http://localhost:8950/api"));
        final BankDetailsValidatorV2 bankDetailsValidator = new BankDetailsValidatorV2(api);
        return bankDetailsValidator;
    }

}
