package baseTest.http;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static params.GlobalConstants.BASE_HTTP_URL;

public abstract class BaseHttpTest {

    protected RequestSpecification init() {
        return RestAssured.given().baseUri(BASE_HTTP_URL);
    }

    protected Response sendRequest(Method method, String endpoint, Map<String, String> headers, String requestBody) {
        RequestSpecification request = init();

        if (headers != null) {
            headers.forEach(request::header);
        }

        if (method != Method.GET) {
            request.body(requestBody != null ? requestBody : "{}");
        }

        return request.request(method, endpoint)
                .then()
                .extract()
                .response();
    }

    public abstract Response executeRequest(Method method, String endpoint, String requestBody);
}
