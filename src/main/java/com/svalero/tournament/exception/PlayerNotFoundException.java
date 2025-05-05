package com.svalero.tournament.exception;

public class PlayerNotFoundException extends Exception{
    public PlayerNotFoundException() {
        super("Player not found");
    }

    public PlayerNotFoundException(String message) {
        super(message);
    }
}
