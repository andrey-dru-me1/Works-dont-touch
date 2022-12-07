package ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.Serializable;

public class Card implements Parcelable {

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

    protected Card(Parcel in) {
        name = in.readString();
        barcode = in.readString();
        image = in.readParcelable(Bitmap.class.getClassLoader());
        path = in.readString();
    }

    public static final Creator<Card> CREATOR = new Creator() {
        @Override
        public Card createFromParcel(Parcel in) {
            return new Card(in);
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };

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

    @NonNull
    @Override
    public String toString() {
        return "Card{" +
                "name='" + name + '\'' +
                ", barcode='" + barcode + '\'' +
                ", image=" + image +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.barcode);
        dest.writeParcelable(this.image, 0);
        dest.writeString(this.path);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
