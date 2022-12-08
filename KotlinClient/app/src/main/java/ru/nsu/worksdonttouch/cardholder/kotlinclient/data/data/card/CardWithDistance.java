package ru.nsu.worksdonttouch.cardholder.kotlinclient.data.data.card;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CardWithDistance {

    private final Long id;

    private final Long distance;

    @JsonCreator
    public CardWithDistance(@JsonProperty("nearest") Long id, @JsonProperty("other") Long distance) {
        this.id = id;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public Long getDistance() {
        return distance;
    }
}
