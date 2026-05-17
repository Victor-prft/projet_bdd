package com.project.artconnect.model;

import java.time.LocalDate;

public class Booking {
    private Workshop workshop;
    private CommunityMember member;
    private LocalDate bookingDate;
    private PaymentStatus paymentStatus; // PENDING, PAID, CANCELLED

    public enum PaymentStatus {
        PENDING, PAID, CANCELLED
    }

    public Booking() {
    }

    public Booking(Workshop workshop, CommunityMember member) {
        this.workshop = workshop;
        this.member = member;
        this.bookingDate = LocalDate.now();
        this.paymentStatus = PaymentStatus.PENDING;
    }

    public Workshop getWorkshop() {
        return workshop;
    }

    public void setWorkshop(Workshop workshop) {
        this.workshop = workshop;
    }

    public CommunityMember getMember() {
        return member;
    }

    public void setMember(CommunityMember member) {
        this.member = member;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
