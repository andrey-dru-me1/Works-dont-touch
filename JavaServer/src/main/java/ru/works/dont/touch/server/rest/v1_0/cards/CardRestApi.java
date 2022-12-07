package ru.works.dont.touch.server.rest.v1_0.cards;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.works.dont.touch.server.entities.*;
import ru.works.dont.touch.server.exceptions.ExistsException;
import ru.works.dont.touch.server.exceptions.NotExistsException;
import ru.works.dont.touch.server.rest.v1_0.auth.AuthorizationService;
import ru.works.dont.touch.server.rest.v1_0.cards.exception.CardCreateException;
import ru.works.dont.touch.server.rest.v1_0.cards.exception.UnknownCardException;
import ru.works.dont.touch.server.rest.map.MapService;
import ru.works.dont.touch.server.rest.v1_0.excepton.AlreadyExistsException;
import ru.works.dont.touch.server.rest.v1_0.cards.object.card.CardEditor;
import ru.works.dont.touch.server.rest.v1_0.cards.object.card.CardList;
import ru.works.dont.touch.server.rest.v1_0.cards.object.card.ReceivedCard;
import ru.works.dont.touch.server.rest.v1_0.cards.object.card.ReturnedCard;
import ru.works.dont.touch.server.rest.v1_0.excepton.NoAuthorizationException;
import ru.works.dont.touch.server.rest.v1_0.excepton.WrongDataException;
import ru.works.dont.touch.server.servicies.CardService;
import ru.works.dont.touch.server.servicies.CoordinateService;
import ru.works.dont.touch.server.servicies.ImageService;
import ru.works.dont.touch.server.servicies.LocationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping(path="/v1.0/cards")
public class CardRestApi {

    private final Logger logger = LoggerFactory.getLogger(CardRestApi.class);

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private CardService cardService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private CoordinateService coordinateService;

    @Autowired
    private MapService mapService;

    @RequestMapping(path = "/getList",
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.GET)
    public CardList getCards(
            @RequestHeader(value = "Authorization", required = true) String authorization,
            @RequestParam(value = "latitude", required = false) Double latitude,
            @RequestParam(value = "longitude", required = false) Double longitude) {
        Optional<User> optionalUser = authorizationService.authorization(authorization);
        if(optionalUser.isEmpty()) {
            throw new NoAuthorizationException();
        }
        User user = optionalUser.get();
        Iterable<Card> cards = cardService.getCardsByUserId(user.getId());
        logger.info(mapService + "," + latitude + ","+longitude);
        if (mapService != null && latitude != null && longitude != null) {
            return mapService.sortCards(cards, latitude, longitude);
        } else {
            List<Long> ids = new ArrayList<>();
            for(Card card : cards) {
                ids.add(card.getId());
            }
            return new CardList(List.of(), ids);
        }
    }

    @RequestMapping(path = "/get",
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.GET)
    public ReturnedCard getCards(
            @RequestHeader(value = "Authorization", required = true) String authorization,
            @RequestParam(value = "id", required = true) long cardId) {
        Optional<User> optionalUser = authorizationService.authorization(authorization);
        if(optionalUser.isEmpty()) {
            throw new NoAuthorizationException();
        }
        User user = optionalUser.get();
        Card card;
        try {
            card = cardService.getCardById(cardId);
            if (card == null || !Objects.equals(card.getOwnerId(), user.getId()))
                throw new UnknownCardException();
        } catch (NotExistsException e) {
            throw new UnknownCardException();
        }
        ReturnedCard returnedCard = new ReturnedCard(card.getId(), card.getName(), card.getBarcode(), new ArrayList<>(), new ArrayList<>());
        Iterable<Location> locations = locationService.findAllByCardId(card.getId());
        for(Location loc : locations) {
            var location = new ru.works.dont.touch.server.rest.v1_0.cards.object.location.Location(loc.getName(), loc.getCustom(), new ArrayList<>());
            Iterable<Coordinate> coordinates = coordinateService.findByLocationId(loc.getId());
            for(Coordinate coordinate : coordinates) {
                location.coordinates().add(new ru.works.dont.touch.server.rest.v1_0.cards.object.coordinate.Coordinate(coordinate.getLatitude(), coordinate.getLongitude()));
            }
            returnedCard.locations().add(location);
        }
        Iterable<Image> images = imageService.findAllByCardId(card.getId());
        for(Image image : images) {
            returnedCard.images().add(image.getId());
        }
        return returnedCard;
    }

