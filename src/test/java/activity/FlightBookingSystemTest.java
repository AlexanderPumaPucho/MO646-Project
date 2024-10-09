package activity;

import activity.FlightBookingSystem.BookingResult;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


public class FlightBookingSystemTest {

    @Before
    public void setup() {
        FlightBookingSystem flightBookingSystem = new FlightBookingSystem();
        LocalDateTime bookingTime = LocalDateTime.of(2024, 10, 1, 12, 0);
        LocalDateTime departureTime = LocalDateTime.of(2024, 10, 2, 12, 0);
    }

    @Test
    public void testInsufficientSeats() {
        FlightBookingSystem bookingSystem = new FlightBookingSystem();
        BookingResult result = bookingSystem.bookFlight(5, LocalDateTime.now(), 3,
                500.00, 50, false, LocalDateTime.now().plusDays(2), 0);
        assertFalse(result.confirmation);
        assertEquals(0, result.totalPrice, 0.01);
    }

}


