package tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.APIClient;
import core.models.Booking;
import core.models.BookingDates;
import core.models.BookingInfo;
import core.models.CreateBooking;
import io.qameta.allure.Allure;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

public class GetBookingTest {
    private APIClient apiClient;
    private ObjectMapper objectMapper; // Для ответа
    private CreateBooking createBooking; // Храним созданное бронирование
    private BookingInfo newBooking;

    // Инициализация API клиента перед каждым тестом
    @Feature("Получение клиента")// Инициализация API клиента перед каждым тестом
    @BeforeEach
    public void setup() {
        apiClient = new APIClient();
        objectMapper = new ObjectMapper();

        // Создаем объект booking с необходимыми данными
        newBooking = new BookingInfo();
        newBooking.setFirstname("gena");
        newBooking.setLastname("orlov");
        newBooking.setTotalprice(103);
        newBooking.setDepositpaid(true);
        newBooking.setBookingdates(new BookingDates("2025-01-12", "2025-01-93"));
        newBooking.setAdditionalneeds("Breakfast");
    }

    @Test
    public void testGetBooking() throws Exception {
        createBooking();
        getBooking();
    }

    public void createBooking() throws JsonProcessingException {
        objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(newBooking); // создаем переменную requestBody и превращаем в строку объект newBooking
        Response response = apiClient.createBooking(requestBody); // в переменную response записали ответ на запрос

        step("Проверка статус кода");
        assertThat(response.getStatusCode()).isEqualTo(200); // Проверили статус код

        step("Десериализация ответа");
        String responseBody = response.asString();
        createBooking = objectMapper.readValue(responseBody, CreateBooking.class); // Взяли Json ответа и превратили в Java, записали в класс CreateBooking
        System.out.println("Создали бронирование " + createBooking.getBookingid());

    }

    public void getBooking() throws JsonProcessingException{
        step("Выполняем запрос для списка ID");
        Response response = apiClient.getBooking();
        assertThat(response.getStatusCode()).isEqualTo(200);

        step("Десериализация ответа");
        String responseBody = response.getBody().asString();
        List<Booking> bookings = objectMapper.readValue(responseBody, new TypeReference<List<Booking>>() {
        });

        assertThat(bookings).isNotEmpty(); // Проверяем что тело содержит объекты bookingid

        for (Booking booking : bookings) {
            assertThat(booking.getBookingid()).isGreaterThan(0); // bookingId должен быть больше 0
        }
        System.out.println("Цикл отработал");
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