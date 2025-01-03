package defaultHttp.tests;

import com.google.gson.reflect.TypeToken;
import dataclass.CreateUserErrorResponse;
import dataclass.CreateUserResponse;
import dataclass.GetUserResponse;
import defaultHttp.DefaultApiTestBase;
import io.restassured.http.Method;
import io.restassured.response.Response;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static defaultHttp.DefaultRequestBuilder.DEFAULT_PASSWORD;
import static defaultHttp.DefaultRequestBuilder.prepareRequestForCreateUser;
import static io.restassured.mapper.ObjectMapperType.GSON;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static params.GlobalConstants.CREATE_USER;
import static params.GlobalConstants.GET_USER;

class DefaultHttpApiTest extends DefaultApiTestBase {

    private static final Pattern BCRYPT_PATTERN = Pattern.compile("^\\$2[aby]\\$\\d{2}\\$[./A-Za-z0-9]{53}$");
    private static final Pattern DATE_PATTERN = Pattern.compile("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");


    // Можно отказаться и использовать CvsSource в самом тесте
    static Stream<Arguments> defaultUserData() {
        long date = System.currentTimeMillis();
        String username = "user" + date;
        String email = "email" + date + "@example.com";

        return Stream.of(
                Arguments.of(username, email)
        );
    }

    @ParameterizedTest(name = "username = {0}, email = {1}")
    @MethodSource("defaultUserData")
    @DisplayName("Успешное создание пользователя:")
    @Tag("High")
    void sendSuccessfulRequestToUserCreateMethod(String username, String email) {
        Response response = executeRequest(Method.POST, CREATE_USER, prepareRequestForCreateUser(username, email));
        CreateUserResponse gsonResponse = response.as(CreateUserResponse.class, GSON);

        assertAll(
                () -> assertEquals(200, response.statusCode(), "Статус-код не соответствует ожидаемому"),
                () -> assertNotNull(response, "response пришел пустым"));
        assertAll(
                () -> assertTrue(gsonResponse.success(), "Поле success должно быть true"),
                () -> assertEquals("User Successully created", gsonResponse.message(), "Сообщение отличается"),
                () -> {
                    CreateUserResponse.Details details = gsonResponse.details();
                    assertNotNull(details, "Детали не должны быть null");
                    assertAll(
                            () -> assertEquals(username, details.username(),
                                    String.format("пришло другое значение username: %s", details.username())),
                            () -> assertEquals(email, details.email(),
                                    String.format("пришло другое значение email: %s", details.email())),
                            () -> assertTrue(
                                    BCRYPT_PATTERN.matcher(details.password()).matches(),
                                    String.format("пришло другое значение password: %s", details.password())),
                            () -> assertInstanceOf(Number.class, details.id(), "поле ID не число"),
                            () -> assertTrue(
                                    DATE_PATTERN.matcher(details.created_at()).matches(),
                                    "Поле created_at не соответствует формату yyyy-MM-dd HH:mm:ss"),
                            () -> assertTrue(
                                    DATE_PATTERN.matcher(details.updated_at()).matches(),
                                    "Поле updated_at не соответствует формату yyyy-MM-dd HH:mm:ss")
                    );
                }
        );
    }

    // Для полноценной проверки не хватает метода BeforeAll например по удалению старых данных в БД - иначе создаем уже созданных юзеров
    // по списку валидации. Или же если запускаем в новом контейнере то все отработает по тесту
    @ParameterizedTest(name = "Создание пользователя с разлиным username = {0}")
    @CsvFileSource(resources = "/createUserWithDifferentNames.csv")
    @Disabled
    void createUserWithDifferentNames(String username) {

        String email = "email_" + System.currentTimeMillis() + "@test.com";

        Response response = executeRequest(Method.POST, CREATE_USER, prepareRequestForCreateUser(username, email));
        CreateUserResponse gsonResponse = response.as(CreateUserResponse.class, GSON);

        assertAll(
                () -> assertEquals(200, response.statusCode(), "Статус-код не соответствует ожидаемому"),
                () -> assertNotNull(response, "response пришел пустым"));
        assertAll(
                () -> assertTrue(gsonResponse.success(), "Поле success должно быть true"),
                () -> assertEquals("User Successully created", gsonResponse.message(), "Сообщение отличается"),
                () -> {
                    CreateUserResponse.Details details = gsonResponse.details();
                    assertNotNull(details, "Детали не должны быть null");
                    assertAll(
                            () -> assertEquals(username, details.username(),
                                    String.format("пришло другое значение username: %s", details.username())),
                            () -> assertEquals(email, details.email(),
                                    String.format("пришло другое значение email: %s", details.email())),
                            () -> assertTrue(
                                    BCRYPT_PATTERN.matcher(details.password()).matches(),
                                    String.format("пришло другое значение password: %s", details.password())),
                            () -> assertInstanceOf(Number.class, details.id(), "поле ID не число"),
                            () -> assertTrue(
                                    DATE_PATTERN.matcher(details.created_at()).matches(),
                                    "Поле created_at не соответствует формату yyyy-MM-dd HH:mm:ss"),
                            () -> assertTrue(
                                    DATE_PATTERN.matcher(details.updated_at()).matches(),
                                    "Поле updated_at не соответствует формату yyyy-MM-dd HH:mm:ss")
                    );
                }
        );
    }

