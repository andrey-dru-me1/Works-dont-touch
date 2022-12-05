package ru.nsu.worksdonttouch.cardholder.kotlinclient.data.update;

import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.Card;

public class ReplaceCardUpdate implements Update {

    private final Card newCard;

    private final Card oldCard;

    public ReplaceCardUpdate(Card oldCard, Card newCard) {
        this.newCard = newCard;
        this.oldCard = oldCard;
    }

    public Card getOldCard() {
        return oldCard;
    }

    public Card getNewCard() {
        return newCard;
    }

    @Override
    public UpdateType getType() {
        return UpdateType.REPLACE_CARD;
    }

}
