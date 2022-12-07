package ru.nsu.worksdonttouch.cardholder.kotlinclient.net.objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ImageAnswer {

    private long id;
    private long cardId;

    @JsonCreator
    public ImageAnswer (@JsonProperty("id") long id, @JsonProperty("cardId") Long cardId) {
        this.id = id;
        this.cardId = cardId;
    }

    public long getId() {
        return id;
    }

    public long getCardId() {
        return cardId;
    }
}
