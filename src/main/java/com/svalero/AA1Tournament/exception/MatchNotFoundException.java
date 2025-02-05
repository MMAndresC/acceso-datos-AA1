package com.svalero.AA1Tournament.exception;

public class MatchNotFoundException extends Exception{
    public MatchNotFoundException() {
        super("Match not found");
    }

    public MatchNotFoundException(String message) {
        super(message);
    }
}
