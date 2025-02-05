package com.svalero.AA1Tournament.exception;

public class PlayerNotFoundException extends Exception{
    public PlayerNotFoundException() {
        super("Player not found");
    }

    public PlayerNotFoundException(String message) {
        super(message);
    }
}
