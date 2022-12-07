package ru.works.dont.touch.server.rest.v1_0.cards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Wrong card id")
public class UnknownCardException extends RuntimeException {
}
