package ru.nsu.worksdonttouch.cardholder.kotlinclient.net.requests;

import android.os.Build;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.data.UserData;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.data.card.Card;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.data.card.CardList;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.net.ApiWorker;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.net.Configuration;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.net.HttpCallback;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.net.objects.ImageAnswer;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.net.objects.SimpleHttpResult;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

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
        HttpUrl url = Configuration.basicBuilder().addPathSegments("auth/change/password").build();
        RequestBody formBody = new FormBody.Builder().build();
        Request request = new Request.Builder()
                .addHeader("Authorization", authorizationString(user))
                .addHeader("Password", password)
                .url(url)
                .post(formBody)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                callback.answer(HttpCallback.HttpResult.errorHandler(response), null);
                return;
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
        HttpUrl url = Configuration.basicBuilder().addPathSegments("cards/getList")
                .addQueryParameter("latitude", latitude + "")
                .addQueryParameter("longitude", longitude + "")
                .build();
        Request request = new Request.Builder()
                .addHeader("Authorization", authorizationString(user))
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                callback.answer(HttpCallback.HttpResult.errorHandler(response), null);
                return;
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
        HttpUrl url = Configuration.basicBuilder().addPathSegments("cards/get")
                .addQueryParameter("cardId", id + "")
                .build();
        Request request = new Request.Builder()
                .addHeader("Authorization", authorizationString(user))
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                callback.answer(HttpCallback.HttpResult.errorHandler(response), null);
                return;
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
        HttpUrl url = Configuration.basicBuilder().addPathSegments("cards/edit").build();
        RequestBody formBody = null;
        try {
            formBody = new FormBody.Builder()
                    .add("card", objectMapper.writeValueAsString(card))
                    .build();
        } catch (JsonProcessingException e) {
            callback.answer(HttpCallback.HttpResult.OTHER, null);
        }
        Request request = new Request.Builder()
                .addHeader("Authorization", authorizationString(user))
                .url(url)
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                callback.answer(HttpCallback.HttpResult.errorHandler(response), null);
                return;
            }
            callback.answer(HttpCallback.HttpResult.SUCCESSFUL, card);

        } catch (IOException e) {
            e.printStackTrace();
            callback.answer(HttpCallback.HttpResult.NO_CONNECTION, null);
        }
    }

    @Override
    public void addCard(Card card, HttpCallback<Card> callback) {
        HttpUrl url = Configuration.basicBuilder().addPathSegments("cards/add").build();
        RequestBody formBody = new FormBody.Builder()
                    .add("card", "{\"name\": \"" + card.getName() +
                            "\", \"barcode\": \"" + card.getBarcode()+"\"}")
                    .build();
        Request request = new Request.Builder()
                .addHeader("Authorization", authorizationString(user))
                .url(url)
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                callback.answer(HttpCallback.HttpResult.errorHandler(response), null);
                return;
            }
            card = objectMapper.readValue(response.body().string(), Card.class);
            callback.answer(HttpCallback.HttpResult.SUCCESSFUL, card);

        } catch (IOException e) {
            e.printStackTrace();
            callback.answer(HttpCallback.HttpResult.NO_CONNECTION, null);
        }
    }

    @Override
    public void imageGet(long id, File toWrite, HttpCallback<File> callback) {
        HttpUrl url = Configuration.basicBuilder().addPathSegments("images/get")
                .addQueryParameter("imageId", id + "")
                .build();
        Request request = new Request.Builder()
                .addHeader("Authorization", authorizationString(user))
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                callback.answer(HttpCallback.HttpResult.errorHandler(response), null);
                return;
            }
            try (InputStream inputStream = response.body().byteStream()) {
                try (OutputStream outputStream = new FileOutputStream(toWrite)) {
                    byte[] buffer = new byte[4096];
                    int size;
                    while((size = inputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, size) ;
                    }
                }
            }
            catch (IOException e) {
                e.printStackTrace();
                callback.answer(HttpCallback.HttpResult.OTHER, null);
            }
            callback.answer(HttpCallback.HttpResult.SUCCESSFUL, toWrite);

        } catch (IOException e) {
            e.printStackTrace();
            callback.answer(HttpCallback.HttpResult.NO_CONNECTION, null);
        }
    }

    @Override
    public void uploadImage(File file, long id, HttpCallback<ImageAnswer> callback) {
        HttpUrl url = Configuration.basicBuilder().addPathSegments("images/upload")
                .addQueryParameter("cardId", id + "")
                .build();
        MultipartBody body = new MultipartBody.Builder()
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(file, MediaType.parse("image/png")))
                .build();
        Request request = new Request.Builder()
                .addHeader("Authorization", authorizationString(user))
                .url(url)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                callback.answer(HttpCallback.HttpResult.errorHandler(response), null);
                return;
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
        HttpUrl url = Configuration.basicBuilder().addPathSegments("images/edit")
                .addQueryParameter("imageId", id + "")
                .build();
        MultipartBody body = new MultipartBody.Builder()
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(file, MediaType.parse("image/png")))
                .build();
        Request request = new Request.Builder()
                .addHeader("Authorization", authorizationString(user))
                .url(url)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                callback.answer(HttpCallback.HttpResult.errorHandler(response), null);
                return;
            }
            ImageAnswer imageAnswer = objectMapper.readValue(response.body().string(), ImageAnswer.class);
            callback.answer(HttpCallback.HttpResult.SUCCESSFUL, imageAnswer);

        } catch (IOException e) {
            e.printStackTrace();
            callback.answer(HttpCallback.HttpResult.NO_CONNECTION, null);
        }
    }

    @Override
    public UserData getUserData() {
        return user;
    }
}
