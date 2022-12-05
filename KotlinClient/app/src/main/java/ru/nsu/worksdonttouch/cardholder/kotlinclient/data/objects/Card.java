package ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects;

import android.net.Uri;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Card {

    private String name;

    private String barcode;

    private Uri image;

    public Card(@NotNull String name, @Nullable String barcode, Uri image) {
        this.name = name;
        this.barcode = barcode;
        this.image = image;
    }

    @JsonCreator
    public Card(
            @JsonProperty("name")
            @NotNull
                    String name,
            @JsonProperty("barcode")
            @Nullable
                    String barcode,
            @JsonProperty("image")
                    String image
    ) {
        this.name = name;
        this.barcode = barcode;
        this.image = Uri.parse(image);
    }

    public String getName() {
        return name;
    }

    public String getBarcode() {
        return barcode;
    }

    public Uri getImage() {
        return image;
    }

    @JsonGetter("image")
    public String getStringImage() {
        return this.image.toString();
    }

    @Override
    public String toString() {
        return "Card{" +
                "name='" + name + '\'' +
                ", barcode='" + barcode + '\'' +
                ", image=" + image +
                '}';
    }
}
