package com.svalero.tournament.exception;

public class UserAlreadyExistException extends Exception{
    public UserAlreadyExistException() {
        super("Already exist user");
    }

    public UserAlreadyExistException(String message) {
        super(message);
    }
}
