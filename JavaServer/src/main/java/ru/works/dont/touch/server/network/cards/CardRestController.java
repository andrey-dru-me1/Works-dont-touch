package ru.works.dont.touch.server.network.cards;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.ServerRequest;

@RestController("/cards")
public class CardRestController {

    private static Logger logger = LoggerFactory.getLogger(CardRestController.class);

    @RequestMapping(
            value = "/getList",
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = {RequestMethod.GET, RequestMethod.POST})
    public void getSortedCards(
            @RequestHeader(value = "Authorization", required = true) ServerRequest.Headers headers) {

    }

    @RequestMapping(
            value = "/get",
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = {RequestMethod.GET})
    public void getCards(
            @RequestHeader(value = "Authorization", required = true) ServerRequest.Headers headers,
            @RequestParam(value = "id", required = true) long id) {

    }

    @RequestMapping(
            value = "/add",
            produces = MediaType.APPLICATION_JSON_VALUE,
            params = MediaType.APPLICATION_JSON_VALUE,
            method = {RequestMethod.POST})
    public void addCards(
            @RequestHeader(value = "Authorization", required = true) ServerRequest.Headers headers,
            @RequestParam(required = true) CardChanger card) {

    }

    @RequestMapping(
            value = "/edit",
            produces = MediaType.APPLICATION_JSON_VALUE,
            params = MediaType.APPLICATION_JSON_VALUE,
            method = {RequestMethod.POST})
    public void addCards(
            @RequestHeader(value = "Authorization", required = true) ServerRequest.Headers headers,
            @RequestParam(required = true) Card card) {

    }

    @RequestMapping(
            value = "/remove",
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = {RequestMethod.POST})
    public void addCards(
            @RequestHeader(value = "Authorization", required = true) ServerRequest.Headers headers,
            @RequestParam(value = "card", required = true) long id) {

    }

}
