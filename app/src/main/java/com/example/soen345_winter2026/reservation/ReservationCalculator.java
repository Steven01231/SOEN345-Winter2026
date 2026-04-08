package com.example.soen345_winter2026.reservation;

public class ReservationCalculator {
    private int ticketCount;
    private final int availableSeats;
    private final double pricePerTicket;

    public ReservationCalculator(int availableSeats, double pricePerTicket) {
        this.availableSeats = availableSeats;
        this.pricePerTicket = pricePerTicket;
        this.ticketCount = availableSeats > 0 ? 1 : 0;
    }
    public int getTicketCount() {
        return ticketCount;
    }
    public boolean increase() {
        if (ticketCount < availableSeats) {
            ticketCount++;
            return true;
        }
        return false;
    }
    public boolean decrease() {
        if (ticketCount > 1) {
            ticketCount--;
            return true;
        }
        return false;
    }
    public double getTotalPrice() {
        return ticketCount * pricePerTicket;
    }
    public boolean canConfirm() {
        return availableSeats > 0;
    }
}
