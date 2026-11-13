package com.pooja.hotelbooking.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pooja.hotelbooking.dto.HotelResponse;
import com.pooja.hotelbooking.entity.Hotel;
import com.pooja.hotelbooking.entity.Room;
import com.pooja.hotelbooking.repository.BookingRepository;
import com.pooja.hotelbooking.repository.HotelRepository;
import com.pooja.hotelbooking.repository.RoomRepository;

@ExtendWith(MockitoExtension.class)
class HotelServiceTest {

    private final LocalDate checkIn = LocalDate.now().plusDays(1);
    private final LocalDate checkOut = checkIn.plusDays(3);

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private HotelService hotelService;

    @Test
    void cheapestAvailableHotel_getsBestDeal() {
        Hotel expensiveHotel = createHotel(1L, "Expensive Hotel", "Goa");
        Hotel cheapestHotel = createHotel(2L, "Cheapest Hotel", "Goa");
        Hotel middleHotel = createHotel(3L, "Middle Hotel", "Goa");
        Room expensiveRoom = createRoom(11L, expensiveHotel, 3, "3000.00");
        Room cheapestRoom = createRoom(22L, cheapestHotel, 3, "2000.00");
        Room middleRoom = createRoom(33L, middleHotel, 3, "2500.00");
        when(hotelRepository.findAll()).thenReturn(List.of(expensiveHotel, cheapestHotel, middleHotel));
        stubAvailability(expensiveHotel, expensiveRoom, 0L);
        stubAvailability(cheapestHotel, cheapestRoom, 0L);
        stubAvailability(middleHotel, middleRoom, 0L);

        List<HotelResponse> responses = hotelService.getHotels(null, checkIn, checkOut);

        assertFalse(responseFor(responses, 1L).isBestDeal());
        assertTrue(responseFor(responses, 2L).isBestDeal());
        assertFalse(responseFor(responses, 3L).isBestDeal());
    }

    @Test
    void soldOutCheapestHotel_neverGetsBestDeal() {
        Hotel cheapestHotel = createHotel(1L, "Sold Out Hotel", "Goa");
        Hotel availableHotel = createHotel(2L, "Available Hotel", "Goa");
        Room cheapestRoom = createRoom(11L, cheapestHotel, 1, "1000.00");
        Room availableRoom = createRoom(22L, availableHotel, 2, "2000.00");
        when(hotelRepository.findAll()).thenReturn(List.of(cheapestHotel, availableHotel));
        stubAvailability(cheapestHotel, cheapestRoom, 1L);
        stubAvailability(availableHotel, availableRoom, 0L);

        List<HotelResponse> responses = hotelService.getHotels(null, checkIn, checkOut);

        assertFalse(responseFor(responses, 1L).isAvailable());
        assertFalse(responseFor(responses, 1L).isBestDeal());
        assertTrue(responseFor(responses, 2L).isBestDeal());
    }

    @Test
    void noAvailableHotels_noHotelGetsBestDeal() {
        Hotel firstHotel = createHotel(1L, "First Hotel", "Goa");
        Hotel secondHotel = createHotel(2L, "Second Hotel", "Goa");
        Room firstRoom = createRoom(11L, firstHotel, 1, "1000.00");
        Room secondRoom = createRoom(22L, secondHotel, 2, "2000.00");
        when(hotelRepository.findAll()).thenReturn(List.of(firstHotel, secondHotel));
        stubAvailability(firstHotel, firstRoom, 1L);
        stubAvailability(secondHotel, secondRoom, 2L);

        List<HotelResponse> responses = hotelService.getHotels(null, checkIn, checkOut);

        assertFalse(responseFor(responses, 1L).isAvailable());
        assertFalse(responseFor(responses, 2L).isAvailable());
        assertFalse(responseFor(responses, 1L).isBestDeal());
        assertFalse(responseFor(responses, 2L).isBestDeal());
    }

    private Hotel createHotel(Long id, String name, String location) {
        Hotel hotel = new Hotel();
        hotel.setId(id);
        hotel.setName(name);
        hotel.setLocation(location);
        hotel.setAmenities("WiFi, Breakfast");
        return hotel;
    }

    private Room createRoom(Long id, Hotel hotel, int totalRooms, String pricePerNight) {
        Room room = new Room();
        room.setId(id);
        room.setHotel(hotel);
        room.setRoomType("Deluxe");
        room.setTotalRooms(totalRooms);
        room.setPricePerNight(new BigDecimal(pricePerNight));
        return room;
    }

    private void stubAvailability(Hotel hotel, Room room, long overlappingBookings) {
        when(roomRepository.findAllByHotelIdOrderByIdAsc(hotel.getId())).thenReturn(List.of(room));
        when(bookingRepository.countOverlapping(eq(room.getId()), eq(checkIn), eq(checkOut)))
                .thenReturn(overlappingBookings);
    }

    private HotelResponse responseFor(List<HotelResponse> responses, Long id) {
        return responses.stream()
                .filter(response -> response.getId().equals(id))
                .findFirst()
                .orElseThrow();
    }
}
