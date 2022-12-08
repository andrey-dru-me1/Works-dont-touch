package ru.nsu.worksdonttouch.cardholder.kotlinclient.net;

import okhttp3.Response;

import java.io.IOException;

public interface HttpCallback<T> {

    public void answer(HttpResult result, T data);

    public enum HttpResult {
        SUCCESSFUL, FAIL, AUTHORIZATION_ERROR, NOT_FOUND, NO_PERMISSION, WRONG_REQUEST, NO_CONNECTION, OTHER;

        public static HttpResult errorHandler(Response response) {
            if (response == null) {
                return NO_CONNECTION;
            }
            if (response.code() / 100 == 5) {
                return FAIL;
            }
            if (response.code() == 401) {
                return AUTHORIZATION_ERROR;
            }
            if (response.code() == 404) {
                return NOT_FOUND;
            }
            if (response.code() == 400) {
                return WRONG_REQUEST;
            }
            if (response.code() == 403) {
                return NO_PERMISSION;
            }
            if (response.code() == 200) {
                return SUCCESSFUL;
            }
            if (response.body() == null) {
                return FAIL;
            }
            return OTHER;
        }
    }

}
