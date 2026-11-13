package com.pooja.hotelbooking.exception;

public class SoldOutException extends RuntimeException {

    public SoldOutException(String message) {
        super(message);
    }
}
