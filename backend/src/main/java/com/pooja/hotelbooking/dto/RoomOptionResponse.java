package com.pooja.hotelbooking.dto;

import java.math.BigDecimal;

public class RoomOptionResponse {

    private Long roomId;
    private String type;
    private BigDecimal pricePerNight;
    private int roomsLeft;
    private boolean available;

    public RoomOptionResponse(Long roomId, String type, BigDecimal pricePerNight, int roomsLeft, boolean available) {
        this.roomId = roomId;
        this.type = type;
        this.pricePerNight = pricePerNight;
        this.roomsLeft = roomsLeft;
        this.available = available;
    }

    public Long getRoomId() {
        return roomId;
    }

    public String getType() {
        return type;
    }

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public int getRoomsLeft() {
        return roomsLeft;
    }

    public boolean isAvailable() {
        return available;
    }
}
