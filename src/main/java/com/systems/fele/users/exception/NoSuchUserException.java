package com.systems.fele.users.exception;

public class NoSuchUserException extends RuntimeException {
    public NoSuchUserException(String userInfo) {
        super(String.format("No such user: %s", userInfo));
    }
}
