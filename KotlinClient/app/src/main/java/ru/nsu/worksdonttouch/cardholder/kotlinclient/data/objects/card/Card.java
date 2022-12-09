package ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.card;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.location.Location;

public class Card implements Serializable {

    private static final long serialVersionUID = 0L;

    @Nullable
    protected Long id;
    @NotNull
    protected String name;
    @Nullable
    protected String barcode;
    @NotNull
    protected List<Long> images;
    @NotNull
    protected List<Location> locations;

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
        this.images = Collections.synchronizedList(images == null ? new ArrayList<>() : images);
        this.locations = Collections.synchronizedList(locations == null ? new ArrayList<>() : locations);
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

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public void setBarcode(@Nullable String barcode) {
        this.barcode = barcode;
    }

    public Card clone() {
        return new Card(id, name, barcode, new ArrayList<>(images), locations.stream().map(Location::clone).collect(Collectors.toList()));
    }

    @Override
    public boolean equals(Object object) {
        return (object instanceof Card) && ((Card)object).getId().equals(id);
    }
}
