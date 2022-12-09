package ru.nsu.worksdonttouch.cardholder.kotlinclient.ui.objects;

import java.io.File;

import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.card.Card;

public class CardWithImage {

    public Card card;
    public File image;

    public CardWithImage(Card card, File image) {
        this.card = card;
        this.image = image;
    }

}
