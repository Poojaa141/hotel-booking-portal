package com.pooja.hotelbooking.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pooja.hotelbooking.dto.BookingRequest;
import com.pooja.hotelbooking.dto.BookingResponse;
import com.pooja.hotelbooking.dto.QuoteRequest;
import com.pooja.hotelbooking.dto.QuoteResponse;
import com.pooja.hotelbooking.entity.Booking;
import com.pooja.hotelbooking.entity.BookingStatus;
import com.pooja.hotelbooking.entity.Hotel;
import com.pooja.hotelbooking.entity.Room;
import com.pooja.hotelbooking.exception.BadRequestException;
import com.pooja.hotelbooking.exception.NotFoundException;
import com.pooja.hotelbooking.exception.SoldOutException;
import com.pooja.hotelbooking.repository.BookingRepository;
import com.pooja.hotelbooking.repository.RoomRepository;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void totalAmount_isNightsTimesPrice() {
        // Arrange
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = checkIn.plusDays(3);
        Room room = createRoom(10L, 5, "2000.00");
        QuoteRequest request = createQuoteRequest(10L, checkIn, checkOut);
        when(roomRepository.findById(10L)).thenReturn(Optional.of(room));
        when(bookingRepository.countOverlapping(10L, checkIn, checkOut)).thenReturn(0L);

        // Act
        QuoteResponse response = bookingService.createQuote(request);

        // Assert
        assertEquals(3, response.getNights());
        assertEquals(new BigDecimal("6000.00"), response.getTotalAmount());
    }

    @Test
    void checkOutEqualToCheckIn_throwsBadRequestException() {
        LocalDate checkIn = LocalDate.now().plusDays(1);
        Room room = createRoom(10L, 5, "2000.00");
        when(roomRepository.findById(10L)).thenReturn(Optional.of(room));

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bookingService.createQuote(createQuoteRequest(10L, checkIn, checkIn)));

        assertEquals("Check-out date must be after check-in date", exception.getMessage());
    }

    @Test
    void checkOutBeforeCheckIn_throwsBadRequestException() {
        LocalDate checkIn = LocalDate.now().plusDays(2);
        LocalDate checkOut = checkIn.minusDays(1);
        Room room = createRoom(10L, 5, "2000.00");
        when(roomRepository.findById(10L)).thenReturn(Optional.of(room));

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bookingService.createQuote(createQuoteRequest(10L, checkIn, checkOut)));

        assertEquals("Check-out date must be after check-in date", exception.getMessage());
    }

    @Test
    void checkInInPast_throwsBadRequestException() {
        LocalDate checkIn = LocalDate.now().minusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(2);
        Room room = createRoom(10L, 5, "2000.00");
        when(roomRepository.findById(10L)).thenReturn(Optional.of(room));

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bookingService.createQuote(createQuoteRequest(10L, checkIn, checkOut)));

        assertEquals("Check-in date cannot be in the past", exception.getMessage());
    }

    @Test
    void soldOutBooking_throwsSoldOutExceptionAndIsNotSaved() {
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = checkIn.plusDays(3);
        Room room = createRoom(10L, 2, "2000.00");
        when(roomRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(room));
        when(bookingRepository.countOverlapping(10L, checkIn, checkOut)).thenReturn(2L);

        SoldOutException exception = assertThrows(SoldOutException.class,
                () -> bookingService.createBooking(createBookingRequest(10L, checkIn, checkOut)));

        assertEquals("No rooms available for the selected dates", exception.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void cancelConfirmedBooking_setsCancelledAndDoesNotDelete() {
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = checkIn.plusDays(3);
        Booking booking = createBooking("HB-TEST1234", BookingStatus.CONFIRMED, checkIn, checkOut);
        when(bookingRepository.findByBookingRefAndGuestEmail("HB-TEST1234", "guest@test.com"))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingResponse response = bookingService.cancelBooking("hb-test1234", " GUEST@Test.COM ");

        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        assertEquals(BookingStatus.CANCELLED, response.getStatus());
        verify(bookingRepository).save(booking);
        verify(bookingRepository, never()).delete(any(Booking.class));
    }

    @Test
    void cancelAlreadyCancelledBooking_throwsBadRequestException() {
        LocalDate checkIn = LocalDate.now().plusDays(1);
        Booking booking = createBooking("HB-TEST1234", BookingStatus.CANCELLED,
                checkIn, checkIn.plusDays(2));
        when(bookingRepository.findByBookingRefAndGuestEmail("HB-TEST1234", "guest@test.com"))
                .thenReturn(Optional.of(booking));

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bookingService.cancelBooking("HB-TEST1234", "guest@test.com"));

        assertEquals("This booking is already cancelled", exception.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void getOrCancel_wrongReferenceOrEmail_throwsNotFoundException() {
        when(bookingRepository.findByBookingRefAndGuestEmail(any(String.class), any(String.class)))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.getBooking("wrong-ref", "guest@test.com"));
        assertThrows(NotFoundException.class,
                () -> bookingService.getBooking("HB-TEST1234", "wrong@test.com"));
        assertThrows(NotFoundException.class,
                () -> bookingService.cancelBooking("wrong-ref", "guest@test.com"));
        assertThrows(NotFoundException.class,
                () -> bookingService.cancelBooking("HB-TEST1234", "wrong@test.com"));
    }

    private QuoteRequest createQuoteRequest(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        QuoteRequest request = new QuoteRequest();
        request.setRoomId(roomId);
        request.setCheckIn(checkIn);
        request.setCheckOut(checkOut);
        return request;
    }

    private BookingRequest createBookingRequest(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        BookingRequest request = new BookingRequest();
        request.setRoomId(roomId);
        request.setGuestName("Guest Test");
        request.setGuestEmail("guest@test.com");
        request.setCheckIn(checkIn);
        request.setCheckOut(checkOut);
        return request;
    }

    private Room createRoom(Long id, int totalRooms, String pricePerNight) {
        Hotel hotel = new Hotel();
        hotel.setName("Test Hotel");
        hotel.setLocation("Test City");

        Room room = new Room();
        room.setId(id);
        room.setHotel(hotel);
        room.setRoomType("Deluxe");
        room.setPricePerNight(new BigDecimal(pricePerNight));
        room.setTotalRooms(totalRooms);
        return room;
    }

    private Booking createBooking(String reference, BookingStatus status, LocalDate checkIn, LocalDate checkOut) {
        Room room = createRoom(10L, 3, "2000.00");
        Booking booking = new Booking();
        booking.setBookingRef(reference);
        booking.setRoom(room);
        booking.setGuestName("Guest Test");
        booking.setGuestEmail("guest@test.com");
        booking.setCheckIn(checkIn);
        booking.setCheckOut(checkOut);
        booking.setNights((int) java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut));
        booking.setTotalAmount(new BigDecimal("6000.00"));
        booking.setStatus(status);
        booking.setCreatedAt(LocalDateTime.now());
        return booking;
    }
}
