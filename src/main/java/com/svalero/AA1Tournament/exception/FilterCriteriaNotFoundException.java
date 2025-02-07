package com.svalero.AA1Tournament.exception;

public class FilterCriteriaNotFoundException extends Exception{
    public FilterCriteriaNotFoundException() {
        super("Filter criteria not found");
    }

    public FilterCriteriaNotFoundException(String message) {
        super(message);
    }
}
