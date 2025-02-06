package com.svalero.AA1Tournament.exception;

public class DetailsMatchNotFoundException extends Exception{
    public DetailsMatchNotFoundException() {
        super("Details match not found");
    }

    public DetailsMatchNotFoundException(String message) {
        super(message);
    }
}
