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
    private Long roomId;
    private String roomType;
    private BigDecimal pricePerNight;
    private int totalRooms;
    private int roomsLeft;
    private boolean available;
    private boolean bestDeal;

    public HotelResponse() {
    }

    public HotelResponse(Long id, String name, String location, String description, String imageUrl,
            List<String> amenities,
            Long roomId, String roomType, BigDecimal pricePerNight, int totalRooms, int roomsLeft,
            boolean available) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.description = description;
        this.imageUrl = imageUrl;
        this.amenities = amenities;
        this.roomId = roomId;
        this.roomType = roomType;
        this.pricePerNight = pricePerNight;
        this.totalRooms = totalRooms;
        this.roomsLeft = roomsLeft;
        this.available = available;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<String> amenities) {
        this.amenities = amenities;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public int getTotalRooms() {
        return totalRooms;
    }

    public void setTotalRooms(int totalRooms) {
        this.totalRooms = totalRooms;
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

    public boolean isBestDeal() {
        return bestDeal;
    }

    public void setBestDeal(boolean bestDeal) {
        this.bestDeal = bestDeal;
    }
}
