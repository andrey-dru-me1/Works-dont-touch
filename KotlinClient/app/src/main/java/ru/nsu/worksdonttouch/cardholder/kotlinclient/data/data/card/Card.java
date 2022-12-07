package ru.nsu.worksdonttouch.cardholder.kotlinclient.data.data.card;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.data.location.Location;

public class Card {

    @Nullable
    private Long id;
    @NotNull
    private String name;
    @Nullable
    private String barcode;
    @NotNull
    private List<Long> images;
    @NotNull
    private List<Location> locations;

    @JsonCreator
    public Card(
            @Nullable
            @JsonProperty("id") Long id,
            @NotNull
            @JsonProperty("name") String name,
            @Nullable
            @JsonProperty("barcode") String barcode,
            @NotNull
            @JsonProperty("images") List<Long> images,
            @NotNull
            @JsonProperty("locations") List<Location> locations) {
        this.id = id;
        this.name = name;
        this.barcode = barcode;
        this.images = images;
        this.locations = locations;
    }

    public Card(
            @NotNull
            String name,
            @Nullable
            String barcode,
            @Nullable
            List<Long> images,
            @Nullable
            List<Location> locations) {
        this.id = null;
        this.name = name;
        this.barcode = barcode;
        this.images = images == null ? new ArrayList<>() : images;
        this.locations = locations == null ? new ArrayList<>() : locations;
    }

    @Nullable
    public Long getId() {
        return id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @Nullable
    public String getBarcode() {
        return barcode;
    }

    @NotNull
    public List<Long> getImages() {
        return images;
    }

    @NotNull
    public List<Location> getLocations() {
        return locations;
    }

    public void setId(@Nullable Long id) {
        this.id = id;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public void setBarcode(@Nullable String barcode) {
        this.barcode = barcode;
    }
}
