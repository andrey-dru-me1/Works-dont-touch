package ru.works.dont.touch.server.rest.v1_0;

import org.springframework.http.HttpStatus;

public record SimpleRestAnswer(HttpStatus code, String reason) {

    public static SimpleRestAnswer getOKAnswer() {
        return new SimpleRestAnswer(HttpStatus.ACCEPTED, "OK");
    }

}
