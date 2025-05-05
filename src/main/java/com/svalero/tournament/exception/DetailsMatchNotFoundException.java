package com.svalero.tournament.exception;

public class DetailsMatchNotFoundException extends Exception{
    public DetailsMatchNotFoundException() {
        super("Details match not found");
    }

    public DetailsMatchNotFoundException(String message) {
        super(message);
    }
}
