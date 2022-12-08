package ru.nsu.worksdonttouch.cardholder.kotlinclient.net.requests;

import android.os.Build;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.data.UserData;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.data.card.Card;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.data.card.CardList;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.net.ApiWorker;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.net.HttpCallback;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.net.objects.ImageAnswer;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.net.objects.SimpleHttpResult;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@SuppressWarnings({"unused"})
public class ApiRequests extends ApiWorker {
    protected UserData user;

    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public ApiRequests(UserData user) {
        this.user = user;
    }

    static String authorizationString(UserData data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return "Basic " + new String(Base64.getEncoder()
                    .encode((data.getLogin() + ":" + data.getPassword()).getBytes(StandardCharsets.UTF_8)));
        }
        return null;
    }

    @Override
    public void changePassword(String password, HttpCallback<SimpleHttpResult> callback) {
        RequestBody formBody = new FormBody.Builder().build();
        Request request = new Request.Builder()
                .addHeader("Authorization", authorizationString(user))
                .addHeader("Password", password)
                .url("http://localhost:8080/v1.0/")
                .post(formBody)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                callback.answer(HttpCallback.HttpResult.errorHandler(response), null);
            }
            SimpleHttpResult simpleHttpResult = objectMapper.readValue(response.body().string(), SimpleHttpResult.class);
            if (simpleHttpResult.getCode().equals("ACCEPTED")) {
                callback.answer(HttpCallback.HttpResult.errorHandler(response), simpleHttpResult);
            }
            else {
                callback.answer(HttpCallback.HttpResult.errorHandler(response), null);
            }

        } catch (IOException e) {
            e.printStackTrace();
            callback.answer(HttpCallback.HttpResult.NO_CONNECTION, null);
        }
    }

    @Override
    public void getCardList(double latitude, double longitude, HttpCallback<CardList> callback) {
        HttpUrl url = HttpUrl.parse("http://localhost:8080/v1.0/").newBuilder()
                .addQueryParameter("latitude", latitude + "")
                .addQueryParameter("longitude", longitude + "")
                .build();
        Request request = new Request.Builder()
                .addHeader("Authorization", authorizationString(user))
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                callback.answer(HttpCallback.HttpResult.errorHandler(response), null);
            }
            CardList cardList = objectMapper.readValue(response.body().string(), CardList.class);
            callback.answer(HttpCallback.HttpResult.SUCCESSFUL, cardList);

        } catch (IOException e) {
            e.printStackTrace();
            callback.answer(HttpCallback.HttpResult.NO_CONNECTION, null);
        }
    }

    @Override
    public void getCard(long id, HttpCallback<Card> callback) {
        HttpUrl url = HttpUrl.parse("http://localhost:8080/v1.0/").newBuilder()
                .addQueryParameter("cardId", id + "")
                .build();
        Request request = new Request.Builder()
                .addHeader("Authorization", authorizationString(user))
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                callback.answer(HttpCallback.HttpResult.errorHandler(response), null);
            }
            Card card = objectMapper.readValue(response.body().string(), Card.class);
            callback.answer(HttpCallback.HttpResult.SUCCESSFUL, card);

        } catch (IOException e) {
            e.printStackTrace();
            callback.answer(HttpCallback.HttpResult.NO_CONNECTION, null);
        }
    }

    @Override
    public void editCard(Card card, HttpCallback<Card> callback) {
        RequestBody formBody = new FormBody.Builder()
                .add("card", card.toString())
                .build();
        Request request = new Request.Builder()
                .addHeader("Authorization", authorizationString(user))
                .url("http://localhost:8080/v1.0/")
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                callback.answer(HttpCallback.HttpResult.errorHandler(response), null);
            }
            callback.answer(HttpCallback.HttpResult.SUCCESSFUL, card);

        } catch (IOException e) {
            e.printStackTrace();
            callback.answer(HttpCallback.HttpResult.NO_CONNECTION, null);
        }
    }

    @Override
    public void addCard(Card card, HttpCallback<Card> callback) {
        RequestBody formBody = new FormBody.Builder()
                .add("card", card.toString())
                .build();
        Request request = new Request.Builder()
                .addHeader("Authorization", authorizationString(user))
                .url("http://localhost:8080/v1.0/")
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                callback.answer(HttpCallback.HttpResult.errorHandler(response), null);
            }
            callback.answer(HttpCallback.HttpResult.SUCCESSFUL, card);

        } catch (IOException e) {
            e.printStackTrace();
            callback.answer(HttpCallback.HttpResult.NO_CONNECTION, null);
        }
    }

    @Override
    public void imageGet(long id, File toWrite, HttpCallback<File> callback) {
        HttpUrl url = HttpUrl.parse("http://localhost:8080/v1.0/").newBuilder()
                .addQueryParameter("imageId", id + "")
                .build();
        Request request = new Request.Builder()
                .addHeader("Authorization", authorizationString(user))
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                callback.answer(HttpCallback.HttpResult.errorHandler(response), null);
            }
            File file = null;
            callback.answer(HttpCallback.HttpResult.SUCCESSFUL, file);

        } catch (IOException e) {
            e.printStackTrace();
            callback.answer(HttpCallback.HttpResult.NO_CONNECTION, null);
        }
    }

    @Override
    public void uploadImage(File file, long id, HttpCallback<ImageAnswer> callback) {
        MultipartBody body = new MultipartBody.Builder()
                .addFormDataPart("cardId", id + "")
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(file, MediaType.parse("image/png")))
                .build();
        Request request = new Request.Builder()
                .addHeader("Authorization", authorizationString(user))
                .url("http://localhost:8080/v1.0/")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                callback.answer(HttpCallback.HttpResult.errorHandler(response), null);
            }
            ImageAnswer imageAnswer = objectMapper.readValue(response.body().string(), ImageAnswer.class);
            callback.answer(HttpCallback.HttpResult.SUCCESSFUL, imageAnswer);

        } catch (IOException e) {
            e.printStackTrace();
            callback.answer(HttpCallback.HttpResult.NO_CONNECTION, null);
        }
    }

    @Override
    public void editImage(File file, long id, HttpCallback<ImageAnswer> callback) {
        MultipartBody body = new MultipartBody.Builder()
                .addFormDataPart("cardId", id + "")
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(file, MediaType.parse("image/png")))
                .build();
        Request request = new Request.Builder()
                .addHeader("Authorization", authorizationString(user))
                .url("http://localhost:8080/v1.0/")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                callback.answer(HttpCallback.HttpResult.errorHandler(response), null);
            }
            ImageAnswer imageAnswer = objectMapper.readValue(response.body().string(), ImageAnswer.class);
            callback.answer(HttpCallback.HttpResult.SUCCESSFUL, imageAnswer);

        } catch (IOException e) {
            e.printStackTrace();
            callback.answer(HttpCallback.HttpResult.NO_CONNECTION, null);
        }
    }
}
