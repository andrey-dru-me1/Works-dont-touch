package ru.nsu.worksdonttouch.cardholder.kotlinclient.data.data.card;

public class SortedCard {

    private final Card card;
    private final long distance;

    public SortedCard(Card card, long distance) {
        this.card = card;
        this.distance = distance;
    }

    public Card getCard() {
        return card;
    }

    public long getDistance() {
        return distance;
    }
}
