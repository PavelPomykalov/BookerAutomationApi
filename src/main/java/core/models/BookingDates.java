package core.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BookingDates {
    private String checkin;
    private String checkout;

    public BookingDates() {}

    // Конструктор
    @JsonCreator

    public BookingDates (
            @JsonProperty("checkout") String checkout,
            @JsonProperty ("checkin") String checkin) {
        this.checkout = checkout;
        this.checkin = checkin;
    }

    // геттеры и сеттеры

    public String getCheckin() {
        return checkin;
    }

    public void setCheckin(String checkin) {
        this.checkin = checkin;
    }

    public String getCheckout() {
        return checkout;
    }

    public void setCheckout(String checkout) {
        this.checkout = checkout;
    }
}
