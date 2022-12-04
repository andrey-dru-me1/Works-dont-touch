package ru.works.dont.touch.server.rest.v1_0.cards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Can't create card")
public class CardCreateException extends RuntimeException {
}
