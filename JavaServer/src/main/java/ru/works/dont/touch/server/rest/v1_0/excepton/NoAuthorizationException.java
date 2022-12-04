package ru.works.dont.touch.server.rest.v1_0.excepton;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "Wrong username or password")
public class NoAuthorizationException extends RuntimeException {
}
