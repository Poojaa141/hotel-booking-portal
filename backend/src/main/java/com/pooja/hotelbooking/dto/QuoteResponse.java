package com.pooja.hotelbooking.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class QuoteResponse {

    private Long roomId;
    private String hotelName;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private long nights;
    private BigDecimal pricePerNight;
    private BigDecimal totalAmount;
    private int roomsLeft;
    private boolean available;

    public QuoteResponse() {
    }

    public QuoteResponse(Long roomId, String hotelName, LocalDate checkIn, LocalDate checkOut, long nights,
            BigDecimal pricePerNight, BigDecimal totalAmount, int roomsLeft, boolean available) {
        this.roomId = roomId;
        this.hotelName = hotelName;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.nights = nights;
        this.pricePerNight = pricePerNight;
        this.totalAmount = totalAmount;
        this.roomsLeft = roomsLeft;
        this.available = available;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalDate checkIn) {
        this.checkIn = checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalDate checkOut) {
        this.checkOut = checkOut;
    }

    public long getNights() {
        return nights;
    }

    public void setNights(long nights) {
        this.nights = nights;
    }

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getRoomsLeft() {
        return roomsLeft;
    }

    public void setRoomsLeft(int roomsLeft) {
        this.roomsLeft = roomsLeft;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
