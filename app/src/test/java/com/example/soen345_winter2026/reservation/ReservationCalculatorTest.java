package com.example.soen345_winter2026.reservation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class ReservationCalculatorTest {
    @Test
    void startsWithOneTicketWhenSeatsAvailable() {
        ReservationCalculator calc = new ReservationCalculator(5, 20.0);

        assertEquals(1, calc.getTicketCount());
        assertEquals(20.0, calc.getTotalPrice(), 0.001);
        assertTrue(calc.canConfirm());
    }

    @Test
    void startsWithZeroWhenNoSeatsAvailable() {
        ReservationCalculator calc = new ReservationCalculator(0, 20.0);

        assertEquals(0, calc.getTicketCount());
        assertEquals(0.0, calc.getTotalPrice(), 0.001);
        assertFalse(calc.canConfirm());
    }

    @Test
    void increaseWorksUntilSeatLimit() {
        ReservationCalculator calc = new ReservationCalculator(2, 20.0);

        assertTrue(calc.increase());
        assertEquals(2, calc.getTicketCount());

        assertFalse(calc.increase());
        assertEquals(2, calc.getTicketCount());
    }

    @Test
    void decreaseDoesNotGoBelowOne() {
        ReservationCalculator calc = new ReservationCalculator(5, 20.0);

        assertFalse(calc.decrease());
        assertEquals(1, calc.getTicketCount());
    }
}
