package ru.nsu.worksdonttouch.cardholder.kotlinclient.net;

import android.os.Build;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.data.UserData;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.data.card.Card;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.data.card.CardList;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.exception.NotAuthorizedException;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.net.exception.ServerConnectionException;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.net.objects.ImageAnswer;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.net.objects.SimpleHttpResult;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.net.requests.ApiRequests;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@SuppressWarnings("unused")
public abstract class ApiWorker {
    private static final OkHttpClient client = new OkHttpClient();
    private static ObjectMapper objectMapper = new ObjectMapper();

    static String authorizationString(UserData data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return "Basic " + new String(Base64.getEncoder()
                    .encode((data.getLogin() + ":" + data.getPassword()).getBytes(StandardCharsets.UTF_8)));
        }
        return null;
    }

    public static ApiWorker authTest(UserData data) throws IOException, NotAuthorizedException {
        RequestBody formBody = new FormBody.Builder().build();
        Request request = new Request.Builder()
                .url("http://localhost:8080/v1.0/")
                .addHeader("Authorization", authorizationString(data))
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            SimpleHttpResult simpleHttpResult = objectMapper.readValue(response.body().string(), SimpleHttpResult.class);

            if (simpleHttpResult.getCode().equals("ACCEPTED")) {
                return new ApiRequests(data);
            }
            else {
                throw new NotAuthorizedException();
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static ApiWorker registration(UserData data) throws ServerConnectionException, IOException {
        RequestBody formBody = new FormBody.Builder().build();
        Request request = new Request.Builder()
                .url("http://localhost:8080/v1.0/")
                .addHeader("Login", data.getLogin())
                .addHeader("Password", data.getPassword())
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            SimpleHttpResult simpleHttpResult = objectMapper.readValue(response.body().string(), SimpleHttpResult.class);
            if (simpleHttpResult.getCode().equals("ACCEPTED")) {
                return new ApiRequests(data);
            }
            else {
                throw new ServerConnectionException();
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public abstract void changePassword(String password, HttpCallback<SimpleHttpResult> callback) throws Exception;

    public abstract void getCardList(double latitude, double longitude, HttpCallback<CardList> callback) throws Exception;

    public abstract void getCard(long id, HttpCallback<Card> callback) throws Exception;

    public abstract void editCard(Card card, HttpCallback<Card> callback) throws Exception;

    /**
     * TODO: Нужно создать новый объект не содержащий id карты для отправки запроса
     * **/
    public abstract void addCard(Card card, HttpCallback<Card> callback) throws Exception;

    /**
     * Возвращает в callback файл toWrite если все прошло успешно
     * **/
    public abstract void imageGet(long id, File toWrite, HttpCallback<File> callback) throws Exception;

    public abstract void uploadImage(File file, long id, HttpCallback<ImageAnswer> callback) throws Exception;

    public abstract void editImage(File file, long id, HttpCallback<ImageAnswer> callback) throws Exception;
    
    public abstract UserData getUserData();

}
