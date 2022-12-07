package ru.nsu.worksdonttouch.cardholder.kotlinclient.data.data.card;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CardList {

    private final List<CardWithDistance> nearest;

    private final List<Long> other;

    @JsonCreator
    public CardList(@JsonProperty("nearest")List<CardWithDistance> nearest, @JsonProperty("other")List<Long> other) {
        this.nearest = nearest;
        this.other = other;
    }

    public List<CardWithDistance> getNearest() {
        return nearest;
    }

    public List<Long> getOther() {
        return other;
    }
}
