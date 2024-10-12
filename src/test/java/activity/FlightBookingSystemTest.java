package activity;

import org.junit.Before;
import org.junit.Test;

import activity.FlightBookingSystem.BookingResult;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class FlightBookingSystemTest {
    private FlightBookingSystem bookingSystem;

    @Before
    public void setUp(){
        bookingSystem = new FlightBookingSystem();
    }

    @Test
    public void shouldBeAbleToBookFlightIfThereAreAvailableSeats(){
        BookingResult result = bookingSystem.bookFlight(
            1,
            LocalDateTime.now(),
            1,
            500,
            50,
            false,
            LocalDateTime.now().plusDays(5),
            0
        );

        assertTrue(result.totalPrice == 200);
        assertTrue(result.refundAmount == 0);
        assertEquals(result.confirmation, true);
        assertEquals(result.pointsUsed, false);
    }

    @Test
    public void shouldNotBeAbleToBookFlightIfThereAreNoAvailableSeats(){
        BookingResult result = bookingSystem.bookFlight(
            1,
            LocalDateTime.now(),
            0,
            0,
            0,
            false,
            null,
            0
        );

        assertTrue(result.totalPrice == 0);
        assertTrue(result.refundAmount == 0);
        assertEquals(result.confirmation, false);
        assertEquals(result.pointsUsed, false);
    }

    @Test
    public void shouldBeAbleToBookFlightWithoutLastMinuteFee(){
        BookingResult result = bookingSystem.bookFlight(
            2,
            LocalDateTime.now(),
            100,
            500,
            50,
            false,
            LocalDateTime.now().plusHours(24),
            0
        );

        assertTrue(result.totalPrice == 400);
        assertTrue(result.refundAmount == 0);
        assertEquals(result.confirmation, true);
        assertEquals(result.pointsUsed, false);
    }

    @Test
    public void shouldBeAbleToBookFlightWithLastMinuteFee(){
        BookingResult result = bookingSystem.bookFlight(
            2,
            LocalDateTime.now(),
            100,
            500,
            50,
            false,
            LocalDateTime.now().plusHours(20),
            0
        );
        // priceFactor = 0,4
        // finalPrice = 400 + 100 de last minute fee

        assertTrue(result.totalPrice == 500);
        assertTrue(result.refundAmount == 0);
        assertEquals(result.confirmation, true);
        assertEquals(result.pointsUsed, false);
    }

    @Test
    public void shouldBeAbleToBookFlightWithoutGroupOfPassengersDiscount(){
        BookingResult result = bookingSystem.bookFlight(
            4,
            LocalDateTime.now(),
            100,
            500,
            50,
            false,
            LocalDateTime.now().plusHours(120),
            0
        );

        assertTrue(result.totalPrice == 800);
        assertTrue(result.refundAmount == 0);
        assertEquals(result.confirmation, true);
        assertEquals(result.pointsUsed, false);
    }

    @Test
    public void shouldBeAbleToBookFlightWithGroupOfPassengersDiscount(){
        BookingResult result = bookingSystem.bookFlight(
            5,
            LocalDateTime.now(),
            100,
            500,
            50,
            false,
            LocalDateTime.now().plusHours(120),
            0
        );

        // priceFactor = 0,4
        // finalPrice = 1000 - 50 = 950

        assertTrue(result.totalPrice == 950);
        assertTrue(result.refundAmount == 0);
        assertEquals(result.confirmation, true);
        assertEquals(result.pointsUsed, false);
    }

    @Test
    public void shouldBeAbleToBookFlightWithRewardPointsRedemption(){
        BookingResult result = bookingSystem.bookFlight(
            3,
            LocalDateTime.now(),
            100,
            500,
            50,
            false,
            LocalDateTime.now().plusHours(120),
            5000
        );

        // priceFactor = 0,4
        // finalPrice = 600 - 50 = 550

        assertTrue(result.totalPrice == 550);
        assertTrue(result.refundAmount == 0);
        assertEquals(result.confirmation, true);
        assertEquals(result.pointsUsed, true);
    }

    @Test
    public void shouldBeAbleReceiveFullRefundInACanceledBookFlight(){
        BookingResult result = bookingSystem.bookFlight(
            3,
            LocalDateTime.now(),
            100,
            500,
            50,
            true,
            LocalDateTime.now().plusHours(48),
            0
        );

        // priceFactor = 0,4
        // finalPrice = 600

        assertTrue(result.totalPrice == 0);
        assertTrue(result.refundAmount == 600);
        assertEquals(result.confirmation, false);
        assertEquals(result.pointsUsed, false);
    }

    @Test
    public void shouldBeAbleReceive50PercentRefundInACanceledBookFlight(){
        BookingResult result = bookingSystem.bookFlight(
            3,
            LocalDateTime.now(),
            100,
            500,
            50,
            true,
            LocalDateTime.now().plusHours(47),
            0
        );

        // priceFactor = 0,4
        // finalPrice = 600

        assertTrue(result.totalPrice == 0);
        assertTrue(result.refundAmount == 300);
        assertEquals(result.confirmation, false);
        assertEquals(result.pointsUsed, false);
    }
}
