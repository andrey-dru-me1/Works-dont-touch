package ru.works.dont.touch.server.rest.v1_0.cards.map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.works.dont.touch.server.entities.Card;
import ru.works.dont.touch.server.entities.Coordinate;
import ru.works.dont.touch.server.entities.Location;
import ru.works.dont.touch.server.rest.v1_0.objects.card.CardList;
import ru.works.dont.touch.server.rest.v1_0.objects.card.CardWithDistance;
import ru.works.dont.touch.server.servicies.CoordinateService;
import ru.works.dont.touch.server.servicies.LocationService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class TwoGisService implements MapService {

    @Autowired
    private LocationService locationService;

    @Autowired
    private CoordinateService coordinateService;

    private static final long masDistance = 5000;

    @Override
    public CardList sortCards(Iterable<Card> cards, double latitude, double longitude) {
        CardList list = new CardList(new ArrayList<>(), new ArrayList<>());
        for (Card card : cards) {
            long distance = getDistance(card, latitude, longitude);
            if (distance <= masDistance) {
                list.nearest().add(new CardWithDistance(card.getId(), distance));
            } else {
                list.other().add(card.getId());
            }
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
        }

        Iterable<Coordinate> coordinates = coordinateService.findAllByCardId(card.getId());
        for(Coordinate coordinate : coordinates) {
            long _distance = distance(latitude, longitude, coordinate.getLatitude(), coordinate.getLongitude());
            if(_distance < distance) {
                distance = _distance;
            }
        }
        return distance;
    }

    private long getDistance(String name, double latitude, double longitude) {
        //some request
        return 100000;
    }

    private static long distance(double lat1, double lat2, double lon1, double lon2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        long distance = (long) (R * c * 1000); // convert to meters

        return distance;
    }

}
