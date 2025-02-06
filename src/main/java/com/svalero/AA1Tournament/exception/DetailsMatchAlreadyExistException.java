package com.svalero.AA1Tournament.exception;

public class DetailsMatchAlreadyExistException extends Exception{
    public DetailsMatchAlreadyExistException() {
        super("Already exist detail match for this team");
    }

    public DetailsMatchAlreadyExistException(String message) {
        super(message);
    }
}
