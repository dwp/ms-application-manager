package uk.gov.dwp.health.pip.application.manager.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import uk.gov.dwp.health.pip.application.manager.config.properties.BankDetailsValidationProperties;
import uk.gov.dwp.health.pip.application.manager.entity.BankDetailsValidityList;
import uk.gov.dwp.health.pip.application.manager.entity.enums.BankDetailsValidity;
import uk.gov.dwp.health.pip.application.manager.external.bankdetails.ApiException;
import uk.gov.dwp.health.pip.application.manager.external.bankdetails.v3.DefaultApi;
import uk.gov.dwp.health.pip.application.manager.external.bankdetails.v3.dto.AccountDetails;
import uk.gov.dwp.health.pip.application.manager.external.bankdetails.v3.dto.AdditionalInformationDto;
import uk.gov.dwp.health.pip.application.manager.external.bankdetails.v3.dto.ValidationResultDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class BankDetailsValidatorTest {

  public static final String ACCOUNT_NUMBER = "1";
  public static final String SORT_CODE = "2";
  public static final String ROLL_NUMBER = "3";
  public static final String CORRELATION_ID = "4";
  public static final String CONSUMER_ID = "5";

  @InjectMocks
  private BankDetailsValidator bankDetailsValidator;
  @Mock
  private BankDetailsValidationProperties properties;
  @Mock
  private DefaultApi defaultApi;

  @AfterEach
  public void teardown() {
    MDC.remove("correlationId");
  }

  @Test
  public void validate() throws ApiException {
    MDC.put("correlationId", CORRELATION_ID);
    when(properties.getConsumerId()).thenReturn(CONSUMER_ID);
    bankDetailsValidator.validate(ACCOUNT_NUMBER, SORT_CODE, ROLL_NUMBER);
    final ArgumentCaptor<AccountDetails> accountDetails = ArgumentCaptor.forClass(AccountDetails.class);
    final ArgumentCaptor<String> correlationIdCaptor = ArgumentCaptor.forClass(String.class);
    final ArgumentCaptor<String> consumerIdCaptor = ArgumentCaptor.forClass(String.class);
    verify(defaultApi, times(1)).validate1(correlationIdCaptor.capture(), consumerIdCaptor.capture(), accountDetails.capture());
    assertEquals(ACCOUNT_NUMBER, accountDetails.getValue().getAccountNumber());
    assertEquals(SORT_CODE, accountDetails.getValue().getSortCode());
    assertEquals(ROLL_NUMBER, accountDetails.getValue().getRollNumber());
    assertEquals(CORRELATION_ID, correlationIdCaptor.getValue());
    assertEquals(CONSUMER_ID, consumerIdCaptor.getValue());
  }

  @Test
  public void invalidBankDetails() throws ApiException {
    final ValidationResultDto result = new ValidationResultDto();
    result.setValidDetails(false);
    result.addAdditionalInformationItem(new AdditionalInformationDto().code("6").severity(AdditionalInformationDto.SeverityEnum.ERROR));
    result.addAdditionalInformationItem(new AdditionalInformationDto().code("65").severity(AdditionalInformationDto.SeverityEnum.WARNING));
    when(defaultApi.validate1(any(), any(), any())).thenReturn(result);
    when(properties.getConsumerId()).thenReturn(CONSUMER_ID);
    final BankDetailsValidityList actualResult = bankDetailsValidator.validate(ACCOUNT_NUMBER, SORT_CODE, ROLL_NUMBER);
    assertNotNull(actualResult);
    assertNotNull(actualResult.getResults());
    assertEquals(2, actualResult.getResults().length);
    assertEquals(BankDetailsValidity.INVALID_ACCOUNT_COMBINATION, actualResult.getResults()[0]);
    assertEquals(BankDetailsValidity.ROLL_NUMBER_REQUIRED, actualResult.getResults()[1]);
  }

  @Test
  public void modulusCheckUnavailableWarningIgnored() throws ApiException {
    final ValidationResultDto result = new ValidationResultDto();
    result.setValidDetails(false);
    result.addAdditionalInformationItem(new AdditionalInformationDto().code("002").severity(AdditionalInformationDto.SeverityEnum.WARNING));
    when(defaultApi.validate1(any(), any(), any())).thenReturn(result);
    when(properties.getConsumerId()).thenReturn(CONSUMER_ID);
    final BankDetailsValidityList actualResult = bankDetailsValidator.validate(ACCOUNT_NUMBER, SORT_CODE, ROLL_NUMBER);
    assertNotNull(actualResult);
    assertNotNull(actualResult.getResults());
    assertEquals(1, actualResult.getResults().length);
    assertEquals(BankDetailsValidity.VALID, actualResult.getResults()[0]);
  }

  @Test
  public void unexpectedCodesIgnoredWithValidBankDetails() throws ApiException {
    final ValidationResultDto result = new ValidationResultDto();
    result.setValidDetails(false);
    // first item here populates response with VALID
    result.addAdditionalInformationItem(new AdditionalInformationDto().code("103").severity(AdditionalInformationDto.SeverityEnum.WARNING));
    result.addAdditionalInformationItem(new AdditionalInformationDto().code("70").severity(AdditionalInformationDto.SeverityEnum.WARNING));
    result.addAdditionalInformationItem(new AdditionalInformationDto().code("20").severity(AdditionalInformationDto.SeverityEnum.WARNING));
    result.addAdditionalInformationItem(new AdditionalInformationDto().code("47").severity(AdditionalInformationDto.SeverityEnum.WARNING));
    when(defaultApi.validate1(any(), any(), any())).thenReturn(result);
    when(properties.getConsumerId()).thenReturn(CONSUMER_ID);
    final BankDetailsValidityList actualResult = bankDetailsValidator.validate(ACCOUNT_NUMBER, SORT_CODE, ROLL_NUMBER);
    assertNotNull(actualResult);
    assertNotNull(actualResult.getResults());
    assertEquals(1, actualResult.getResults().length);
    assertEquals(BankDetailsValidity.VALID, actualResult.getResults()[0]);
  }

  @Test
  public void allUnrecognisedCodesEquatesToValidBankDetails() throws ApiException {
    final ValidationResultDto result = new ValidationResultDto();
    result.setValidDetails(false);
    // all these items are unmapped so we hit the 'catch all' at the end adding VALID
    result.addAdditionalInformationItem(new AdditionalInformationDto().code("70").severity(AdditionalInformationDto.SeverityEnum.WARNING));
    result.addAdditionalInformationItem(new AdditionalInformationDto().code("20").severity(AdditionalInformationDto.SeverityEnum.WARNING));
    result.addAdditionalInformationItem(new AdditionalInformationDto().code("47").severity(AdditionalInformationDto.SeverityEnum.WARNING));
    when(defaultApi.validate1(any(), any(), any())).thenReturn(result);
    when(properties.getConsumerId()).thenReturn(CONSUMER_ID);
    final BankDetailsValidityList actualResult = bankDetailsValidator.validate(ACCOUNT_NUMBER, SORT_CODE, ROLL_NUMBER);
    assertNotNull(actualResult);
    assertNotNull(actualResult.getResults());
    assertEquals(1, actualResult.getResults().length);
    assertEquals(BankDetailsValidity.VALID, actualResult.getResults()[0]);
  }

  @Test
  public void sortCodeAccountNumberNotFound() throws ApiException {
    serviceThrowsError(BankDetailsValidity.INVALID_SORT_CODE_ACCOUNT_NUMBER_DATA_FORMAT, 400);
  }

  @Test
  public void bankWizardUnavailable() throws ApiException {
    serviceThrowsError(BankDetailsValidity.SERVICE_DOWN, 500);
  }

  private void serviceThrowsError(final BankDetailsValidity expected, final int responseErrorCode) throws ApiException {
    final ValidationResultDto result = new ValidationResultDto();
    result.setValidDetails(false);
    result.addAdditionalInformationItem(new AdditionalInformationDto().code("6").severity(AdditionalInformationDto.SeverityEnum.ERROR));
    result.addAdditionalInformationItem(new AdditionalInformationDto().code("65").severity(AdditionalInformationDto.SeverityEnum.WARNING));
    when(defaultApi.validate1(any(), any(), any())).thenThrow(new ApiException(responseErrorCode, ""));
    when(properties.getConsumerId()).thenReturn(CONSUMER_ID);
    final BankDetailsValidityList actualResult = bankDetailsValidator.validate(ACCOUNT_NUMBER, SORT_CODE, ROLL_NUMBER);
    assertNotNull(actualResult);
    assertNotNull(actualResult.getResults());
    assertEquals(1, actualResult.getResults().length);
    assertEquals(expected, actualResult.getResults()[0]);
  }

}
