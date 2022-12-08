package ru.nsu.worksdonttouch.cardholder.kotlinclient.net.requests;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.UserData;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.card.Card;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.card.CardList;
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

    public static String authorizationString(UserData data) {
        if (true) {
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
                .addQueryParameter("id", id + "")
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
        RequestBody body = null;
        try {
            body = RequestBody.create(objectMapper.writeValueAsString(card), JSON);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
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
            callback.answer(HttpCallback.HttpResult.SUCCESSFUL, card);

        } catch (IOException e) {
            e.printStackTrace();
            callback.answer(HttpCallback.HttpResult.NO_CONNECTION, null);
        }
    }

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    @Override
    public void addCard(Card card, HttpCallback<Card> callback) {
        HttpUrl url = Configuration.basicBuilder().addPathSegments("cards/add").build();
        RequestBody body = RequestBody.create("{\"name\": \"" + card.getName() +
                "\", \"barcode\": \"" + card.getBarcode()+"\"}", JSON);

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
            card = objectMapper.readValue(response.body().string(), Card.class);
            callback.answer(HttpCallback.HttpResult.SUCCESSFUL, card);
            return;

        } catch (IOException e) {
            e.printStackTrace();
            callback.answer(HttpCallback.HttpResult.NO_CONNECTION, null);
        }
    }

    @Override
    public void imageGet(long id, File toWrite, HttpCallback<File> callback) {
        HttpUrl url = Configuration.basicBuilder().addPathSegments("images/get")
                .addQueryParameter("id", id + "")
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
                    byte[] buffer = new byte[1<<17];
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
        final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        RequestBody req = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart(
                        "file",
                        file.getName(),
                        RequestBody.create(MEDIA_TYPE_PNG, file)
                )
                .build();
        Request request = new Request.Builder()
                .addHeader("Authorization", authorizationString(user))
                .url(url)
                .post(req)
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
                .addQueryParameter("id", id + "")
                .build();
        final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        RequestBody req = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart(
                        "file",
                        file.getName(),
                        RequestBody.create(MEDIA_TYPE_PNG, file)
                )
                .build();
        Request request = new Request.Builder()
                .addHeader("Authorization", authorizationString(user))
                .url(url)
                .post(req)
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
