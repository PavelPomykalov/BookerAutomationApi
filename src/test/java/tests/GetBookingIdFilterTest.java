package tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.APIClient;
import core.models.BookingDates;
import core.models.BookingInfo;
import core.models.CreateBooking;
import io.qameta.allure.Epic;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

@Epic("Задача № 332")
@Severity(SeverityLevel.CRITICAL)
public class GetBookingIdFilterTest {
    private APIClient apiClient;
    private ObjectMapper objectMapper;
    private List<Integer> createdBooking;

    // Инициализация API клиента и создания booking перед каждым тестом
    @BeforeEach
    public void setup() {
        apiClient = new APIClient();
        objectMapper = new ObjectMapper();
    }

    BookingInfo[] bookings = new BookingInfo[]{
            new BookingInfo("TEST", "ANDROID", 101, true, new BookingDates("2025-01-01", "2025-01-02"), "Breakfast"),
            new BookingInfo("TEST", "TESTOV", 102, false, new BookingDates("2025-02-01", "2025-02-02"), "Breakfast"),
            new BookingInfo("AUTOTEST", "PETROV", 103, true, new BookingDates("2025-03-01", "2025-03-02"), "Breakfast")
    };

    @Test
    public void getBookingFilterAndDelete() throws Exception {
        createBooking();
        getBookingWithFilters();
    }

    public void createBooking() throws JsonProcessingException {
        step("Создаем массив где будем хранить созданные бронирования");
        createdBooking = new ArrayList<>();

        for (BookingInfo booking : bookings) {
            String requestBody = objectMapper.writeValueAsString(booking);
            Response response = apiClient.createBooking(requestBody);

            step("Проверка статус кода 200");
            assertThat(response.statusCode()).isEqualTo(200);

            CreateBooking createBooking = objectMapper.readValue(response.asString(), CreateBooking.class);

            step("добавление ID в массив");
            createdBooking.add(createBooking.getBookingid());

        }
        step("Выводим список всех созданных ID");
        System.out.println("созданные ID: " + createdBooking);
    }

    public void getBookingWithFilters() {
        step("Запрос с фильтрацией по firstname");
        Map<String, String> filtersFirstName = new HashMap<>();
        filtersFirstName.put("firstname", "TEST");

        Response responseFiltersFirstName = apiClient.getBookingWithFilters(filtersFirstName);
        assertThat(responseFiltersFirstName.statusCode()).isEqualTo(200);
        responseFiltersFirstName.prettyPrint();

        List<Integer> bookingIdWithFirstName = responseFiltersFirstName.jsonPath().getList("bookingid");

        for (int id : bookingIdWithFirstName) {
            Response bookingResponse = apiClient.getBookingId(id);
            assertThat(bookingResponse.statusCode()).isEqualTo(200);
            String firstname = bookingResponse.jsonPath().getString("firstname");
            assertThat(firstname).isEqualTo("TEST");
        }

        System.out.println("Второй тест");
        step("Запрос с фильтрацией по lastname");
        Map<String,String> filtersLastName = new HashMap<>();
        filtersLastName.put("lastname", "TESTOV");

        Response responseFiltersLastName = apiClient.getBookingWithFilters(filtersLastName);
        assertThat(responseFiltersLastName.getStatusCode()).isEqualTo(200);
        responseFiltersLastName.prettyPrint();

        List<Integer> bookingIdWithLastName = responseFiltersLastName.jsonPath().getList("bookingid");

        for (int id : bookingIdWithLastName) {
            Response bookingResponseLastName = apiClient.getBookingId(id);
            assertThat(bookingResponseLastName.statusCode()).isEqualTo(200);
            String bookingLastName = bookingResponseLastName.jsonPath().getString("lastname");
            assertThat(bookingLastName).isEqualTo("TESTOV");
        }
        System.out.println("Второй тест пройден");

//        System.out.println("Третий тест");
//        step("Запрос с фильтрацией по bookingdates");
//        Map<String,String> filtersBookingDates = new HashMap<>();
//        filtersBookingDates.put("checkin", "2025-01-02");
//
//        Response responseFiltersBookingDates = apiClient.getBookingWithFilters(filtersBookingDates);
//        assertThat(responseFiltersBookingDates.getStatusCode()).isEqualTo(200);
//        responseFiltersBookingDates.prettyPrint();
//
//        List<Integer> bookingIdWithBookingDates = responseFiltersBookingDates.jsonPath().getList("bookingid");
//
//        for (int id : bookingIdWithBookingDates) {
//            Response bookingWithBookingDates = apiClient.getBookingId(id);
//
//            assertThat(bookingWithBookingDates.statusCode()).isEqualTo(200);
//            String bookingDates = bookingWithBookingDates.jsonPath().getString("checkin");
//            assertThat(bookingDates).isEqualTo("2025-01-02");
//        }
//        System.out.println("Третий тест пройден");
    }
    @AfterEach
    public void deleteId(){
        step("Удаление ID");
        apiClient.createToken("admin","password123");

        if(createdBooking.isEmpty()){
            System.out.println("Нет ID, который нужно удалить");
        }
        else {
            for (int id : createdBooking) {

                apiClient.deleteBooking(id);
                System.out.println("Удален " + id);

                Response checkResponse = apiClient.getBookingId(id);

                assertThat(checkResponse.statusCode()).isEqualTo(404);
            }
        }
    }
}



