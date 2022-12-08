package ru.nsu.worksdonttouch.cardholder.kotlinclient.net.objects;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SimpleHttpResult {

    private String code;
    private String reason;

    @JsonCreator
    public SimpleHttpResult(@JsonProperty("code") String code, @JsonProperty("reason") String reason) {
        this.code = code;
        this.reason = reason;
    }

    public String getCode() {
        return code;
    }

    public String getReason() {
        return reason;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
