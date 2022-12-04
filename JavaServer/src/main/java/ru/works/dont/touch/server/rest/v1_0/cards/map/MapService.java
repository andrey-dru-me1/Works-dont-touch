package ru.works.dont.touch.server.rest.v1_0.cards.map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.works.dont.touch.server.entities.Card;
import ru.works.dont.touch.server.rest.v1_0.objects.card.CardList;

@Service
public interface MapService {

    @Autowired
    TwoGisService TWO_GIS_SERVICE = null;

    static MapService getInstance() {
        return TWO_GIS_SERVICE;
    }

    CardList sortCards(Iterable<Card> cards , double latitude, double longitude);

}
