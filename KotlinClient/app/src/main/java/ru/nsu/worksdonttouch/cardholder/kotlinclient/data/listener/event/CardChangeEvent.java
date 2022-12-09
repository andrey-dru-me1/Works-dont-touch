package ru.nsu.worksdonttouch.cardholder.kotlinclient.data.listener.event;

import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.card.Card;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.listener.Event;

public class CardChangeEvent implements Event {

    private final Card card;

    public CardChangeEvent(Card card) {
        this.card = card;
    }

    public Card getCard() {
        return card;
    }
}