    @RequestMapping(path = "/add",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            method = {RequestMethod.POST})
    public ReturnedCard addCards(
            @RequestHeader(value = "Authorization", required = true) String authorization,
            @RequestBody(required = true) ReceivedCard receivedCard){
        if(receivedCard.name() == null)
            throw new WrongDataException();
        Optional<User> optionalUser = authorizationService.authorization(authorization);
        if(optionalUser.isEmpty()) {
            throw new NoAuthorizationException();
        }
        User user = optionalUser.get();
        Card card;
        try {
            card = cardService.saveCard(receivedCard.name(), receivedCard.barcode(), user.getId());
        } catch (ExistsException e) {
            throw new AlreadyExistsException();
        }
        if(card == null)
            throw new CardCreateException();
        return new ReturnedCard(card.getId(), card.getName(), card.getBarcode(), List.of(), List.of());
    }

    @RequestMapping(path = "/edit",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            method = {RequestMethod.POST})
    public ReturnedCard editCards(
            @RequestHeader(value = "Authorization", required = true) String authorization,
            @RequestBody(required = true) CardEditor cardEditor)  {
        if(cardEditor.id() == null)
            throw new WrongDataException();
        Optional<User> optionalUser = authorizationService.authorization(authorization);
        if(optionalUser.isEmpty()) {
            throw new NoAuthorizationException();
        }
        User user = optionalUser.get();
        Card card;
        try {
            card = cardService.getCardById(cardEditor.id());
            if (card == null || !Objects.equals(card.getOwnerId(), user.getId()))
                throw new UnknownCardException();
        } catch (NotExistsException e) {
            throw new UnknownCardException();
        }
        if (cardEditor.barcode() != null)
            card.setBarcode(card.getBarcode());
        if (cardEditor.name() != null)
            card.setName(cardEditor.name());
        ReturnedCard returnedCard = new ReturnedCard(card);
        if (cardEditor.images() != null) {
            Iterable<Image> images = imageService.findAllImageByCardId(card.getId());
            for (Image image : images) {
                if (!cardEditor.images().contains(image.getId())) {
                    try {
                        imageService.deleteById(image.getId());
                    } catch (NotExistsException e) {
                        logger.warn("Strange behavior", e);
                    }
                } else {
                    returnedCard.images().add(image.getId());
                }
            }
        } else {
            Iterable<Image> images = imageService.findAllByCardId(card.getId());
            for (Image image : images) {
                returnedCard.images().add(image.getId());
            }
        }
        if (cardEditor.locations() != null) {
            locationService.deleteByCardId(card.getId());
            coordinateService.deleteByCardId(card.getId());
            for (ru.works.dont.touch.server.rest.v1_0.cards.object.location.Location loc : cardEditor.locations()) {
                try {
                    Location createdLocation = locationService.save(loc.isCustom(), loc.name(), card.getId());
                    if (createdLocation != null) {
                        var returnedLocation = new ru.works.dont.touch.server.rest.v1_0.cards.object.location.Location(createdLocation.getName(), createdLocation.getCustom(), new ArrayList<>());
                        for (ru.works.dont.touch.server.rest.v1_0.cards.object.coordinate.Coordinate coordinate : loc.coordinates()) {
                            Coordinate createdCoordinate = coordinateService.save(createdLocation.getId(), coordinate.latitude(), coordinate.longitude());
                            if (createdCoordinate != null)
                                returnedLocation.coordinates().add(new ru.works.dont.touch.server.rest.v1_0.cards.object.coordinate.Coordinate(createdCoordinate.getLatitude(), createdCoordinate.getLongitude()));
                        }
                        returnedCard.locations().add(returnedLocation);
                    }
                } catch (ExistsException e) {
                    logger.warn("Card location edit error", e);
                }
            }
        } else {
            Iterable<Location> locations = locationService.findAllByCardId(card.getId());
            for(Location loc : locations) {
                var returnedLocation = new ru.works.dont.touch.server.rest.v1_0.cards.object.location.Location(loc.getName(), loc.getCustom(), new ArrayList<>());
                Iterable<Coordinate> coordinates = coordinateService.findByLocationId(loc.getId());
                for(Coordinate coordinate : coordinates)
                    returnedLocation.coordinates().add(new ru.works.dont.touch.server.rest.v1_0.cards.object.coordinate.Coordinate(coordinate.getLatitude(), coordinate.getLongitude()));
                returnedCard.locations().add(returnedLocation);
            }
        }
        return returnedCard;
    }



}
