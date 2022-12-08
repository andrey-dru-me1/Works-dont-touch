package ru.nsu.worksdonttouch.cardholder.kotlinclient.net.requests;

import android.os.Build;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
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
import java.util.Map;

@SuppressWarnings("unused")
public class ApiRequests extends ApiWorker {
    protected UserData user;

    public ApiRequests(UserData user) {
        this.user = user;
    }

    static class Gist {
        Map<String, GistFile> files;

    }
    static class GistFile {
        String content;

    }
    private static final OkHttpClient client = new OkHttpClient();
    private final Moshi moshi = new Moshi.Builder().build();
    private final JsonAdapter<Gist> gistJsonAdapter = moshi.adapter(Gist.class);

    private boolean checkResponse(Response response) throws Exception {
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
        if (response.body() == null) {
            throw new IOException("No response");
        }
        return true;
    }

    static String authorizationString(UserData data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return "Basic " + new String(Base64.getEncoder()
                    .encode((data.getLogin() + ":" + data.getPassword()).getBytes(StandardCharsets.UTF_8)));
        }
        return null;
    }

    @Override
    public void changePassword(String password, HttpCallback<SimpleHttpResult> callback) throws Exception {
        RequestBody formBody = new FormBody.Builder()
                .build();
        Request request = new Request.Builder()
                .url("http://localhost:8080/v1.0/")
                .addHeader("Password", password)
                .addHeader("Authorization", authorizationString(user))
                .post(formBody)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!checkResponse(response)) {
                callback.answer(HttpCallback.HttpResult.FAIL, null);
            }

            SimpleHttpResult simpleHttpResult = null;
            callback.answer(HttpCallback.HttpResult.SUCCESSFUL, simpleHttpResult);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getCardList(double latitude, double longitude, HttpCallback<CardList> callback) throws Exception {
        HttpUrl url = HttpUrl.parse("http://localhost:8080/v1.0/").newBuilder()
                .addQueryParameter("latitude", latitude + "")
                .addQueryParameter("longitude", longitude + "")
                .build();
        Request request = new Request.Builder()
                .addHeader("Authorization", authorizationString(user))
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!checkResponse(response)) {
                callback.answer(HttpCallback.HttpResult.FAIL, null);
            }

            CardList cardList = null;
            callback.answer(HttpCallback.HttpResult.SUCCESSFUL, cardList);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getCard(long id, HttpCallback<Card> callback) throws Exception {
        HttpUrl url = HttpUrl.parse("http://localhost:8080/v1.0/").newBuilder()
                .addQueryParameter("cardId", id + "")
                .build();
        Request request = new Request.Builder()
                .addHeader("Authorization", authorizationString(user))
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!checkResponse(response)) {
                callback.answer(HttpCallback.HttpResult.FAIL, null);
            }

            Card card = null;
            callback.answer(HttpCallback.HttpResult.SUCCESSFUL, card);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void editCard(Card card, HttpCallback<Card> callback) throws Exception {
        RequestBody formBody = new FormBody.Builder()
                .add("card", card.toString())
                .build();
        Request request = new Request.Builder()
                .addHeader("Authorization", authorizationString(user))
                .url("http://localhost:8080/v1.0/")
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!checkResponse(response)) {
                callback.answer(HttpCallback.HttpResult.FAIL, null);
            }

            callback.answer(HttpCallback.HttpResult.SUCCESSFUL, card);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addCard(Card card, HttpCallback<Card> callback) throws Exception {
        RequestBody formBody = new FormBody.Builder()
                .add("card", card.toString())
                .build();
        Request request = new Request.Builder()
                .addHeader("Authorization", authorizationString(user))
                .url("http://localhost:8080/v1.0/")
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!checkResponse(response)) {
                callback.answer(HttpCallback.HttpResult.FAIL, null);
            }

            callback.answer(HttpCallback.HttpResult.SUCCESSFUL, card);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void imageGet(long id, File toWrite, HttpCallback<File> callback) throws Exception {
        HttpUrl url = HttpUrl.parse("http://localhost:8080/v1.0/").newBuilder()
                .addQueryParameter("imageId", id + "")
                .build();
        Request request = new Request.Builder()
                .addHeader("Authorization", authorizationString(user))
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!checkResponse(response)) {
                callback.answer(HttpCallback.HttpResult.FAIL, null);
            }

            File file = null;
            callback.answer(HttpCallback.HttpResult.SUCCESSFUL, file);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void uploadImage(File file, long id, HttpCallback<ImageAnswer> callback) throws Exception {
        MultipartBody body = new MultipartBody.Builder()
                .addFormDataPart("cardId", id + "")
                .addFormDataPart("file", file.getName(), RequestBody.create(file, MediaType.parse("image/png")))
                .build();
        Request request = new Request.Builder()
                .addHeader("Authorization", authorizationString(user))
                .url("http://localhost:8080/v1.0/")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!checkResponse(response)) {
                callback.answer(HttpCallback.HttpResult.FAIL, null);
            }

            callback.answer(HttpCallback.HttpResult.SUCCESSFUL, null);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void editImage(File file, long id, HttpCallback<ImageAnswer> callback) throws Exception {
        MultipartBody body = new MultipartBody.Builder()
                .addFormDataPart("cardId", id + "")
                .addFormDataPart("file", file.getName(), RequestBody.create(file, MediaType.parse("image/png")))
                .build();
        Request request = new Request.Builder()
                .addHeader("Authorization", authorizationString(user))
                .url("http://localhost:8080/v1.0/")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!checkResponse(response)) {
                callback.answer(HttpCallback.HttpResult.FAIL, null);
            }


            callback.answer(HttpCallback.HttpResult.SUCCESSFUL, null);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
