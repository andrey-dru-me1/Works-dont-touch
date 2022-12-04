package ru.works.dont.touch.server.rest.v1_0.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.works.dont.touch.server.entities.User;
import ru.works.dont.touch.server.rest.v1_0.SimpleRestAnswer;

import java.util.Optional;

@RestController
@RequestMapping(path="/v1.0/auth")
public class AuthorizationRestApi {

    @Autowired
    private AuthorizationService authorizationService;

    @PostMapping(path = "/registration",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public SimpleRestAnswer registration(
            @RequestHeader(value = "login", required = true) String login,
            @RequestHeader(value = "password", required = true) String password) {
        if(authorizationService.registration(login, password)) {
            return SimpleRestAnswer.getOKAnswer();
        }
        return new SimpleRestAnswer(HttpStatus.INTERNAL_SERVER_ERROR, "can't create new user");
    }

    @RequestMapping(path = "/test",
            method = {RequestMethod.GET, RequestMethod.POST},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public SimpleRestAnswer test(
        @RequestHeader(value = "Authorization") String authorization){
        if(authorizationService.authorization(authorization).isPresent()) {
            return SimpleRestAnswer.getOKAnswer();
        }
        return new SimpleRestAnswer(HttpStatus.UNAUTHORIZED, "Wrong password");
    }

    @RequestMapping(path = "/change/password",
            method = {RequestMethod.POST},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public SimpleRestAnswer changePassword(
            @RequestHeader(value = "Authorization") String authorization,
            @RequestHeader(value = "Password") String newPassword){
        Optional<User> user = authorizationService.authorization(authorization);
        if(user.isPresent()) {
            authorizationService.changePassword(user.get().getLogin(), newPassword);
            return SimpleRestAnswer.getOKAnswer();
        } else {
            return new SimpleRestAnswer(HttpStatus.UNAUTHORIZED, "Wrong password");
        }
    }

}
