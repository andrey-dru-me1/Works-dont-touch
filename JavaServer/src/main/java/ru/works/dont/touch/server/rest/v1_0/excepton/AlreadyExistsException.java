package ru.works.dont.touch.server.rest.v1_0.excepton;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Already exists")
public class AlreadyExistsException extends RuntimeException {
}
