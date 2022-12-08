package ru.nsu.worksdonttouch.cardholder.kotlinclient.net;

import okhttp3.HttpUrl;

@SuppressWarnings("unused")
public class Configuration {
    private Configuration() {}

    public static String scheme = "http";
    public static String host = "localhost";
    public static Integer port = 8080;
    public static String basicPathSegment = "v1.0";

    public static HttpUrl.Builder basicBuilder() {
        HttpUrl.Builder builder = new HttpUrl.Builder().scheme(scheme).host(host);
        if (port != null) {
            builder = builder.port(port);
        }
        if (basicPathSegment != null) {
            return builder.addPathSegments(basicPathSegment);
        }
        return builder;
    }
}
