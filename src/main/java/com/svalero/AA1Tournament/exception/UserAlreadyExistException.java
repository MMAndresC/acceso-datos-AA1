package com.svalero.AA1Tournament.exception;

public class UserAlreadyExistException extends Exception{
    public UserAlreadyExistException() {
        super("Already exist user");
    }

    public UserAlreadyExistException(String message) {
        super(message);
    }
}
