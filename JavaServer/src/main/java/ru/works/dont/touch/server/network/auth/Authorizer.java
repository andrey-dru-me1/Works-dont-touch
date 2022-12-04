package ru.works.dont.touch.server.network.auth;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

//TODO: сделать кастомные исключения
@Component("authorizer")
public class Authorizer {

    public boolean authorization(@NotNull String[] authorization) throws Exception {
        if(authorization.length == 0)
            throw new Exception();
        return switch (authorization[0]) {
            case "Base" -> baseAuthorization(authorization[1]);
            default -> throw new Exception();
        };
    }

    public boolean registration(@NotNull String login, @NotNull String password) throws Exception {
        //TODO: поиск в кэшированных значениях, запрос к базе данных
        return true;
    }

    public boolean changePassword(@NotNull String[] authorization, @NotNull String password) throws Exception {
        //TODO: реализовать логику
        return true;
    }

    private boolean baseAuthorization(String base64String) throws Exception {
        try {
            String data = new String(Base64.getDecoder().decode(base64String.getBytes(StandardCharsets.UTF_8)));
            String[] values = data.split(":", 2);
            if(values.length != 2)
                throw new Exception();
            //TODO: поиск в кэшированных значениях, запрос к базе данных
        } catch (IllegalArgumentException e) {
            throw new Exception();
        }
        return true;
    }


}
