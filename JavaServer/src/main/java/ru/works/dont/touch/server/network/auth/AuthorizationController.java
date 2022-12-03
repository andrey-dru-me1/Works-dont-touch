package ru.works.dont.touch.server.network.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="/auth")
public class AuthorizationController {

    @Autowired
    private Authorizer authorizer;

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public void register(
            @RequestParam(value = "login", required = true) String login,
            @RequestParam(value = "password", required = true) String password) throws Exception {
        authorizer.registration(login, password);
    }

    @RequestMapping(path = "/change/password", method = RequestMethod.POST)
    public void changePassword(
            @RequestHeader(value = "Authorization", required = true) String auth,
            @RequestHeader(value = "Password", required = true) String password) throws Exception {
        authorizer.changePassword(auth.split(" "), password);
    }

    // Authorization: base64(login:password(UTF-8))
    @RequestMapping(path = "/test", method = RequestMethod.POST)
    public void test(
            @RequestHeader(value = "Authorization", required = true) String auth) throws Exception {
        authorizer.authorization(auth.split(" "));
    }

}
