package tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.APIClient;
import core.models.BookingInfo;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetBookingById {
    private APIClient apiClient;
    private ObjectMapper objectMapper;

    // Инициализация API клиента перед каждым тестом
    @BeforeEach
    public void setup() {
        apiClient = new APIClient();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testGetBookingId() throws Exception {
        // Выполняем запрос к эндпоинту /booking через APIClient
        Response response = apiClient.getBookingId(1);

        // Проверяем статус код = 200
        assertThat(response.getStatusCode()).isEqualTo(200);

        // Десериализуем тело ответа
        String responseBody = response.getBody().asString();  //  Правильный способ получения JSON
        BookingInfo bookingInfo = objectMapper.readValue(responseBody, BookingInfo.class);


        assertThat(bookingInfo).isNotNull();
        assertThat(bookingInfo.getFirstname())
                .isInstanceOf(String.class)// проверка что FirstName строка
                .isNotEmpty(); //проверка что строка не пустая

        assertThat(bookingInfo.getBookingdates())
                .isInstanceOf(Object.class);

        assertThat(bookingInfo.getTotalprice())
                .isInstanceOf(Number.class);

    }
}
