package ru.works.dont.touch.server.rest.v1_0.images.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Wrong image id")
public class UnknownImageException extends RuntimeException {
}
