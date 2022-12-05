package ru.nsu.worksdonttouch.cardholder.kotlinclient.data.update;

import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.Card;

public class AddCardUpdate implements Update {

    private final Card card;

    public AddCardUpdate(Card card) {
        this.card = card;
    }

    public Card getCard() {
        return card;
    }

    @Override
    public UpdateType getType() {
        return UpdateType.ADD_CARD;
    }
}
