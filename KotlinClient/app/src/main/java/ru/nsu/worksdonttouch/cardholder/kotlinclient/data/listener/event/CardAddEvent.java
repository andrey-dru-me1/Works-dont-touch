package ru.nsu.worksdonttouch.cardholder.kotlinclient.data.listener.event;

import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.data.card.Card;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.listener.Event;

public class CardAddEvent implements Event {

    private Card card;

    public CardAddEvent(Card card) {
        this.card = card;
    }

    public Card getCard() {
        return card;
    }
}
