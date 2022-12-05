package ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects;

import android.net.Uri;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Card {

    private final long id;

    private String name;

    private String barcode;

    private Uri image;

    public Card(long id, @NotNull String name, @Nullable String barcode, Uri image) {
        this.id = id;
        this.name = name;
        this.barcode = barcode;
        this.image = image;
    }

    public long getId() {
        return id;
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

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", barcode='" + barcode + '\'' +
                ", image=" + image +
                '}';
    }
}
