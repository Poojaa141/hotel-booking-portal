package com.pooja.hotelbooking.dto;

import java.math.BigDecimal;
import java.util.List;

public class HotelResponse {

    private Long id;
    private String name;
    private String location;
    private String description;
    private String imageUrl;
    private List<String> amenities;
    private List<RoomOptionResponse> rooms;
    private BigDecimal startingPrice;
    private int roomsLeft;
    private boolean available;
    private boolean bestDeal;

    public HotelResponse() {
    }

    public HotelResponse(Long id, String name, String location, String description, String imageUrl,
            List<String> amenities, List<RoomOptionResponse> rooms, BigDecimal startingPrice, int roomsLeft,
            boolean available) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.description = description;
        this.imageUrl = imageUrl;
        this.amenities = amenities;
        this.rooms = rooms;
        this.startingPrice = startingPrice;
        this.roomsLeft = roomsLeft;
        this.available = available;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public List<RoomOptionResponse> getRooms() {
        return rooms;
    }

    public BigDecimal getStartingPrice() {
        return startingPrice;
    }

    public int getRoomsLeft() {
        return roomsLeft;
    }

    public boolean isAvailable() {
        return available;
    }

    public boolean isBestDeal() {
        return bestDeal;
    }

    public void setBestDeal(boolean bestDeal) {
        this.bestDeal = bestDeal;
    }
}
