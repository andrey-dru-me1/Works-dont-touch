package ru.works.dont.touch.server.rest.map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.works.dont.touch.server.entities.Card;
import ru.works.dont.touch.server.entities.Coordinate;
import ru.works.dont.touch.server.entities.Location;
import ru.works.dont.touch.server.rest.v1_0.cards.object.card.CardList;
import ru.works.dont.touch.server.rest.v1_0.cards.object.card.CardWithDistance;
import ru.works.dont.touch.server.servicies.CoordinateService;
import ru.works.dont.touch.server.servicies.LocationService;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Locale;

@Service
public class TwoGisService implements MapService {

    @Autowired
    private LocationService locationService;

    @Autowired
    private CoordinateService coordinateService;

    private static final long maxDistance = 5000;
    private final OkHttpClient client = new OkHttpClient();
    public static class Card2 {
        public long cardId;
        public String name;
        public String barcode;
        public long[] images;
        public Location location;

        public Card2(long cardId, String name, String barcode, long[] images, Location location) {
            this.cardId = cardId;
            this.name = name;
            this.barcode = barcode;
            this.images = images;
            this.location = location;
        }

        public String toString() {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json;
            try {
                json = ow.writeValueAsString(this);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return json;
        }
    }

    @Override
    public CardList sortCards(Iterable<Card> cards, double latitudeSelf, double longitudeSelf) {
        CardList list = new CardList(new ArrayList<>(), new ArrayList<>());
        for (Card card : cards) {
            long distance = getDistance(card, latitudeSelf, longitudeSelf);
            if (distance <= maxDistance) {
                list.nearest().add(new CardWithDistance(card.getId(), distance));
            } else {
                list.other().add(card.getId());
            }
            LoggerFactory.getLogger(TwoGisService.class).info("distance "+ card.getName() + ":"+distance);
        }

        return list;
    }

    private long getDistance(Card card, double latitudeSelf, double longitudeSelf)  {

        Iterable<Location> locations = locationService.findAllByCardId(card.getId());
        long distance = 100000;
        for (Location loc : locations) {
            if (!loc.getCustom()) {
                long _distance = getDistance(loc.getName(), latitudeSelf, longitudeSelf);
                if(_distance < distance) {
                    distance = _distance;
                }
            }
            LoggerFactory.getLogger(TwoGisService.class).info("distance "+ loc.getName() + ":" + distance + ":" +latitudeSelf + ":"+longitudeSelf);
        }

        Iterable<Coordinate> coordinates = coordinateService.findAllByCardId(card.getId());
        for(Coordinate coordinate : coordinates) {
            long _distance = distance(latitudeSelf, coordinate.getLatitude(), longitudeSelf, coordinate.getLongitude());
            if(_distance < distance) {
                distance = _distance;
            }
            LoggerFactory.getLogger(TwoGisService.class).info("distance "+ coordinate.getLatitude() + ":" + coordinate.getLongitude() + ":" + distance + ":" +latitudeSelf + ":"+longitudeSelf);
        }
        return distance;
    }

    private long getDistance(String name, double latitudeSelf, double longitudeSelf) {
        //some request
        return 100000;
    }
    public void run() {
        File file = new File("D:\\Снимок экрана 2022-10-03 180032.png");
        MultipartBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(file, MediaType.parse("image/png")))
                .addFormDataPart("cardId", 52 + "").build();
        Request request = new Request.Builder().url("http://localhost:8080/v1.0/images/upload")
                .post(body)
                .addHeader("Authorization", authorizationString("test", "test"))
                .build();
        try {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                    System.out.println("fail");
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    System.out.println("success " + response.code());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void cardAdd() {
        RequestBody formBody = new FormBody.Builder()
                .add("card", "s")
                .build();
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("catalog.api.2gis.com")
                .addPathSegments("3.0/items")
                .addQueryParameter("q", "кафе")
                .addQueryParameter("fields", "items.point")
                .addQueryParameter("type", "branch")
                .addQueryParameter("point", String.format(Locale.US,"%f,%f",83.092295, 54.8471105 ))
                .addQueryParameter("radius", "5000")
                .addQueryParameter("key", "rurzhm0379")
                .build();
        System.out.println(url);


        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        ObjectMapper mapper = new ObjectMapper();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            assert response.body() != null;
            var reqRes = response.body().string();



            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static long distance(double lat1, double lat2, double lon1, double lon2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (long) (R * c * 1000);
    }
    String authorizationString(String login, String password) {
        return "Basic " + new String(Base64.getEncoder().encode((login + ":" + password).getBytes(StandardCharsets.UTF_8)));
    }

}
