package com.pooja.hotelbooking.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void roomsLeft_sumsRoomTypesAndNeverGoesBelowZero() {
        Hotel hotel = createHotel(1L, "Room Count Hotel");
        Room standard = createRoom(11L, hotel, 4, "3000.00", "Standard");
        Room deluxe = createRoom(12L, hotel, 2, "4000.00", "Deluxe");
        when(hotelRepository.findAll()).thenReturn(List.of(hotel));
        stubRooms(hotel, List.of(standard, deluxe), 1L, 5L);

        HotelResponse response = hotelService.getHotels(null, checkIn, checkOut).get(0);

        assertEquals(3, response.getRoomsLeft());
        assertTrue(response.isAvailable());
        assertEquals(3, response.getRooms().get(0).getRoomsLeft());
        assertEquals(0, response.getRooms().get(1).getRoomsLeft());
    }

    @Test
    void hotelIsUnavailableOnlyWhenAllRoomTypesAreSoldOut() {
        Hotel partlyAvailable = createHotel(1L, "Partly Available Hotel");
        Hotel fullySoldOut = createHotel(2L, "Fully Sold Out Hotel");
        Room standard = createRoom(11L, partlyAvailable, 4, "3000.00", "Standard");
        Room deluxe = createRoom(12L, partlyAvailable, 2, "4000.00", "Deluxe");
        Room soldOutStandard = createRoom(21L, fullySoldOut, 4, "2800.00", "Standard");
        Room soldOutSuite = createRoom(22L, fullySoldOut, 1, "5000.00", "Suite");
        when(hotelRepository.findAll()).thenReturn(List.of(partlyAvailable, fullySoldOut));
        stubRooms(partlyAvailable, List.of(standard, deluxe), 4L, 1L);
        stubRooms(fullySoldOut, List.of(soldOutStandard, soldOutSuite), 5L, 1L);

        List<HotelResponse> responses = hotelService.getHotels(null, checkIn, checkOut);

        assertTrue(responseFor(responses, 1L).isAvailable());
        assertEquals(1, responseFor(responses, 1L).getRoomsLeft());
        assertFalse(responseFor(responses, 2L).isAvailable());
        assertEquals(0, responseFor(responses, 2L).getRoomsLeft());
    }

    @Test
    void bestDeal_usesLowestStartingPriceAmongAvailableHotels() {
        Hotel firstHotel = createHotel(1L, "Sold Out Cheapest Room Hotel");
        Hotel bestHotel = createHotel(2L, "Best Price Hotel");
        Hotel thirdHotel = createHotel(3L, "Partly Sold Out Hotel");
        Room firstStandard = createRoom(11L, firstHotel, 2, "3000.00", "Standard");
        Room firstDeluxe = createRoom(12L, firstHotel, 2, "5000.00", "Deluxe");
        Room bestStandard = createRoom(21L, bestHotel, 3, "3500.00", "Standard");
        Room thirdStandard = createRoom(31L, thirdHotel, 1, "2500.00", "Standard");
        Room thirdSuite = createRoom(32L, thirdHotel, 2, "6000.00", "Suite");
        when(hotelRepository.findAll()).thenReturn(List.of(firstHotel, bestHotel, thirdHotel));
        stubRooms(firstHotel, List.of(firstStandard, firstDeluxe), 2L, 0L);
        stubRooms(bestHotel, List.of(bestStandard), 0L);
        stubRooms(thirdHotel, List.of(thirdStandard, thirdSuite), 1L, 0L);

        List<HotelResponse> responses = hotelService.getHotels(null, checkIn, checkOut);

        assertEquals(new BigDecimal("5000.00"), responseFor(responses, 1L).getStartingPrice());
        assertEquals(new BigDecimal("3500.00"), responseFor(responses, 2L).getStartingPrice());
        assertEquals(new BigDecimal("6000.00"), responseFor(responses, 3L).getStartingPrice());
        assertFalse(responseFor(responses, 1L).isBestDeal());
        assertTrue(responseFor(responses, 2L).isBestDeal());
        assertFalse(responseFor(responses, 3L).isBestDeal());
        assertEquals(1, responses.stream().filter(HotelResponse::isBestDeal).count());
    }

    private Hotel createHotel(Long id, String name) {
        Hotel hotel = new Hotel();
        hotel.setId(id);
        hotel.setName(name);
        hotel.setLocation("Goa");
        hotel.setAmenities("WiFi, Breakfast");
        return hotel;
    }

    private Room createRoom(Long id, Hotel hotel, int totalRooms, String pricePerNight, String type) {
        Room room = new Room();
        room.setId(id);
        room.setHotel(hotel);
        room.setRoomType(type);
        room.setTotalRooms(totalRooms);
        room.setPricePerNight(new BigDecimal(pricePerNight));
        return room;
    }

    private void stubRooms(Hotel hotel, List<Room> rooms, long... overlappingCounts) {
        when(roomRepository.findAllByHotelIdOrderByIdAsc(hotel.getId())).thenReturn(rooms);
        for (int index = 0; index < rooms.size(); index++) {
            when(bookingRepository.countOverlapping(
                    eq(rooms.get(index).getId()), eq(checkIn), eq(checkOut)))
                    .thenReturn(overlappingCounts[index]);
        }
    }

    private HotelResponse responseFor(List<HotelResponse> responses, Long id) {
        return responses.stream()
                .filter(response -> response.getId().equals(id))
                .findFirst()
                .orElseThrow();
    }
}
