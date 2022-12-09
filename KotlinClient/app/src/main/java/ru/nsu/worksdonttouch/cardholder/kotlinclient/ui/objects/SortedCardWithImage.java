package ru.nsu.worksdonttouch.cardholder.kotlinclient.ui.objects;

import java.io.File;

import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.card.Card;

public class SortedCardWithImage {

    public Card card;
    public File image;
    public long distance;

    public SortedCardWithImage(Card card, long distance, File file) {
        this.card = card;
        this.image = file;
        this.distance = distance;
    }

}
