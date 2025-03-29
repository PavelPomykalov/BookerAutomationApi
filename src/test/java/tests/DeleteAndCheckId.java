package tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.APIClient;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DeleteAndCheckId {
    private APIClient apiClient;
    private ObjectMapper objectMapper;
    private int id;

    // Инициализация API клиента перед тестами
    @BeforeEach
    public void setup() {
        apiClient = new APIClient();
        objectMapper = new ObjectMapper();
        apiClient.createToken("admin", "password123");
    }

    @Test
    public void testDeleteId() throws Exception {
        getBookingId();
        deleteBookingId();
        testGetBookingId();
    }

    private void getBookingId() throws Exception {
        Response response = apiClient.getBooking();
        // Проверяем статус код = 200
        assertThat(response.getStatusCode()).isEqualTo(200);
        id = Integer.parseInt(response.jsonPath().getString("bookingid[0]"));
        System.out.println("id записан: " + id);
    }

    private void deleteBookingId() throws Exception {
        // Отправляем запрос на удаление
        Response response = apiClient.deleteBooking(id);

        // Проверяем статус код 201
        assertThat(response.getStatusCode()).isEqualTo(201);
        System.out.println(" id удален: " + id);
    }

    public void testGetBookingId() throws Exception {
        // Выполняем запрос к эндпоинту /booking через APIClient
        Response response = apiClient.getBookingId(id);

        assertThat(response.getStatusCode()).isEqualTo(404); // Проверяем статус код = 404
        System.out.println("id 100 % удален");

   }
}
