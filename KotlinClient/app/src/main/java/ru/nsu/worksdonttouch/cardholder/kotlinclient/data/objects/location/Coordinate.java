package ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.location;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Coordinate implements Serializable {

    private static final long serialVersionUID = 0L;

    private double latitude;
    private double longitude;

    @JsonCreator
    public Coordinate(@JsonProperty("latitude") double latitude, @JsonProperty("longitude") double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Coordinate clone() {
        return new Coordinate(latitude, longitude);
    }

}