    @ParameterizedTest(name = "Создание пользователя с разлиным password = {0}")
    @CsvFileSource(resources = "/createUserWithDifferentPassword.csv")
    void createUserWithDifferentPassword(String password) {

        String email = "email_" + System.currentTimeMillis() + "@test.com";
        String username = "username_" + System.currentTimeMillis();

        Response response = executeRequest(Method.POST, CREATE_USER, prepareRequestForCreateUser(username, email, password));
        CreateUserResponse gsonResponse = response.as(CreateUserResponse.class, GSON);

        assertAll(
                () -> assertEquals(200, response.statusCode(), "Статус-код не соответствует ожидаемому"),
                () -> assertNotNull(response, "response пришел пустым"));
        assertAll(
                () -> assertTrue(gsonResponse.success(), "Поле success должно быть true"),
                () -> assertEquals("User Successully created", gsonResponse.message(), "Сообщение отличается"),
                () -> {
                    CreateUserResponse.Details details = gsonResponse.details();
                    assertNotNull(details, "Детали не должны быть null");
                    assertAll(
                            () -> assertEquals(username, details.username(),
                                    String.format("пришло другое значение username: %s", details.username())),
                            () -> assertEquals(email, details.email(),
                                    String.format("пришло другое значение email: %s", details.email())),
                            () -> assertTrue(
                                    BCRYPT_PATTERN.matcher(details.password()).matches(),
                                    String.format("пришло другое значение password: %s", details.password())),
                            () -> assertInstanceOf(Number.class, details.id(), "поле ID не число"),
                            () -> assertTrue(
                                    DATE_PATTERN.matcher(details.created_at()).matches(),
                                    "Поле created_at не соответствует формату yyyy-MM-dd HH:mm:ss"),
                            () -> assertTrue(
                                    DATE_PATTERN.matcher(details.updated_at()).matches(),
                                    "Поле updated_at не соответствует формату yyyy-MM-dd HH:mm:ss")
                    );
                }
        );
    }

    // Для полноценной проверки не хватает метода BeforeAll например по удалению старых данных в БД по email- иначе создаем уже созданных
    // юзеров по списку валидации . Или же если запускаем в новом контейнере то все отработает по тесту
    @ParameterizedTest(name = "Создание пользователя с разлиным email = {0}")
    @CsvFileSource(resources = "/createUserWithDifferentEmail.csv")
    @Disabled
    void createUserWithDifferentEmail(String email) {

        String username = "username_" + System.currentTimeMillis();

        Response response = executeRequest(Method.POST, CREATE_USER, prepareRequestForCreateUser(username, email, DEFAULT_PASSWORD));
        CreateUserResponse gsonResponse = response.as(CreateUserResponse.class, GSON);

        assertAll(
                () -> assertEquals(200, response.statusCode(), "Статус-код не соответствует ожидаемому"),
                () -> assertNotNull(response, "response пришел пустым"));
        assertAll(
                () -> assertTrue(gsonResponse.success(), "Поле success должно быть true"),
                () -> assertEquals("User Successully created", gsonResponse.message(), "Сообщение отличается"),
                () -> {
                    CreateUserResponse.Details details = gsonResponse.details();
                    assertNotNull(details, "Детали не должны быть null");
                    assertAll(
                            () -> assertEquals(username, details.username(),
                                    String.format("пришло другое значение username: %s", details.username())),
                            () -> assertEquals(email, details.email(),
                                    String.format("пришло другое значение email: %s", details.email())),
                            () -> assertTrue(
                                    BCRYPT_PATTERN.matcher(details.password()).matches(),
                                    String.format("пришло другое значение password: %s", details.password())),
                            () -> assertInstanceOf(Number.class, details.id(), "поле ID не число"),
                            () -> assertTrue(
                                    DATE_PATTERN.matcher(details.created_at()).matches(),
                                    "Поле created_at не соответствует формату yyyy-MM-dd HH:mm:ss"),
                            () -> assertTrue(
                                    DATE_PATTERN.matcher(details.updated_at()).matches(),
                                    "Поле updated_at не соответствует формату yyyy-MM-dd HH:mm:ss")
                    );
                }
        );
    }

