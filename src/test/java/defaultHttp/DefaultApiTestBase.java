package defaultHttp;

import baseTest.http.BaseHttpTest;
import io.restassured.http.Method;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;

import java.util.HashMap;
import java.util.Map;

import static params.GlobalConstants.APPLICATION_JSON;
import static params.GlobalConstants.CONTENT_TYPE;

public class DefaultApiTestBase extends BaseHttpTest {

    private static Map<String, String> headers;

    @BeforeAll
    public static void setUpHeaders() {
        headers = new HashMap<>();
        headers.put(CONTENT_TYPE, APPLICATION_JSON);
    }

    @Override
    public Response executeRequest(Method method, String endpoint, String requestBody) {
        return sendRequest(method, endpoint, headers, requestBody);
    }
}
