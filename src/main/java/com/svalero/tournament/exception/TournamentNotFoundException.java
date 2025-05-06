package com.svalero.tournament.exception;

public class TournamentNotFoundException extends Exception{
    public TournamentNotFoundException() {
        super("Tournament not found");
    }

    public TournamentNotFoundException(String message) {
        super(message);
    }
}
