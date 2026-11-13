package com.pooja.hotelbooking.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;

import com.pooja.hotelbooking.dto.BookingRequest;
import com.pooja.hotelbooking.dto.BookingResponse;
import com.pooja.hotelbooking.dto.QuoteRequest;
import com.pooja.hotelbooking.dto.QuoteResponse;
import com.pooja.hotelbooking.service.BookingService;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/quote")
    public QuoteResponse createQuote(@Valid @RequestBody QuoteRequest request) {
        return bookingService.createQuote(request);
    }

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingRequest request) {
        BookingResponse response = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{ref}")
    public BookingResponse getBooking(@PathVariable String ref, @RequestParam String email) {
        return bookingService.getBooking(ref, email);
    }

    @PutMapping("/{ref}/cancel")
    public BookingResponse cancelBooking(@PathVariable String ref, @RequestParam String email) {
        return bookingService.cancelBooking(ref, email);
    }
}
