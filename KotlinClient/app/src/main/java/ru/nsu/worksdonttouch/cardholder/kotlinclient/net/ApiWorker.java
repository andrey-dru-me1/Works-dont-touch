package ru.nsu.worksdonttouch.cardholder.kotlinclient.net;

import android.os.Build;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unused")
public abstract class ApiWorker {

    protected UserData user;
    private static ApiWorker apiWorker;

    static class Gist {
        Map<String, GistFile> files;

    }
    static class GistFile {
        String content;

    }
    private static final OkHttpClient client = new OkHttpClient();
    private final Moshi moshi = new Moshi.Builder().build();
    private final JsonAdapter<Gist> gistJsonAdapter = moshi.adapter(Gist.class);


    static String authorizationString(String login, String password) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return "Basic " + new String(Base64.getEncoder()
                    .encode((login + ":" + password).getBytes(StandardCharsets.UTF_8)));
        }
        return null;
    }

    public static ApiWorker login(UserData data) throws Exception {
        Request request = new Request.Builder()
                .url("http://localhost:8080/v1.0/")
                .addHeader("Authorization",
                        Objects.requireNonNull(authorizationString(data.getLogin(), data.getPassword())))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            if (response.body() == null) {
                throw new IOException("No response");
            }

            if (response.body().string().equals("true")) {
                return new ApiRequests();
            } else {
                throw new NotAuthorizedException();
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerConnectionException();
        }
    }

    public static ApiWorker register(UserData data) throws Exception {
        apiWorker.user = data;
        return apiWorker;
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

    public abstract void uploadImage(long id, HttpCallback<ImageAnswer> callback) throws Exception;

}
