package com.svalero.AA1Tournament.exception;


public class TeamNotFoundException extends Exception{
    public TeamNotFoundException() {
        super("Team not found");
    }

    public TeamNotFoundException(String message) {
        super(message);
    }

}
