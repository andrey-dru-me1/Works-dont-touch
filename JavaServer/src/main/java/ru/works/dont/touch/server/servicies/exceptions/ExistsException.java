package ru.works.dont.touch.server.servicies.exceptions;

public class ExistsException extends DataBaseException {
    public ExistsException(String message, Throwable err) {
        super(message, err);
    }

    public ExistsException(String message) {
        super(message);
    }
    public ExistsException(){
        super("Something is already exists");
    }
}
