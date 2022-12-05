package ru.works.dont.touch.server.rest.v1_0.objects.card;

import ru.works.dont.touch.server.entities.Card;
import ru.works.dont.touch.server.rest.v1_0.objects.loaction.Location;

import java.util.ArrayList;
import java.util.List;

public record ReturnedCard(long id, String name, String barcode, List<Long> images, List<Location> locations) {

    public ReturnedCard(Card card) {
        this(card.getId(), card.getName(), card.getBarcode(), new ArrayList<>(), new ArrayList<>());
    }

}
