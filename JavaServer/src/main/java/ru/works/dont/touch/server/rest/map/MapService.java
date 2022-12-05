package ru.works.dont.touch.server.rest.map;

import ru.works.dont.touch.server.entities.Card;
import ru.works.dont.touch.server.rest.v1_0.cards.object.card.CardList;

public interface MapService {

    public abstract CardList sortCards(Iterable<Card> cards , double latitude, double longitude);

}
