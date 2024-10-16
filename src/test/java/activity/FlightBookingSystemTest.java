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
            LocalDateTime.now().plusHours(50),
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
            LocalDateTime.now().plusHours(30),
            0
        );

        // priceFactor = 0,4
        // finalPrice = 600

        assertTrue(result.totalPrice == 0);
        assertTrue(result.refundAmount == 300);
        assertEquals(result.confirmation, false);
        assertEquals(result.pointsUsed, false);
    }

    @Test
    public void shouldBeAbleToBookFlightWhenPassengersEqualAvailableSeats() {
        BookingResult result = bookingSystem.bookFlight(
                5,
                LocalDateTime.now(),
                5,
                200,
                50,
                false,
                LocalDateTime.now().plusHours(72),
                0
        );

        assertTrue(result.totalPrice > 0);
        assertTrue(result.refundAmount == 0);
        assertEquals(true, result.confirmation);
        assertEquals(false, result.pointsUsed);
    }

    @Test
    public void shouldNotApplyLastMinuteFeeWhenExactly24HoursToDeparture() {
        BookingResult result = bookingSystem.bookFlight(
                2,
                LocalDateTime.now(),
                10,
                150,
                30,
                false,
                LocalDateTime.now().plusHours(24),
                0
        );

        double expectedPriceFactor = (30 / 100.0) * 0.8;
        double expectedFinalPrice = 150 * expectedPriceFactor * 2;

        assertEquals(expectedFinalPrice, result.totalPrice, 0.001);
        assertTrue(result.refundAmount == 0);
        assertEquals(true, result.confirmation);
        assertEquals(false, result.pointsUsed);
    }

    @Test
    public void shouldNotApplyGroupDiscountWhenPassengersAreFour() {
        BookingResult result = bookingSystem.bookFlight(
                4,
                LocalDateTime.now(),
                10,
                100,
                20,
                false,
                LocalDateTime.now().plusHours(72),
                0
        );

        double expectedPriceFactor = (20 / 100.0) * 0.8;
        double expectedFinalPrice = 100 * expectedPriceFactor * 4;

        assertEquals(expectedFinalPrice, result.totalPrice, 0.001);
        assertEquals(true, result.confirmation);
    }

    @Test
    public void shouldApplyGroupDiscountWhenPassengersAreFive() {
        BookingResult result = bookingSystem.bookFlight(
                5,
                LocalDateTime.now(),
                10,
                100,
                20,
                false,
                LocalDateTime.now().plusHours(72),
                0
        );

        double expectedPriceFactor = (20 / 100.0) * 0.8;
        double priceBeforeDiscount = 100 * expectedPriceFactor * 5;
        double expectedFinalPrice = priceBeforeDiscount * 0.95;

        assertEquals(expectedFinalPrice, result.totalPrice, 0.001);
        assertEquals(true, result.confirmation);
    }

    @Test
    public void shouldGiveFullRefundWhenCancellationIsExactly48HoursBeforeDeparture() {
        BookingResult result = bookingSystem.bookFlight(
                2,
                LocalDateTime.now(),
                10,
                200,
                50,
                true,
                LocalDateTime.now().plusHours(48),
                0
        );

        double priceFactor = (50 / 100.0) * 0.8;
        double finalPrice = 200 * priceFactor * 2;

        assertEquals(0, result.totalPrice, 0.001);
        assertEquals(finalPrice, result.refundAmount, 0.001);
        assertEquals(false, result.confirmation);
    }

    @Test
    public void shouldGiveHalfRefundWhenCancellationIsLessThan48HoursBeforeDeparture() {
        BookingResult result = bookingSystem.bookFlight(
                2,
                LocalDateTime.now(),
                10,
                200,
                50,
                true,
                LocalDateTime.now().plusHours(47),
                0
        );

        double priceFactor = (50 / 100.0) * 0.8;
        double finalPrice = 200 * priceFactor * 2;

        assertEquals(0, result.totalPrice, 0.001);
        assertEquals(finalPrice * 0.5, result.refundAmount, 0.001);
        assertEquals(false, result.confirmation);
    }

}
