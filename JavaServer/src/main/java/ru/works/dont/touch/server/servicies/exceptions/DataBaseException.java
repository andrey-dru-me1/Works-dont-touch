package ru.works.dont.touch.server.servicies.exceptions;

public class DataBaseException extends Exception{
    public DataBaseException(String message, Throwable err){
        super(message, err);
    }
    public DataBaseException(String message){
        super(message);
    }
}
