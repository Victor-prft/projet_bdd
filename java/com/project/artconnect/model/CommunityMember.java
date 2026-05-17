package com.project.artconnect.model;

import java.util.ArrayList;
import java.util.List;

public class CommunityMember {
    private Integer id_member;
    private String name;
    private String email;
    private Integer birthYear;
    private String phone;
    private City city;
    private List<Discipline> favoriteDisciplines = new ArrayList<>();
    private MembershipType membershipType; // free, premium
    private List<Booking> bookings = new ArrayList<>();
    private List<Review> reviews = new ArrayList<>();

    public enum MembershipType {
        free, premium
    }

    public CommunityMember() {
    }

    public CommunityMember(String name, String email) {
        this.name = name;
        this.email = email;
        this.membershipType = MembershipType.free;
    }

    public Integer getId() {
        return id_member;
    }
 
    public void setId(Integer id) {
        this.id_member = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public MembershipType getMembershipType() {
        return membershipType;
    }

    public void setMembershipType(MembershipType membershipType) {
        this.membershipType = membershipType;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public List<Discipline> getFavoriteDisciplines() {
        return favoriteDisciplines;
    }

    public void setFavoriteDisciplines(List<Discipline> favoriteDisciplines) {
        this.favoriteDisciplines = favoriteDisciplines;
    }


    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public void addBooking(Booking booking) {
        this.bookings.add(booking);
        if (booking.getMember() != this) {
            booking.setMember(this);
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
