package ru.nsu.worksdonttouch.cardholder.kotlinclient.data;

public interface DataCallBack<T> {

    public void callback(DataStatus status, T data);

    public enum DataStatus {
        OK, NOT_SYNCHRONISED, CANCELED, WRONG_USER
    }
}
