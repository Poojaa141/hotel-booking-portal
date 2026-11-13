package com.pooja.hotelbooking.service;

import java.math.BigDecimal;
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
import com.pooja.hotelbooking.exception.NotFoundException;
import com.pooja.hotelbooking.exception.SoldOutException;
import com.pooja.hotelbooking.repository.BookingRepository;
import com.pooja.hotelbooking.repository.RoomRepository;
import com.pooja.hotelbooking.util.DateValidator;

@Service
public class BookingService {

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

        Booking savedBooking = bookingRepository.save(booking);
        return new BookingResponse(
                savedBooking.getBookingRef(),
                room.getHotel().getName(),
                room.getHotel().getLocation(),
                room.getRoomType(),
                savedBooking.getGuestName(),
                savedBooking.getGuestEmail(),
                savedBooking.getCheckIn(),
                savedBooking.getCheckOut(),
                savedBooking.getNights(),
                room.getPricePerNight(),
                savedBooking.getTotalAmount(),
                savedBooking.getStatus(),
                savedBooking.getCreatedAt());
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
