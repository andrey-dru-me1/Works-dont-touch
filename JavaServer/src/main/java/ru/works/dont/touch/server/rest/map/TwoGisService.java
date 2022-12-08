package ru.works.dont.touch.server.rest.map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.works.dont.touch.server.entities.Card;
import ru.works.dont.touch.server.entities.Coordinate;
import ru.works.dont.touch.server.entities.Location;
import ru.works.dont.touch.server.rest.v1_0.cards.object.card.CardList;
import ru.works.dont.touch.server.rest.v1_0.cards.object.card.CardWithDistance;
import ru.works.dont.touch.server.servicies.CoordinateService;
import ru.works.dont.touch.server.servicies.LocationService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

@Service
public class TwoGisService implements MapService {

    @Autowired
    private LocationService locationService;

    @Autowired
    private CoordinateService coordinateService;
    @Value("${twogis.key}")
    private String key2gis;
    @Value("${twogis.radius}")
    private String radius;

    private static final long maxDistance = 5000;
    private final OkHttpClient client = new OkHttpClient();


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
            LoggerFactory.getLogger(TwoGisService.class).info("distance " + card.getName() + ":" + distance);
        }

        return list;
    }

    private long getDistance(Card card, double latitudeSelf, double longitudeSelf) {
        Iterable<Location> locations = locationService.findAllByCardId(card.getId());
        long distance = 100000;
        for (Location loc : locations) {
            if (!loc.getCustom()) {
                try {
                    long _distance = getDistance(loc.getName(), latitudeSelf, longitudeSelf);
                    if (_distance < distance) {
                        distance = _distance;
                    }
                } catch (IOException ignore) {
                }

            }
            LoggerFactory.getLogger(TwoGisService.class).info("distance " + loc.getName() + ":" + distance + ":" + latitudeSelf + ":" + longitudeSelf);
        }

        Iterable<Coordinate> coordinates = coordinateService.findAllByCardId(card.getId());
        for (Coordinate coordinate : coordinates) {
            long _distance = distance(latitudeSelf, coordinate.getLatitude(), longitudeSelf, coordinate.getLongitude());
            if (_distance < distance) {
                distance = _distance;
            }
            LoggerFactory.getLogger(TwoGisService.class).info("distance " + coordinate.getLatitude() + ":" + coordinate.getLongitude() + ":" + distance + ":" + latitudeSelf + ":" + longitudeSelf);
        }
        return distance;
    }

    private long getDistance(String name, double latitudeSelf, double longitudeSelf) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        Request request = requestBuild(name, latitudeSelf, longitudeSelf);
        JsonNode all;
        Response response = getResponse(request);
        var reqRes = response.body().string();
        //System.out.println(reqRes);
        all = mapper.readTree(reqRes);
        //System.out.println(all.toPrettyString());
        ////all = mapper.readTree("{\"meta\":{\"api_version\":\"3.0.938263\",\"code\":200,\"issue_date\":\"20221208\"},\"result\":{\"items\":[{\"address_comment\":\"1 этаж\",\"address_name\":\"Инженерная, 5/1\",\"id\":\"70000001007379456\",\"name\":\"Быстроном, супермаркет\",\"point\":{\"lat\":54.860612,\"lon\":83.108528},\"type\":\"branch\"}],\"total\":1}}");
        var shops = all.get("result");
        var total = Integer.parseInt(shops.get("total").toPrettyString());
        shops = shops.get("items");
        //System.out.println(shops.toPrettyString());
        long minDist = 100000;
        for (int i = 0; i < total; i++) {
            var shop = shops.get(i);
            //System.out.println(i);
            if (shop == null) {
                continue;
            }
            //System.out.println(shop.toPrettyString());
            Point point =
                    mapper.readValue(shop.get("point").toString(),
                            Point.class);
            long dist = distance(latitudeSelf, point.getLat(), longitudeSelf, point.getLon());
            if (minDist > dist) {
                minDist = dist;
                //System.out.println("\n\n!!!!!!!!\n"+shop.toPrettyString() + "\n\n");
            }
        }
        return minDist;
    }

    private Request requestBuild(String name, double latitudeSelf, double longitudeSelf) {
        RequestBody formBody = new FormBody.Builder()
                .add("card", "s")
                .build();
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("catalog.api.2gis.com")
                .addPathSegments("3.0/items")
                .addQueryParameter("q", name)
                .addQueryParameter("fields", "items.point")
                .addQueryParameter("type", "branch")
                .addQueryParameter("point", String.format(Locale.US, "%f,%f", longitudeSelf, latitudeSelf))
                .addQueryParameter("radius", radius)
                .addQueryParameter("key", key2gis)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        return request;
    }

    private Response getResponse(Request request) throws IOException {
        Response response = null;
        response = client.newCall(request).execute();
        if (!response.isSuccessful())
            throw new IOException("Unexpected code " + response);
        return response;
    }
//    public void run() {
//        File file = new File("D:\\Снимок экрана 2022-10-03 180032.png");
//        MultipartBody body = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("file", file.getName(), RequestBody.create(file, MediaType.parse("image/png")))
//                .addFormDataPart("cardId", 52 + "").build();
//        Request request = new Request.Builder().url("http://localhost:8080/v1.0/images/upload")
//                .post(body)
//                .addHeader("Authorization", authorizationString("test", "test"))
//                .build();
//        try {
//            client.newCall(request).enqueue(new Callback() {
//                @Override
//                public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                    e.printStackTrace();
//                    System.out.println("fail");
//                }
//
//                @Override
//                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                    System.out.println("success " + response.code());
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


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

}
