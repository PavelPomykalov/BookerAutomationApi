package tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.APIClient;
import core.models.BookingDates;
import core.models.BookingInfo;
import core.models.CreateBooking;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Severity(SeverityLevel.CRITICAL)
public class UpdateBookingIdTestPatch {
    private APIClient apiClient;
    private ObjectMapper objectMapper; // Для ответа
    private CreateBooking createBooking; // Храним созданное бронирование
    private BookingInfo newBooking; // Новый объект для создания бронирования (запрос)

    // Инициализация API клиента и создания booking перед каждым тестом
    @BeforeEach
    public void setup(){
        apiClient = new APIClient();
        objectMapper = new ObjectMapper();

        // Создаем объект booking с необходимыми данными
        newBooking = new BookingInfo();
        newBooking.setFirstname("TEST");
        newBooking.setLastname("Petrov");
        newBooking.setTotalprice(105);
        newBooking.setDepositpaid(true);
        newBooking.setBookingdates(new BookingDates("2025-01-09","2025-01-10"));
        newBooking.setAdditionalneeds("Breakfast");
    }

    @Test
    public void updateAndCheckBooking() throws Exception {
        createBooking();
        patchUpdateBooking();
    }

    public void createBooking() throws JsonProcessingException {
        String requestBody = objectMapper.writeValueAsString(newBooking);
        Response response = apiClient.createBooking(requestBody);

        step("Проверка статус кода");
        assertThat(response.getStatusCode()).isEqualTo(200);

        step("Десереализация ответа");
        String responseBody = response.asString();
        createBooking = objectMapper.readValue(responseBody, CreateBooking.class);
        System.out.println("Создали " + createBooking.getBookingid());

    }

    public void patchUpdateBooking() throws JsonProcessingException {

        step("Обновление");
        apiClient.createToken("admin", "password123");
        newBooking.setLastname("Testov");
        newBooking.setTotalprice(110);

        String requestBody = objectMapper.writeValueAsString(newBooking);

        Response response = apiClient.updateBooking(createBooking.getBookingid(), requestBody);

        step("Проверка статус кода на обновление");
        assertThat(response.getStatusCode()).isEqualTo(200);
        System.out.println("Обновили данные "+ createBooking.getBookingid());

        step("Проверка, что данные обновлены");
        assertThat(createBooking).isNotNull();
        assertThat(response.jsonPath().getString("lastname")).isEqualTo(newBooking.getLastname());
        assertThat(response.jsonPath().getInt("totalprice")).isEqualTo(newBooking.getTotalprice());
        System.out.println("Response Body: " + response.getBody().asString());
    }

    @AfterEach
    public void deleteId () {
        step("Удаление ID");
        apiClient.createToken("admin", "password123");
        System.out.println("Удаляем " + createBooking.getBookingid());
        apiClient.deleteBooking(createBooking.getBookingid());

        assertThat(apiClient.getBookingId(createBooking.getBookingid()).getStatusCode()).isEqualTo(404);

    }

}
