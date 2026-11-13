package com.pooja.hotelbooking.util;

import java.time.LocalDate;

import com.pooja.hotelbooking.exception.BadRequestException;

public final class DateValidator {

    private DateValidator() {
    }

    public static void validateDates(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            return;
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new BadRequestException("Check-out date must be after check-in date");
        }
        if (checkIn.isBefore(LocalDate.now())) {
            throw new BadRequestException("Check-in date cannot be in the past");
        }
    }
}
