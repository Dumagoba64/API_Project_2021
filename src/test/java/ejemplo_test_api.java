import io.restassured.RestAssured;
import org.junit.Test;
import io.restassured.response.Response;
import static org.junit.Assert.*;
import static io.restassured.RestAssured.*;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Allure;
import io.qameta.allure.*;
import io.qameta.allure.model.Attachment;
import io.restassured.filter.Filter;
import io.qameta.allure.restassured.AllureRestAssured;

public class ejemplo_test_api{


    @Test
    @DisplayName("test covid apI")
    @Description("This test check the body response")
    public void api_Covid_test(){
        RestAssured.baseURI = String.format("https://api.quarantine.country/api/v1/summary/latest");

        Response response = given()
                .log().all()
                .filter(new AllureRestAssured())
                .headers("Accept", "application/json")
                .get();

        String body_response = response.getBody().asString();
        String  status = String.format(String.valueOf(response.getStatusCode()));

        System.out.println("Body response: " + body_response);
        System.out.println("Status code: " + response.getStatusCode());
        assertEquals(200,response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("total_cases"));

        Allure.addAttachment("Status code", status);
    }
}