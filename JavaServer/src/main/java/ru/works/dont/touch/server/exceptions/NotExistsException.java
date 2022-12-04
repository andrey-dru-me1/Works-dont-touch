package ru.works.dont.touch.server.exceptions;

public class NotExistsException extends DataBaseException {
    public NotExistsException(String message, Throwable err) {
        super(message, err);
    }

    public NotExistsException(String message) {
        super(message);
    }

    public NotExistsException() {
        super("Not exists");
    }

}

