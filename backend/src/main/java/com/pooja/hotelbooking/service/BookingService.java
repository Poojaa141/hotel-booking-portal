package com.pooja.hotelbooking.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pooja.hotelbooking.dto.BookingRequest;
import com.pooja.hotelbooking.dto.BookingResponse;
import com.pooja.hotelbooking.dto.QuoteRequest;
import com.pooja.hotelbooking.dto.QuoteResponse;
import com.pooja.hotelbooking.entity.Booking;
import com.pooja.hotelbooking.entity.BookingStatus;
import com.pooja.hotelbooking.entity.Room;
import com.pooja.hotelbooking.exception.BadRequestException;
import com.pooja.hotelbooking.exception.NotFoundException;
import com.pooja.hotelbooking.exception.SoldOutException;
import com.pooja.hotelbooking.repository.BookingRepository;
import com.pooja.hotelbooking.repository.RoomRepository;
import com.pooja.hotelbooking.util.DateValidator;

@Service
public class BookingService {

    private static final String BOOKING_NOT_FOUND_MESSAGE =
            "Booking not found. Please check your reference and email";

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    public BookingService(RoomRepository roomRepository, BookingRepository bookingRepository) {
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional(readOnly = true)
    public QuoteResponse createQuote(QuoteRequest request) {
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new NotFoundException("Room not found"));
        DateValidator.validateDates(request.getCheckIn(), request.getCheckOut());

        long nights = ChronoUnit.DAYS.between(request.getCheckIn(), request.getCheckOut());
        BigDecimal totalAmount = room.getPricePerNight().multiply(BigDecimal.valueOf(nights));
        long overlappingBookings = bookingRepository.countOverlapping(
                room.getId(), request.getCheckIn(), request.getCheckOut());
        int roomsLeft = (int) Math.max(0L, room.getTotalRooms() - overlappingBookings);

        return new QuoteResponse(
                room.getId(),
                room.getHotel().getName(),
                request.getCheckIn(),
                request.getCheckOut(),
                nights,
                room.getPricePerNight(),
                totalAmount,
                roomsLeft,
                roomsLeft > 0);
    }

    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        Room room = roomRepository.findByIdForUpdate(request.getRoomId())
                .orElseThrow(() -> new NotFoundException("Room not found"));
        DateValidator.validateDates(request.getCheckIn(), request.getCheckOut());

        long nights = ChronoUnit.DAYS.between(request.getCheckIn(), request.getCheckOut());
        BigDecimal totalAmount = room.getPricePerNight().multiply(BigDecimal.valueOf(nights));
        long overlappingBookings = bookingRepository.countOverlapping(
                room.getId(), request.getCheckIn(), request.getCheckOut());

        if (overlappingBookings >= room.getTotalRooms()) {
            throw new SoldOutException("No rooms available for the selected dates");
        }

        Booking booking = new Booking();
        booking.setBookingRef(createBookingRef());
        booking.setRoom(room);
        booking.setGuestName(request.getGuestName().trim());
        booking.setGuestEmail(request.getGuestEmail().trim().toLowerCase(Locale.ROOT));
        booking.setCheckIn(request.getCheckIn());
        booking.setCheckOut(request.getCheckOut());
        booking.setNights(Math.toIntExact(nights));
        booking.setTotalAmount(totalAmount);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setCreatedAt(LocalDateTime.now());

        return toResponse(bookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    public BookingResponse getBooking(String reference, String email) {
        return toResponse(findBooking(reference, email));
    }

    @Transactional
    public BookingResponse cancelBooking(String reference, String email) {
        Booking booking = findBooking(reference, email);

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException("This booking is already cancelled");
        }
        if (booking.getCheckIn().isBefore(LocalDate.now())) {
            throw new BadRequestException(
                    "Cannot cancel a booking whose check-in date has already passed");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        return toResponse(bookingRepository.save(booking));
    }

    private Booking findBooking(String reference, String email) {
        String cleanReference = reference.trim().toUpperCase(Locale.ROOT);
        String cleanEmail = email.trim().toLowerCase(Locale.ROOT);
        return bookingRepository.findByBookingRefAndGuestEmail(cleanReference, cleanEmail)
                .orElseThrow(() -> new NotFoundException(BOOKING_NOT_FOUND_MESSAGE));
    }

    private BookingResponse toResponse(Booking booking) {
        Room room = booking.getRoom();
        return new BookingResponse(
                booking.getBookingRef(),
                room.getHotel().getName(),
                room.getHotel().getLocation(),
                room.getRoomType(),
                booking.getGuestName(),
                booking.getGuestEmail(),
                booking.getCheckIn(),
                booking.getCheckOut(),
                booking.getNights(),
                room.getPricePerNight(),
                booking.getTotalAmount(),
                booking.getStatus(),
                booking.getCreatedAt());
    }

    private String createBookingRef() {
        String bookingRef;
        do {
            bookingRef = "HB-" + UUID.randomUUID().toString()
                    .replace("-", "")
                    .substring(0, 8)
                    .toUpperCase(Locale.ROOT);
        } while (bookingRepository.existsByBookingRef(bookingRef));
        return bookingRef;
    }
}
