package com.example.soen345_winter2026.events;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class ReservationStatusTest {

    @Test
    void values_containsActiveAndCancelled() {
        assertArrayEquals(
                new ReservationStatus[]{ReservationStatus.ACTIVE, ReservationStatus.CANCELLED},
                ReservationStatus.values()
        );
    }

    @Test
    void valueOf_parsesActive() {
        assertEquals(ReservationStatus.ACTIVE, ReservationStatus.valueOf("ACTIVE"));
    }

    @Test
    void valueOf_parsesCancelled() {
        assertEquals(ReservationStatus.CANCELLED, ReservationStatus.valueOf("CANCELLED"));
    }

    @Test
    void ordinals_areStable() {
        assertEquals(0, ReservationStatus.ACTIVE.ordinal());
        assertEquals(1, ReservationStatus.CANCELLED.ordinal());
    }

    @Test
    void name_matchesEnumConstant() {
        assertNotNull(ReservationStatus.ACTIVE.name());
        assertEquals("CANCELLED", ReservationStatus.CANCELLED.name());
    }
}
