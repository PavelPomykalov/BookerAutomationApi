package core.models;

public class CreateBooking {
    private int bookingid;
    private BookingInfo booking;

    public BookingInfo getBooking() {
        return booking;
    }

    public void setBooking(BookingInfo booking) {
        this.booking = booking;
    }

    public int getBookingid() {
        return bookingid;
    }

    public void setBookingid(int bookingid) {
        this.bookingid = bookingid;
    }
}
