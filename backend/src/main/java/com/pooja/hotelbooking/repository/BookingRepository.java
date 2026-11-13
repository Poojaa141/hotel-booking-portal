package com.pooja.hotelbooking.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pooja.hotelbooking.entity.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    boolean existsByBookingRef(String bookingRef);

    Optional<Booking> findByBookingRefAndGuestEmail(String bookingRef, String guestEmail);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.room.id = :roomId AND b.status = com.pooja.hotelbooking.entity.BookingStatus.CONFIRMED AND b.checkIn < :checkOut AND b.checkOut > :checkIn")
    long countOverlapping(@Param("roomId") Long roomId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut);
}
