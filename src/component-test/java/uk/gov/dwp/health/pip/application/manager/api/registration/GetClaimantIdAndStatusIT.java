package uk.gov.dwp.health.pip.application.manager.api.registration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.health.pip.application.manager.api.ApiTest;
import uk.gov.dwp.health.pip.application.manager.openapi.registration.v1.dto.ClaimantIdAndApplicationStatus;
import uk.gov.dwp.health.pip.application.manager.openapi.v1.dto.ApplicationDto;
import uk.gov.dwp.health.pip.application.manager.requestmodels.registration.Registration;
import uk.gov.dwp.health.pip.application.manager.utils.RandomStringUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.dwp.health.pip.application.manager.utils.UrlBuilderUtil.buildGetClaimantIdAndStatusByIdUrl;
import static uk.gov.dwp.health.pip.application.manager.utils.UrlBuilderUtil.buildPostApplicationUrl;


public class GetClaimantIdAndStatusIT extends ApiTest {

    private String url;

    @BeforeEach
    public void createApplicationData() {
        Registration registration = Registration.builder().claimantId(RandomStringUtil.generate(24)).build();
        var response = postRequest(buildPostApplicationUrl(), registration).getBody().as(ApplicationDto.class);
        url = buildGetClaimantIdAndStatusByIdUrl(response.getApplicationId());
    }

    @Test
    public void shouldReturn200StatusCodeForValidApplicationID() {
        int actualStatusCode = getRequest(url).statusCode();
        ClaimantIdAndApplicationStatus claimantIdAndStatusDto =
                extractGetRequest(url, ClaimantIdAndApplicationStatus.class);

        assertThat(actualStatusCode).isEqualTo(200);
        assertThat(claimantIdAndStatusDto.getClaimantId()).matches("^[a-zA-Z0-9]{24}$");
        assertThat(claimantIdAndStatusDto.getApplicationStatus())
                .isEqualTo(ClaimantIdAndApplicationStatus.ApplicationStatusEnum.REGISTRATION);
    }

    @Test
    public void shouldReturn404StatusCodeWhenTheApplicationIdIsNotFound() {
        int actualResponseCode = getRequest(buildGetClaimantIdAndStatusByIdUrl(RandomStringUtil.generate(24))).statusCode();

        assertThat(actualResponseCode).isEqualTo(404);
    }

    @Test
    public void shouldReturn400StatusCodeWhenTheApplicationIdInvalidFormat() {
        int actualResponseCode = getRequest(buildGetClaimantIdAndStatusByIdUrl("invalid-format")).statusCode();

        assertThat(actualResponseCode).isEqualTo(400);
    }

}
