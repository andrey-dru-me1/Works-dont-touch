package ru.nsu.worksdonttouch.cardholder.kotlinclient.net;

public interface HttpCallback<T> {

    public void answer(HttpResult result, T data);

    public enum HttpResult {
        SUCCESSFUL, FAIL, AUTHORIZATION_ERROR, NOT_FOUND, NO_PERMISSION
    }

}
