package ru.works.dont.touch.server.rest.map;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.works.dont.touch.server.entities.Card;
import ru.works.dont.touch.server.entities.Coordinate;
import ru.works.dont.touch.server.entities.Location;
import ru.works.dont.touch.server.rest.v1_0.cards.object.card.CardList;
import ru.works.dont.touch.server.rest.v1_0.cards.object.card.CardWithDistance;
import ru.works.dont.touch.server.servicies.CoordinateService;
import ru.works.dont.touch.server.servicies.LocationService;

import java.util.ArrayList;

@Service
public class TwoGisService implements MapService {

    @Autowired
    private LocationService locationService;

    @Autowired
    private CoordinateService coordinateService;

    private static final long maxDistance = 5000;

    @Override
    public CardList sortCards(Iterable<Card> cards, double latitude, double longitude) {
        CardList list = new CardList(new ArrayList<>(), new ArrayList<>());
        for (Card card : cards) {
            long distance = getDistance(card, latitude, longitude);
            if (distance <= maxDistance) {
                list.nearest().add(new CardWithDistance(card.getId(), distance));
            } else {
                list.other().add(card.getId());
            }
            LoggerFactory.getLogger(TwoGisService.class).info("distance "+ card.getName() + ":"+distance);
        }

        return list;
    }

    private long getDistance(Card card, double latitude, double longitude)  {

        Iterable<Location> locations = locationService.findAllByCardId(card.getId());
        long distance = 100000;
        for (Location loc : locations) {
            if (!loc.getCustom()) {
                long _distance = getDistance(loc.getName(), latitude, longitude);
                if(_distance < distance) {
                    distance = _distance;
                }
            }
            LoggerFactory.getLogger(TwoGisService.class).info("distance "+ loc.getName() + ":" + distance + ":" +latitude + ":"+longitude);
        }

        Iterable<Coordinate> coordinates = coordinateService.findAllByCardId(card.getId());
        for(Coordinate coordinate : coordinates) {
            long _distance = distance(latitude, coordinate.getLatitude(), longitude, coordinate.getLongitude());
            if(_distance < distance) {
                distance = _distance;
            }
            LoggerFactory.getLogger(TwoGisService.class).info("distance "+ coordinate.getLatitude() + ":" + coordinate.getLongitude() + ":" + distance + ":" +latitude + ":"+longitude);
        }
        return distance;
    }

    private long getDistance(String name, double latitude, double longitude) {
        //some request
        return 100000;
    }

    private static long distance(double lat1, double lat2, double lon1, double lon2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (long) (R * c * 1000);
    }

}
