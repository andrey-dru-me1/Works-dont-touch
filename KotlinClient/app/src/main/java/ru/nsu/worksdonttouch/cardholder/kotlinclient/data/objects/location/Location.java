package ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.location;

import java.util.List;
import java.util.stream.Collectors;

public class Location {

    private String name;
    private boolean isCustom;
    private List<Coordinate> coordinates;

    public Location(String name, boolean isCustom,  List<Coordinate> coordinates) {
        this.name = name;
        this.isCustom = isCustom;
        this.coordinates = coordinates;
    }

    public String getName() {
        return name;
    }

    public boolean isCustom() {
        return isCustom;
    }

    public List<Coordinate> getCoordinates() {
        return coordinates;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCustom(boolean custom) {
        isCustom = custom;
    }

    public void setCoordinates(List<Coordinate> coordinates) {
        this.coordinates = coordinates;
    }

    public Location clone() {
        return new Location(name, isCustom, coordinates.stream().map(Coordinate::clone).collect(Collectors.toList()));
    }
}
