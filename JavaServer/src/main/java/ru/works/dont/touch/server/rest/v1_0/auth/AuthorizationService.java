package ru.works.dont.touch.server.rest.v1_0.auth;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.works.dont.touch.server.entities.User;
import ru.works.dont.touch.server.exceptions.ExistsException;
import ru.works.dont.touch.server.exceptions.NotExistsException;
import ru.works.dont.touch.server.rest.v1_0.auth.exception.AuthFormatException;
import ru.works.dont.touch.server.rest.v1_0.auth.exception.NotExistsUserException;
import ru.works.dont.touch.server.servicies.UserService;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

@Service
public class AuthorizationService {

    @Autowired
    private UserService userService;

    private final SecureRandom random = new SecureRandom();

    public Optional<User> authorization(@NotNull String authorization) {
        String[] authData = authorization.split(" ", 2);
        if(authData.length < 2) {
            throw new AuthFormatException();
        }
        return switch (authData[0]) {
            case "Basic" -> Optional.ofNullable(baseAuthorization(authData[1]));
            default -> throw new AuthFormatException();
        };
    }

    public boolean registration(@NotNull String username, @NotNull String password) {
        try {
            return userService.saveNewUser(username, hashPassword(password)) != null;
        } catch (ExistsException e) {
            return false;
        }
    }

    public boolean changePassword(@NotNull String username, @NotNull String password) {
        try {
            userService.changeByLogin(username, hashPassword(password));
            return true;
        } catch (NotExistsException e) {
            throw new NotExistsUserException();
        }
    }

    private User baseAuthorization(String base64) {
        String data;
        try {
            data = new String(Base64.getDecoder().decode(base64.getBytes(StandardCharsets.UTF_8)));
        } catch (IllegalArgumentException e) {
            throw new AuthFormatException();
        }
        String[] pair = data.split(":", 2);
        if(pair.length != 2)
            throw new AuthFormatException();
        User user;
        try {
            user = userService.getUserByLogin(pair[0]);
        } catch (NotExistsException e) {
            throw new NotExistsUserException();
        }
        if(checkPassword(pair[1].getBytes(StandardCharsets.UTF_8) ,user.getPassword())) {
            return user;
        } else {
            return null;
        }
    }

    private boolean checkPassword(byte[] password, byte[] hash) {
        BCrypt.Result result = BCrypt.verifyer().verify(password, hash);
        return result.verified;
    }

    private byte[] hashPassword(String password) {
        return BCrypt.with(random).hash(12, password.getBytes(StandardCharsets.UTF_8));
    }
}
