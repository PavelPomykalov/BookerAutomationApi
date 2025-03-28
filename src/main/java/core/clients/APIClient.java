package core.clients;

import core.settings.ApiEndpoints;
import io.restassured.RestAssured;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import io.restassured.specification.RequestSpecification;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class APIClient {
    private final String baseUrl;
    private String token;

    public APIClient() {
        this.baseUrl = determineBaseUrl();
    }

    private String determineBaseUrl() {
        String environment = System.getProperty("env", "test");
        String configFileName = "application-" + environment + ".properties";

        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFileName)) {
            if (input == null) {
                throw new IllegalStateException("Configuration file not found: " + configFileName);
            }
            properties.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load configuration file: " + configFileName, e);
        }
        return properties.getProperty("baseUrl");
    }

    // Настройка базовых параметров HTTP-запросов
    private RequestSpecification getRequestSpec() {
        return RestAssured.given()
                .baseUri(baseUrl)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .filter(addAuthTokenFilter());
    }

    //Метод для получения токена
    public void createToken(String username, String password){
        // Тело запрос для получения токена
        String requestBody = String.format("{\"username\": \"%s\", \"password\": \"%s\" }", username, password);

        Response response =  getRequestSpec()
                .body(requestBody)
                .when()
                .post(ApiEndpoints.AUTH.getPath()) //Используем ENUM для эндпоинта /auth
                .then()
                .statusCode(200)
                .extract()
                .response();

        // Извлекаем токен из ответа
        token = response.jsonPath().getString("token");
    }

    // Фильтр для добавления токена в заголовок Authorization
    private Filter addAuthTokenFilter(){
        return (FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx)-> {
            if (token != null) {
                requestSpec.header("Cookie", "token = " + token);
            }
            return ctx.next(requestSpec, responseSpec); // Продолжает выполнение запроса
        };
    }

    // Get запрос на эндпоинт /ping
    public Response ping() {
        return getRequestSpec() // это то что мы поместили в getRequestSpec
                .when() // команда объявляет то что мы будем делать
                .get(ApiEndpoints.PING.getPath()) //отправляем get запрос //  Используем Enum для эндроинта /ping
                .then() //Затем
                .statusCode(201) // Ожидаемый статус код
                .extract() // Распаковка того, что пришло в response
                .response();
    }

    // Get запрос на эндпоинт /booking
    public Response getBooking() {
        return getRequestSpec() // это то что мы поместили в getRequestSpec
                .when() // команда объявляет то что мы будем делать
                .get(ApiEndpoints.BOOKING.getPath()) //отправляем get запрос //  Используем Enum для эндроинта /ping
                .then() //Затем
                .statusCode(200) // Ожидаемый статус код
                .extract() // Распаковка того, что пришло в response
                .response();
    }

    // Get запрос на эндпоинт /booking /{id}
    public Response getBookingId(int bookingid) {
        return getRequestSpec()// это то что мы поместили в getRequestSpec
                .when() // команда объявляет то что мы будем делать
                .get(ApiEndpoints.BOOKING.getPath() + "/" + bookingid) //отправляем get запрос //  Используем Enum для эндроинта /booking
                .then() //Затем
                .extract() // Распаковка того, что пришло в response
                .response();
    }

    // DELETE запрос на эндпоинт /booking
    public Response deleteBooking (int bookingid) {
        return getRequestSpec()// это то что мы поместили в getRequestSpec
                .when() // команда объявляет то что мы будем делать
                .delete(ApiEndpoints.BOOKING.getPath() + "/" + bookingid)  //  Используем Enum для эндроинта /booking
                .then() //Затем
                .log().all()
                .statusCode(201)// Ожидаемый статус код
                .extract() // Распаковка того, что пришло в response
                .response();
    }



}


