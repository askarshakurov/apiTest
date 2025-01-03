package defaultHttp;

import com.google.gson.Gson;
import dataclass.CreateUserRequest;

public class DefaultRequestBuilder {

    public static final String DEFAULT_PASSWORD = "simplePassword123";

    public static String prepareRequestForCreateUser(String username, String email) {
        return prepareRequestForCreateUser(username, email, DEFAULT_PASSWORD);
    }

    public static String prepareRequestForCreateUser(String username, String email, String password) {
        CreateUserRequest request = new CreateUserRequest(
                username,
                email,
                password
        );

        Gson gson = new Gson();
        return gson.toJson(request);
    }
}
