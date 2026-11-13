package com.pooja.hotelbooking.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.pooja.hotelbooking.entity.Booking;
import com.pooja.hotelbooking.entity.BookingStatus;
import com.pooja.hotelbooking.entity.Hotel;
import com.pooja.hotelbooking.entity.Room;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void countOverlapping_countsStayInsideExistingBooking() {
        LocalDate existingCheckIn = LocalDate.now().plusDays(10);
        LocalDate existingCheckOut = existingCheckIn.plusDays(5);
        Room room = createRoom("Deluxe");
        saveBooking(room, BookingStatus.CONFIRMED, existingCheckIn, existingCheckOut);

        long count = bookingRepository.countOverlapping(
                room.getId(), existingCheckIn.plusDays(1), existingCheckOut.minusDays(1));

        assertEquals(1, count);
    }

    @Test
    void countOverlapping_doesNotCountStayStartingOnExistingCheckOutDay() {
        LocalDate existingCheckIn = LocalDate.now().plusDays(10);
        LocalDate existingCheckOut = existingCheckIn.plusDays(3);
        Room room = createRoom("Standard");
        saveBooking(room, BookingStatus.CONFIRMED, existingCheckIn, existingCheckOut);

        long count = bookingRepository.countOverlapping(
                room.getId(), existingCheckOut, existingCheckOut.plusDays(2));

        assertEquals(0, count);
    }

    @Test
    void countOverlapping_doesNotCountStayEndingOnExistingCheckInDay() {
        LocalDate existingCheckIn = LocalDate.now().plusDays(10);
        LocalDate existingCheckOut = existingCheckIn.plusDays(3);
        Room room = createRoom("Standard");
        saveBooking(room, BookingStatus.CONFIRMED, existingCheckIn, existingCheckOut);

        long count = bookingRepository.countOverlapping(
                room.getId(), existingCheckIn.minusDays(2), existingCheckIn);

        assertEquals(0, count);
    }

    @Test
    void countOverlapping_countsPartlyOverlappingStay() {
        LocalDate existingCheckIn = LocalDate.now().plusDays(10);
        LocalDate existingCheckOut = existingCheckIn.plusDays(3);
        Room room = createRoom("Suite");
        saveBooking(room, BookingStatus.CONFIRMED, existingCheckIn, existingCheckOut);

        long count = bookingRepository.countOverlapping(
                room.getId(), existingCheckIn.minusDays(2), existingCheckOut.minusDays(1));

        assertEquals(1, count);
    }

    @Test
    void countOverlapping_doesNotCountCancelledBooking() {
        LocalDate checkIn = LocalDate.now().plusDays(10);
        Room room = createRoom("Deluxe");
        saveBooking(room, BookingStatus.CANCELLED, checkIn, checkIn.plusDays(3));

        long count = bookingRepository.countOverlapping(room.getId(), checkIn, checkIn.plusDays(2));

        assertEquals(0, count);
    }

    @Test
    void countOverlapping_doesNotCountBookingForDifferentRoom() {
        LocalDate checkIn = LocalDate.now().plusDays(10);
        Room bookedRoom = createRoom("Standard");
        Room requestedRoom = createRoom("Deluxe");
        saveBooking(bookedRoom, BookingStatus.CONFIRMED, checkIn, checkIn.plusDays(3));

        long count = bookingRepository.countOverlapping(
                requestedRoom.getId(), checkIn, checkIn.plusDays(2));

        assertEquals(0, count);
    }

    private Room createRoom(String roomType) {
        Hotel hotel = new Hotel();
        hotel.setName("Test Hotel");
        hotel.setLocation("Test City");
        hotel.setDescription("A hotel for repository tests");
        entityManager.persist(hotel);

        Room room = new Room();
        room.setHotel(hotel);
        room.setRoomType(roomType);
        room.setPricePerNight(new BigDecimal("4500.00"));
        room.setTotalRooms(3);
        entityManager.persist(room);
        entityManager.flush();
        return room;
    }

    private void saveBooking(Room room, BookingStatus status, LocalDate checkIn, LocalDate checkOut) {
        Booking booking = new Booking();
        booking.setBookingRef("HB-" + room.getId() + "-" + status.name());
        booking.setRoom(room);
        booking.setGuestName("Test Guest");
        booking.setGuestEmail("test@example.com");
        booking.setCheckIn(checkIn);
        booking.setCheckOut(checkOut);
        booking.setNights((int) java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut));
        booking.setTotalAmount(new BigDecimal("13500.00"));
        booking.setStatus(status);
        booking.setCreatedAt(LocalDateTime.now());
        entityManager.persist(booking);
        entityManager.flush();
    }
}
