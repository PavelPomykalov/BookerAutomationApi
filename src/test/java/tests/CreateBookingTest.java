package tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.APIClient;
import core.models.BookingDates;
import core.models.BookingInfo;
import core.models.CreateBooking;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

   public class CreateBookingTest {
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
        newBooking.setFirstname("Ivan");
        newBooking.setLastname("Petrov");
        newBooking.setTotalprice(100);
        newBooking.setDepositpaid(true);
        newBooking.setBookingdates(new BookingDates("2025-01-09","2025-01-10"));
        newBooking.setAdditionalneeds("Breakfast");
    }

    @Test
    public void createBooking() throws JsonProcessingException {
        objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(newBooking);
        Response response = apiClient.createBooking(requestBody);

        step("Проверка статус кода");
        assertThat(response.getStatusCode()).isEqualTo(200);

        step("Десереализация ответа");
        String responseBody = response.asString();
        createBooking = objectMapper.readValue(responseBody, CreateBooking.class);
        System.out.println("Создали " + createBooking.getBookingid());


        step("Проверка тела ответа бронирования, который создали");
        assertThat(createBooking).isNotNull();
        assertEquals(createBooking.getBooking().getFirstname(), newBooking.getFirstname());
        assertEquals(createBooking.getBooking().getLastname(), newBooking.getLastname());
        assertEquals(createBooking.getBooking().getTotalprice(), newBooking.getTotalprice());
        assertEquals(createBooking.getBooking().isDepositpaid(), newBooking.isDepositpaid());
        assertEquals(createBooking.getBooking().getBookingdates().getCheckin(), newBooking.getBookingdates().getCheckin());
        assertEquals(createBooking.getBooking().getBookingdates().getCheckout(), newBooking.getBookingdates().getCheckout());
        assertEquals(createBooking.getBooking().getAdditionalneeds(), newBooking.getAdditionalneeds());
    }

       @AfterEach
       public void deleteId () {
            apiClient.createToken("admin", "password123");
            step("Удаление ID");
            System.out.println("Удаляем " + createBooking.getBookingid());
            apiClient.deleteBooking(createBooking.getBookingid());

            assertThat(apiClient.getBookingId(createBooking.getBookingid()).getStatusCode()).isEqualTo(404);

        }
    }

