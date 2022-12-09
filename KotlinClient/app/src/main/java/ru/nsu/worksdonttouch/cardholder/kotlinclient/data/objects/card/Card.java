package ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.card;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.location.Location;

public class Card implements Parcelable {

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

    protected Card(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        name = in.readString();
        barcode = in.readString();
        in.readList(images, null);
        in.readList(locations, null);
    }

    public static final Creator<Card> CREATOR = new Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel in) {
            return new Card(in);
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };

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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        if(this.id == null) return;
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(barcode);
        dest.writeList(images);
        dest.writeList(locations);
    }

    @Override
    public boolean equals(Object object) {
        return (object instanceof Card) && ((Card)object).getId().equals(id);
    }
}
