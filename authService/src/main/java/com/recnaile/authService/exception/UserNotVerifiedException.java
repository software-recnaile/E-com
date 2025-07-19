package com.recnaile.authService.exception;

public class UserNotVerifiedException extends RuntimeException {
    public UserNotVerifiedException(String message) {
        super(message);
    }
}
