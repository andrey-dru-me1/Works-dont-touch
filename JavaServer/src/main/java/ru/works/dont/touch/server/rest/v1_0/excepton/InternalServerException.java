package ru.works.dont.touch.server.rest.v1_0.excepton;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Incorrect server behavior")
public class InternalServerException extends RuntimeException {
}
