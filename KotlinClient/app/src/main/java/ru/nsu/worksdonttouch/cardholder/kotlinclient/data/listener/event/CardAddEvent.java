package ru.nsu.worksdonttouch.cardholder.kotlinclient.data.listener.event;

import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.data.card.Card;

public class CardAddEvent {

    private Card card;

    public CardAddEvent(Card card) {
        this.card = card;
    }

    public Card getCard() {
        return card;
    }
}
