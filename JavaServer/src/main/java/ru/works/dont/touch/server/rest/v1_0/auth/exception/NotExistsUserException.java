package ru.works.dont.touch.server.rest.v1_0.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "Not exists user")
public class NotExistsUserException extends RuntimeException {
}
