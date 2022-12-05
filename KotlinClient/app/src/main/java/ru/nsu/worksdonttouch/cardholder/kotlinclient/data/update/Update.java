package ru.nsu.worksdonttouch.cardholder.kotlinclient.data.update;

import org.jetbrains.annotations.NotNull;

public interface Update {

    @NotNull
    UpdateType getType();

}
