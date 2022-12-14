package ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.card;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.location.Location;

public class LocalCard extends Card implements Serializable {

    private static final long serialVersionUID = 0L;

    private Integer localID;

    @JsonCreator
    public LocalCard(
            @Nullable
            @JsonProperty("localID") Integer localID,
            @NotNull
            @JsonProperty("name") String name,
            @Nullable
            @JsonProperty("barcode") String barcode,
            @NotNull
            @JsonProperty("images") List<Long> images,
            @NotNull
            @JsonProperty("locations") List<Location> locations) {
        super(null, name, barcode, images, locations);
        this.localID = localID;
    }

    public LocalCard(
            @NotNull String name,
            @Nullable String barcode,
            @NotNull List<Long> images,
            @NotNull List<Location> coordinates) {
        super(null, name, barcode, images, coordinates);
        this.localID = null;
    }

    public Integer getLocalID() {
        return localID;
    }

    public void setLocalID(Integer localID) {
        this.localID = localID;
    }

    @Override
    public LocalCard clone() {
        return new LocalCard(localID, name, barcode,  new ArrayList<>(images), locations.stream().map(Location::clone).collect(Collectors.toList()));
    }

    @Override
    public boolean equals(Object object) {
        return (object instanceof LocalCard) && ((LocalCard)object).getLocalID().equals(localID);
    }

}
