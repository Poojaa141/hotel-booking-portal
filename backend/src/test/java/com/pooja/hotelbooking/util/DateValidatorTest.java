package com.pooja.hotelbooking.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.pooja.hotelbooking.exception.BadRequestException;

class DateValidatorTest {

    @Test
    void validDates_pass() {
        assertDoesNotThrow(() -> DateValidator.validateDates(
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(3)));
    }

    @Test
    void checkOutEqualToCheckIn_throwsBadRequest() {
        LocalDate checkIn = LocalDate.now().plusDays(1);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> DateValidator.validateDates(checkIn, checkIn));

        assertEquals("Check-out date must be after check-in date", exception.getMessage());
    }

    @Test
    void checkOutBeforeCheckIn_throwsBadRequest() {
        LocalDate checkIn = LocalDate.now().plusDays(2);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> DateValidator.validateDates(checkIn, checkIn.minusDays(1)));

        assertEquals("Check-out date must be after check-in date", exception.getMessage());
    }

    @Test
    void checkInInPast_throwsBadRequest() {
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> DateValidator.validateDates(LocalDate.now().minusDays(1), LocalDate.now().plusDays(1)));

        assertEquals("Check-in date cannot be in the past", exception.getMessage());
    }

    @Test
    void checkInToday_isAllowed() {
        assertDoesNotThrow(() -> DateValidator.validateDates(LocalDate.now(), LocalDate.now().plusDays(1)));
    }
}
