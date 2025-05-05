package com.svalero.tournament.exception;

public class PasswordIncorrectException extends Exception{
    public PasswordIncorrectException() {
        super("Incorrect password");
    }

    public PasswordIncorrectException(String message) {
        super(message);
    }
}
