package ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class Card {

    private String name;

    private String barcode;

    private Bitmap image;

    private String path;

    public Card(@NotNull String name, @Nullable String barcode, Bitmap image, String path) {
        this.name = name;
        this.barcode = barcode;
        this.image = image;
        this.path = path;

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
                    String imagePath
    ) {
        this.name = name;
        this.barcode = barcode;
        this.path = imagePath;
        this.image = BitmapFactory.decodeFile(imagePath);
    }

    public String getName() {
        return name;
    }

    public String getBarcode() {
        return barcode;
    }

    public Bitmap getImage() {
        return image;
    }

    @JsonGetter("image")
    public String getImagePath() {
        return this.path;
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
