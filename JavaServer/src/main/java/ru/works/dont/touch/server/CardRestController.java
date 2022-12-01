package ru.works.dont.touch.server;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.works.dont.touch.server.data.Card;

@RestController
public class CardRestController {

    @RequestMapping(value = "/cards/get", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Card[] getCardsMethod(String name) {
        Card[] cards = new Card[2];
        cards[0] = new Card(0, "shop3", name, "112312141234123", new Long[]{1L, 2L});
        cards[1] = new Card(1, "shop4", "secondCard", "235234236234", new Long[]{3L, 4L});
        return cards;
    }

}
