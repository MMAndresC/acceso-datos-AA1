package com.svalero.tournament.exception;

public class CasterNotFoundException extends Exception{
    public CasterNotFoundException() {
        super("Caster not found");
    }

    public CasterNotFoundException(String message) {
        super(message);
    }
}
