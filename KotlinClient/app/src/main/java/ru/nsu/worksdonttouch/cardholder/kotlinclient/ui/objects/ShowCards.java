package ru.nsu.worksdonttouch.cardholder.kotlinclient.ui.objects;

import java.io.File;
import java.util.List;

import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.card.Card;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.card.SortedCard;

public class ShowCards {

    private final List<SortedCard> sortedCards;
    private final List<Card> other;
    private final List<File> sortedCardsImages;
    private final List<File> otherImages;

    public ShowCards(List<SortedCard> sortedCards, List<Card> other, List<File> sortedCardsImages, List<File> otherImages) {
        this.sortedCards = sortedCards;
        this.other = other;
        this.sortedCardsImages = sortedCardsImages;
        this.otherImages = otherImages;
    }

    public List<SortedCard> getSortedCards() {
        return sortedCards;
    }

    public List<Card> getOther() {
        return other;
    }

    public List<File> getSortedImageFiles() {
        return sortedCardsImages;
    }

    public List<File> getOtherImageFiles() {
        return otherImages;
    }

}
