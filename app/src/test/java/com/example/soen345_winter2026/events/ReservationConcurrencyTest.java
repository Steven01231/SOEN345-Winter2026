package com.example.soen345_winter2026.events;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Concurrency safety tests for the booking flow.
 *
 * Supports NFR: "The system should support concurrent users without performance degradation".
 *
 * These tests do NOT talk to Firestore. They exercise a local model of the transactional
 * check-and-decrement semantics that {@link ReservationRepository#bookEvent} relies on,
 * and verify that under N concurrent bookers we get exactly the expected number of
 * successes — i.e. seats can never be over-sold.
 *
 * The production implementation enforces this through a Firestore transaction
 * (atomic read-modify-write on availableSeats + idempotent reservation ID per user).
 * Here we model that contract with a lock-protected seat counter and confirm the
 * invariants still hold under heavy contention.
 */
class ReservationConcurrencyTest {

    /**
     * Minimal in-memory stand-in for the event + reservation state that
     * {@link ReservationRepository#bookEvent} mutates inside a Firestore transaction.
     * The synchronized {@code tryBook} mirrors the transactional guarantees:
     *   - at most one reservation per (userId, eventId)
     *   - availableSeats decremented atomically, never below zero
     */
    private static final class TransactionalSeatStore {
        private int availableSeats;
        private final java.util.Set<String> activeReservations = new java.util.HashSet<>();

        TransactionalSeatStore(int availableSeats) {
            this.availableSeats = availableSeats;
        }

        synchronized boolean tryBook(String userId, String eventId) {
            String key = userId + "_" + eventId;
            if (activeReservations.contains(key)) return false;
            if (availableSeats <= 0) return false;
            availableSeats--;
            activeReservations.add(key);
            return true;
        }

        synchronized int remainingSeats() {
            return availableSeats;
        }

        synchronized int activeReservationCount() {
            return activeReservations.size();
        }
    }

    @Nested
    @DisplayName("parallel bookEvent invocations")
    class ParallelBookings {

        @Test
        @DisplayName("exactly one booker wins the last seat when N threads race for it")
        void lastSeat_exactlyOneWinner() throws InterruptedException {
            TransactionalSeatStore store = new TransactionalSeatStore(1);
            int bookers = 50;
            String eventId = "evt-1";
            ExecutorService pool = Executors.newFixedThreadPool(bookers);
            CountDownLatch startGate = new CountDownLatch(1);
            AtomicInteger successes = new AtomicInteger();
            AtomicInteger failures = new AtomicInteger();

            for (int i = 0; i < bookers; i++) {
                String userId = "user-" + i;
                pool.submit(() -> {
                    try {
                        startGate.await();
                        if (store.tryBook(userId, eventId)) successes.incrementAndGet();
                        else failures.incrementAndGet();
                    } catch (InterruptedException ignored) {
                        Thread.currentThread().interrupt();
                    }
                });
            }

            startGate.countDown();
            pool.shutdown();
            assertTrue(pool.awaitTermination(5, TimeUnit.SECONDS), "pool did not finish in time");

            assertEquals(1, successes.get(), "exactly one booker should win the last seat");
            assertEquals(bookers - 1, failures.get(), "all other bookers must be rejected");
            assertEquals(0, store.remainingSeats(), "seat counter must land at 0, never negative");
            assertEquals(1, store.activeReservationCount(), "only one active reservation must exist");
        }

        @Test
        @DisplayName("N seats + M>N concurrent bookers yields exactly N successful bookings")
        void limitedSeats_neverOversold() throws InterruptedException {
            int seats = 10;
            int bookers = 200;
            TransactionalSeatStore store = new TransactionalSeatStore(seats);
            String eventId = "evt-limited";
            ExecutorService pool = Executors.newFixedThreadPool(32);
            CountDownLatch startGate = new CountDownLatch(1);
            AtomicInteger successes = new AtomicInteger();

            for (int i = 0; i < bookers; i++) {
                String userId = "user-" + i;
                pool.submit(() -> {
                    try {
                        startGate.await();
                        if (store.tryBook(userId, eventId)) successes.incrementAndGet();
                    } catch (InterruptedException ignored) {
                        Thread.currentThread().interrupt();
                    }
                });
            }

            startGate.countDown();
            pool.shutdown();
            assertTrue(pool.awaitTermination(5, TimeUnit.SECONDS), "pool did not finish in time");

            assertEquals(seats, successes.get(), "exactly `seats` bookings must succeed");
            assertEquals(0, store.remainingSeats(), "all seats consumed, none left");
            assertEquals(seats, store.activeReservationCount(), "active reservations must equal seats sold");
        }

        @Test
        @DisplayName("same user booking the same event in parallel only succeeds once")
        void sameUserDuplicate_onlyOneSucceeds() throws InterruptedException {
            TransactionalSeatStore store = new TransactionalSeatStore(100);
            String userId = "dup-user";
            String eventId = "evt-dup";
            int attempts = 32;
            ExecutorService pool = Executors.newFixedThreadPool(attempts);
            CountDownLatch startGate = new CountDownLatch(1);
            List<Boolean> results = java.util.Collections.synchronizedList(new ArrayList<>());

            for (int i = 0; i < attempts; i++) {
                pool.submit(() -> {
                    try {
                        startGate.await();
                        results.add(store.tryBook(userId, eventId));
                    } catch (InterruptedException ignored) {
                        Thread.currentThread().interrupt();
                    }
                });
            }

            startGate.countDown();
            pool.shutdown();
            assertTrue(pool.awaitTermination(5, TimeUnit.SECONDS), "pool did not finish in time");

            long successCount = results.stream().filter(Boolean::booleanValue).count();
            assertEquals(1, successCount, "a single user must not be able to double-book the same event");
            assertEquals(99, store.remainingSeats(), "only one seat should have been consumed");
        }
    }
}
