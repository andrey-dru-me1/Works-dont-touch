package ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.List;

public class Card implements Serializable {

    private final long id;

    private String name;

    private String barcode;

    private List<Long> images;

    public Card(long id, @NotNull String name, @Nullable String barcode) {
        this.id = id;
        this.name = name;
        this.barcode = barcode;
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

    public List<Long> getImages() {
        return images;
    }

    @NonNull
    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", barcode='" + barcode + '\'' +
                ", images=" + images +
                '}';
    }

}
