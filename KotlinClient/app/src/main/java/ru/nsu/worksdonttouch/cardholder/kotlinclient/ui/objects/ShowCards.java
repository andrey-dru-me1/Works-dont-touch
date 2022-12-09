package ru.nsu.worksdonttouch.cardholder.kotlinclient.ui.objects;

import java.util.List;
import java.util.stream.Collectors;

import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.card.Cards;

public class ShowCards {

    private final List<SortedCardWithImage> sortedCards;
    private final List<CardWithImage> other;

    public ShowCards(List<SortedCardWithImage> sortedCards1, List<CardWithImage> other1) {
        this.sortedCards = sortedCards1;
        this.other = other1;
    }

    public ShowCards(Cards cards) {
        this.sortedCards = cards.getSortedCards().stream().map(a -> new SortedCardWithImage(a.getCard(), a.getDistance(), null)).collect(Collectors.toList());
        this.other = cards.getSortedCards().stream().map(a -> new CardWithImage(a.getCard(), null)).collect(Collectors.toList());
    }

    public List<SortedCardWithImage> getSortedCards() {
        return sortedCards;
    }

    public List<CardWithImage> getOther() {
        return other;
    }
}
