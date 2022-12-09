package ru.nsu.worksdonttouch.cardholder.kotlinclient.net;


import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.exception.NotAuthorizedException;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.UserData;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.card.Card;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.card.CardList;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.net.exception.ServerConnectionException;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.net.objects.ImageAnswer;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.net.objects.SimpleHttpResult;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.net.requests.ApiRequests;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;



public abstract class ApiWorker {
    private static final OkHttpClient client = new OkHttpClient();
    private static ObjectMapper objectMapper = new ObjectMapper();

    static String authorizationString(UserData data) {
        return "Basic "+ new String(Base64.getEncoder().encode((data.getLogin() + ":" + data.getPassword()).getBytes(StandardCharsets.UTF_8)));
    }

    public static ApiWorker authTest(UserData data) throws IOException, NotAuthorizedException, NullPointerException {
        HttpUrl url = Configuration.basicBuilder().addPathSegments("auth/test").build();
        RequestBody formBody = new FormBody.Builder().build();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", authorizationString(data))
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {

            if (response.body() == null) {
                throw new NullPointerException();
            }
            if (!response.isSuccessful()){
                throw new NotAuthorizedException();
            }

            SimpleHttpResult simpleHttpResult = objectMapper.readValue(response.body().string(), SimpleHttpResult.class);

            if (simpleHttpResult.getCode().equals("ACCEPTED")) {
                return new ApiRequests(data);
            }
            else {

                throw new NotAuthorizedException();
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ApiWorker registration(UserData data) throws IOException, NullPointerException {
        HttpUrl url = Configuration.basicBuilder().addPathSegments("auth/registration").build();
        RequestBody formBody = new FormBody.Builder().build();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Login", data.getLogin())
                .addHeader("Password", data.getPassword())
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            if (response.body() == null) {
                throw new NullPointerException();
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
        }
        return null;
    }

    public abstract void changePassword(String password, HttpCallback<SimpleHttpResult> callback);

    public abstract void getCardList(double latitude, double longitude, HttpCallback<CardList> callback);

    public abstract void getCardList(HttpCallback<CardList> callback);

    public abstract void getCard(long id, HttpCallback<Card> callback);

    public abstract void deleteCard(Card card, HttpCallback<Card> callback);

    public abstract void editCard(Card card, HttpCallback<Card> callback);

    /**
     * TODO: Нужно создать новый объект не содержащий id карты для отправки запроса
     * **/
    public abstract void addCard(Card card, HttpCallback<Card> callback);

    /**
     * Возвращает в callback файл toWrite если все прошло успешно
     * **/
    public abstract void imageGet(long id, File toWrite, HttpCallback<File> callback);

    public abstract void uploadImage(File file, long id, HttpCallback<ImageAnswer> callback);

    public abstract void editImage(File file, long id, HttpCallback<ImageAnswer> callback);
    
    public abstract UserData getUserData();

}
