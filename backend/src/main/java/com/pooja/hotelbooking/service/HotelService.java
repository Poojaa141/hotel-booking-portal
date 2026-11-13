package com.pooja.hotelbooking.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pooja.hotelbooking.dto.HotelResponse;
import com.pooja.hotelbooking.dto.RoomOptionResponse;
import com.pooja.hotelbooking.entity.Hotel;
import com.pooja.hotelbooking.entity.Room;
import com.pooja.hotelbooking.exception.BadRequestException;
import com.pooja.hotelbooking.exception.NotFoundException;
import com.pooja.hotelbooking.repository.BookingRepository;
import com.pooja.hotelbooking.repository.HotelRepository;
import com.pooja.hotelbooking.repository.RoomRepository;
import com.pooja.hotelbooking.util.DateValidator;

@Service
@Transactional(readOnly = true)
public class HotelService {

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    public HotelService(HotelRepository hotelRepository, RoomRepository roomRepository,
            BookingRepository bookingRepository) {
        this.hotelRepository = hotelRepository;
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<HotelResponse> getHotels(String location, LocalDate checkIn, LocalDate checkOut) {
        validateDates(checkIn, checkOut);

        List<Hotel> hotels;
        if (location == null || location.isBlank()) {
            hotels = hotelRepository.findAll();
        } else {
            hotels = hotelRepository.findAllByLocationContainingIgnoreCase(location);
        }

        List<HotelResponse> responses = hotels.stream()
                .map(hotel -> createResponse(hotel, checkIn, checkOut))
                .toList();
        markBestDeal(responses);
        return responses;
    }

    public HotelResponse getHotel(Long id, LocalDate checkIn, LocalDate checkOut) {
        validateDates(checkIn, checkOut);

        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Hotel not found"));
        return createResponse(hotel, checkIn, checkOut);
    }

    private HotelResponse createResponse(Hotel hotel, LocalDate checkIn, LocalDate checkOut) {
        List<Room> hotelRooms = roomRepository.findAllByHotelIdOrderByIdAsc(hotel.getId());
        if (hotelRooms.isEmpty()) {
            throw new IllegalStateException("Room not found for hotel");
        }

        List<RoomOptionResponse> roomResponses = hotelRooms.stream()
                .map(room -> createRoomResponse(room, checkIn, checkOut))
                .sorted(Comparator.comparingInt(room -> roomTypeOrder(room.getType())))
                .toList();
        int roomsLeft = roomResponses.stream()
                .mapToInt(RoomOptionResponse::getRoomsLeft)
                .sum();
        boolean available = roomResponses.stream().anyMatch(RoomOptionResponse::isAvailable);
        BigDecimal startingPrice = roomResponses.stream()
                .filter(RoomOptionResponse::isAvailable)
                .map(RoomOptionResponse::getPricePerNight)
                .min(BigDecimal::compareTo)
                .orElseGet(() -> roomResponses.stream()
                        .map(RoomOptionResponse::getPricePerNight)
                        .min(BigDecimal::compareTo)
                        .orElseThrow());
        List<String> amenities = hotel.getAmenities() == null || hotel.getAmenities().isBlank()
                ? List.of()
                : Arrays.stream(hotel.getAmenities().split(","))
                        .map(String::trim)
                        .filter(amenity -> !amenity.isEmpty())
                        .toList();

        return new HotelResponse(
                hotel.getId(),
                hotel.getName(),
                hotel.getLocation(),
                hotel.getDescription(),
                hotel.getImageUrl(),
                amenities,
                roomResponses,
                startingPrice,
                roomsLeft,
                available);
    }

    private RoomOptionResponse createRoomResponse(Room room, LocalDate checkIn, LocalDate checkOut) {
        long overlappingBookings = 0;
        if (checkIn != null && checkOut != null) {
            overlappingBookings = bookingRepository.countOverlapping(room.getId(), checkIn, checkOut);
        }

        int roomsLeft = (int) Math.max(0L, room.getTotalRooms() - overlappingBookings);
        return new RoomOptionResponse(
                room.getId(),
                room.getRoomType(),
                room.getPricePerNight(),
                roomsLeft,
                roomsLeft > 0);
    }

    private int roomTypeOrder(String type) {
        return switch (type) {
            case "Standard" -> 0;
            case "Deluxe" -> 1;
            case "Suite" -> 2;
            default -> 3;
        };
    }

    private void markBestDeal(List<HotelResponse> hotels) {
        BigDecimal lowestPrice = hotels.stream()
                .filter(HotelResponse::isAvailable)
                .map(HotelResponse::getStartingPrice)
                .min(BigDecimal::compareTo)
                .orElse(null);

        for (HotelResponse hotel : hotels) {
            boolean bestDeal = lowestPrice != null
                    && hotel.isAvailable()
                    && hotel.getStartingPrice().compareTo(lowestPrice) == 0;
            hotel.setBestDeal(bestDeal);
        }
    }

    private void validateDates(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null && checkOut == null) {
            return;
        }
        if (checkIn == null || checkOut == null) {
            throw new BadRequestException("Please provide both check-in and check-out dates");
        }
        DateValidator.validateDates(checkIn, checkOut);
    }
}
