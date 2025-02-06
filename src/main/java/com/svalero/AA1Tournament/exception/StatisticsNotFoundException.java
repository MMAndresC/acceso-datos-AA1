package com.svalero.AA1Tournament.exception;

public class StatisticsNotFoundException extends Exception{
    public StatisticsNotFoundException() {
        super("Statistics not found");
    }

    public StatisticsNotFoundException(String message) {
        super(message);
    }
}
