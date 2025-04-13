package tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.APIClient;
import core.models.BookingDates;
import core.models.BookingInfo;
import core.models.CreateBooking;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetBookingByIdTest {
    private APIClient apiClient;
    private ObjectMapper objectMapper; // Для ответа
    private CreateBooking createBooking; // Храним созданное бронирование
    private BookingInfo newBooking;

    @Severity(SeverityLevel.BLOCKER)
    @Feature("Получение клиента")// Инициализация API клиента перед каждым тестом
    @BeforeEach
    public void setup() {
        apiClient = new APIClient();
        objectMapper = new ObjectMapper();

        // Создаем объект booking с необходимыми данными
        newBooking = new BookingInfo();
        newBooking.setFirstname("pavel");
        newBooking.setLastname("testov");
        newBooking.setTotalprice(101);
        newBooking.setDepositpaid(true);
        newBooking.setBookingdates(new BookingDates("2025-01-09", "2025-01-10"));
        newBooking.setAdditionalneeds("Breakfast");
    }

    @Test
    public void getBookingAfterCreate() throws Exception {
        createBooking();
        getBookingId();
    }

        public void createBooking () throws JsonProcessingException {
            objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writeValueAsString(newBooking);
            Response response = apiClient.createBooking(requestBody);

            step("Проверка статус кода");
            assertThat(response.getStatusCode()).isEqualTo(200);

            step("Десереализация ответа");
            String responseBody = response.asString();
            createBooking = objectMapper.readValue(responseBody, CreateBooking.class);
            System.out.println("Создали " + createBooking.getBookingid());

        }

        public void getBookingId () {
            step("Выполняем запрос на получение ID");
            Response response = apiClient.getBookingId(createBooking.getBookingid());
            assertThat(response.getStatusCode()).isEqualTo(200);

            step("Проверка наличия полей в ответе");
            assertThat(response).isNotNull();
            assertThat(response.jsonPath().getString("firstname")).isEqualTo(newBooking.getFirstname());
            assertThat(response.jsonPath().getString("lastname")).isEqualTo(newBooking.getLastname());
            assertThat(response.jsonPath().getInt("totalprice")).isEqualTo(newBooking.getTotalprice());
            assertThat(response.jsonPath().getBoolean("depositpaid")).isEqualTo(newBooking.isDepositpaid());
            assertThat(response.jsonPath().getString("bookingdates.checkin")).isEqualTo(newBooking.getBookingdates().getCheckin());
            assertThat(response.jsonPath().getString("bookingdates.checkout")).isEqualTo(newBooking.getBookingdates().getCheckout());
            assertThat(response.jsonPath().getString("additionalneeds")).isEqualTo(newBooking.getAdditionalneeds());
            System.out.println("Проверки прошли");

        }
            @AfterEach
                 public void deleteId () {
                 step("Удаление ID");
                 apiClient.createToken("admin", "password123");
                 System.out.println("Удаляем " + createBooking.getBookingid());
                 apiClient.deleteBooking(createBooking.getBookingid()); // запрос на удаление ID

             assertThat(apiClient.getBookingId(createBooking.getBookingid()).getStatusCode()).isEqualTo(404);

    }
    }