    @Test
    @DisplayName("Получение респонса ошибки и кода 400 при попытке создать текущего юзера по username")
    void receiveErrorWithTryToCreateExistingUsername() {
        String email = "email_" + System.currentTimeMillis() + "@test.com";
        String username = "username_" + System.currentTimeMillis();

        Response response = executeRequest(Method.POST, CREATE_USER, prepareRequestForCreateUser(username, email, DEFAULT_PASSWORD));

        assertAll(
                () -> assertEquals(200, response.statusCode(), "Статус-код не соответствует ожидаемому"),
                () -> assertNotNull(response, "response пришел пустым"));

        String newEmail = "NewEmail_" + System.currentTimeMillis() + "@test.com";
        Response responseError = executeRequest(Method.POST, CREATE_USER, prepareRequestForCreateUser(username, newEmail,
                DEFAULT_PASSWORD));
        CreateUserErrorResponse gsonResponseError = responseError.as(CreateUserErrorResponse.class, GSON);

        assertAll(
                () -> assertEquals(400, responseError.statusCode(), "Статус-код не соответствует ожидаемому"),
                () -> assertNotNull(responseError, "response пришел пустым"));
        assertAll(
                () -> assertFalse(gsonResponseError.success(), "Поле success должно быть false"),
                () -> assertTrue(gsonResponseError.message().getFirst().contains("This username is taken. Try another"),
                        String.format("Сообщение об ошибке неверное : %s", gsonResponseError.message()))
        );
    }

    @Test
    @DisplayName("Получение респонса ошибки и кода 400 при попытке создать текущего юзера по email")
    void receiveErrorWithTryToCreateExistingUserWithEmail() {
        String email = "email_" + System.currentTimeMillis() + "@test.com";
        String username = "username_" + System.currentTimeMillis();

        Response response = executeRequest(Method.POST, CREATE_USER, prepareRequestForCreateUser(username, email, DEFAULT_PASSWORD));

        assertAll(
                () -> assertEquals(200, response.statusCode(), "Статус-код не соответствует ожидаемому"),
                () -> assertNotNull(response, "response пришел пустым"));

        String newUsername = "newUsername_" + System.currentTimeMillis();
        Response responseError = executeRequest(Method.POST, CREATE_USER, prepareRequestForCreateUser(newUsername, email,
                DEFAULT_PASSWORD));
        CreateUserErrorResponse gsonResponseError = responseError.as(CreateUserErrorResponse.class, GSON);

        assertAll(
                () -> assertEquals(400, responseError.statusCode(), "Статус-код не соответствует ожидаемому"),
                () -> assertNotNull(responseError, "response пришел пустым"));
        assertAll(
                () -> assertFalse(gsonResponseError.success(), "Поле success должно быть false"),
                () -> assertTrue(gsonResponseError.message().getFirst().contains("Email already exists"),
                        String.format("Сообщение об ошибке неверное : %s", gsonResponseError.message()))
        );
    }

    @Test
    @DisplayName("Получение новосозданного пользователя в списках всех юзеров")
    @Tag("Integration")
    void createAndCheckUserByGetUserRequest() {
        String email = "email_" + System.currentTimeMillis() + "@test.com";
        String username = "username_" + System.currentTimeMillis();

        // Создаем юзера
        Response postResponse = executeRequest(Method.POST, CREATE_USER, prepareRequestForCreateUser(username, email, DEFAULT_PASSWORD));
        assertAll(
                () -> assertEquals(200, postResponse.statusCode(), "Статус-код не соответствует ожидаемому"),
                () -> assertNotNull(postResponse, "response пришел пустым"));

        Response getResponse = executeRequest(Method.GET, GET_USER, null);
        assertAll(
                () -> assertEquals(200, getResponse.statusCode(), "Статус-код не соответствует ожидаемому"),
                () -> assertNotNull(getResponse, "response пришел пустым"));

        // Находим нашего юзера из полученного списка GET запроса
        List<GetUserResponse.UserDetails> users = getResponse.as(new TypeToken<List<GetUserResponse.UserDetails>>() {}.getType(), GSON);
        GetUserResponse.UserDetails createdUser = users.stream()
                .filter(user -> user.username().equals(username) && user.email().equals(email))
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        String.format("юзер: %s с email: %s не найден" , username, email)
                ));

        assertAll(
                () -> assertNotNull(createdUser.id(), "Поле id не должно быть null"),
                () -> assertInstanceOf(Integer.class, createdUser.id(), "Поле id должно быть числом"),
                () -> assertEquals(username, createdUser.username(), "username не совпадает"),
                () -> assertEquals(email, createdUser.email(), "email не совпадает"),
                () -> assertTrue(
                        BCRYPT_PATTERN.matcher(createdUser.password()).matches(),
                        String.format("Поле password не соответствует Bcrypt шаблону: %s", createdUser.password())),
                () -> assertTrue(DATE_PATTERN.matcher(createdUser.created_at()).matches(),
                        "Поле created_at не соответствует формату yyyy-MM-dd HH:mm:ss"),
                () -> assertTrue(DATE_PATTERN.matcher(createdUser.updated_at()).matches(),
                        "Поле updated_at не соответствует формату yyyy-MM-dd HH:mm:ss")
        );
    }
}
