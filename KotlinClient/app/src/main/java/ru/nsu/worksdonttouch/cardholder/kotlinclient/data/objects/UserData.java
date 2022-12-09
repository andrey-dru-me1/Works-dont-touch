package ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.jetbrains.annotations.NotNull;

public class UserData {

    private final String login;

    private final String password;

    @JsonCreator
    public UserData(@JsonProperty("login") @NotNull String login, @JsonProperty("password") @NotNull String password) {
        this.login = login;
        this.password = password;
    }

    @NotNull
    public String getLogin() {
        return login;
    }

    @NotNull
    public String getPassword() {
        return password;
    }

}
