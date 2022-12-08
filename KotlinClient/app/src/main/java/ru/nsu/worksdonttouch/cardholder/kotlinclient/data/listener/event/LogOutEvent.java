package ru.nsu.worksdonttouch.cardholder.kotlinclient.data.listener.event;

import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.data.UserData;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.listener.Event;

public class LogOutEvent implements Event {

    private final UserData userData;

    public LogOutEvent(UserData userData) {
        this.userData = userData;
    }

    public UserData getUserData() {
        return userData;
    }
}
