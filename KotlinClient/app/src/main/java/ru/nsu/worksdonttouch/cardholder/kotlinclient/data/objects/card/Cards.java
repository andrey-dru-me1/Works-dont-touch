package ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.card;

import java.util.List;

public class Cards {

    private final List<SortedCard> sortedCards;
    private final List<Card> other;

    public Cards(List<SortedCard> sortedCards, List<Card> other) {
        this.sortedCards = sortedCards;
        this.other = other;
    }

    public List<SortedCard> getSortedCards() {
        return sortedCards;
    }

    public List<Card> getOther() {
        return other;
    }
}
