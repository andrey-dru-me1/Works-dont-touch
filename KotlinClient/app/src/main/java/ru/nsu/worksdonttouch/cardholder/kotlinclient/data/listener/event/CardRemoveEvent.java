package ru.nsu.worksdonttouch.cardholder.kotlinclient.data.listener.event;

import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.data.card.Card;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.listener.Event;

public class CardRemoveEvent implements Event {

    private final Card card;

    public CardRemoveEvent(Card card) {
        this.card = card;
    }

    public Card getCard() {
        return card;
    }
}
