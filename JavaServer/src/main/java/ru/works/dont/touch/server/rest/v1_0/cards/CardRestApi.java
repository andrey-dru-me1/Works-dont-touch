package ru.works.dont.touch.server.rest.v1_0.cards;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.works.dont.touch.server.entities.Card;
import ru.works.dont.touch.server.entities.User;
import ru.works.dont.touch.server.exceptions.ExistsException;
import ru.works.dont.touch.server.rest.v1_0.auth.AuthorizationService;
import ru.works.dont.touch.server.rest.v1_0.cards.exception.CardCreateException;
import ru.works.dont.touch.server.rest.v1_0.cards.map.MapService;
import ru.works.dont.touch.server.rest.v1_0.excepton.AlreadyExistsException;
import ru.works.dont.touch.server.rest.v1_0.objects.card.CardList;
import ru.works.dont.touch.server.rest.v1_0.objects.card.ReceivedCard;
import ru.works.dont.touch.server.rest.v1_0.objects.card.ReturnedCard;
import ru.works.dont.touch.server.rest.v1_0.excepton.NoAuthorizationException;
import ru.works.dont.touch.server.rest.v1_0.excepton.WrongDataException;
import ru.works.dont.touch.server.servicies.CardService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path="/v1.0/cards")
public class CardRestApi {

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private CardService cardService;

    @RequestMapping(path = "/get",
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.GET)
    public CardList getCards(
            @RequestHeader(value = "Authorization", required = true) String authorization,
            @RequestParam(value = "latitude", required = true) double latitude,
            @RequestParam(value = "longitude", required = true) double longitude) {
        Optional<User> optionalUser = authorizationService.authorization(authorization);
        if(optionalUser.isEmpty()) {
            throw new NoAuthorizationException();
        }
        User user = optionalUser.get();
        MapService mapService = MapService.getInstance();
        Iterable<Card> cards = cardService.getCardsByUserId(user.getId());
        if (mapService != null) {
            return mapService.sortCards(cards, latitude, longitude);
        } else {
            List<Long> ids = new ArrayList<>();
            for(Card card : cards) {
                ids.add(card.getId());
            }
            return new CardList(List.of(), ids);
        }
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
        Card card = null;
        try {
            card = cardService.saveCard(receivedCard.name(), receivedCard.barcode(), user.getId());
        } catch (ExistsException e) {
            throw new AlreadyExistsException();
        }
        if(card == null)
            throw new CardCreateException();
        return new ReturnedCard(card.getId(), card.getName(), card.getBarcode(), List.of(), List.of());
    }

}
