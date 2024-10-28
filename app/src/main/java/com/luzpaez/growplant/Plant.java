package com.luzpaez.growplant;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.database.PropertyName;

public class Plant implements Parcelable {
    private String date;
    private String description;
    private String family;
    private String imageUrl; // Usa este nombre
    private String name; // Agregar nombre científico
    private int quantity; // Agregar cantidad registrada

    // Constructor vacío requerido por Firebase
    public Plant() {}

    // Constructor con parámetros
    public Plant(String date, String description, String family, String imageUrl, String name, int quantity) {
        this.date = date;
        this.description = description;
        this.family = family;
        this.imageUrl = imageUrl;
        this.name = name; // Asignar nombre
        this.quantity = quantity; // Asignar cantidad
    }

    // Getters con anotaciones
    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getFamily() {
        return family;
    }

    public String getName() {
        return name; // Agregar método getter
    }

    public int getQuantity() {
        return quantity; // Agregar método getter
    }

    @PropertyName("image_url")
    public String getImageUrl() {
        return imageUrl;
    }

    @PropertyName("image_url")
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Implementación de Parcelable
    protected Plant(Parcel in) {
        date = in.readString();
        description = in.readString();
        family = in.readString();
        imageUrl = in.readString();
        name = in.readString(); // Leer nombre científico
        quantity = in.readInt(); // Leer cantidad
    }

    public static final Creator<Plant> CREATOR = new Creator<Plant>() {
        @Override
        public Plant createFromParcel(Parcel in) {
            return new Plant(in);
        }

        @Override
        public Plant[] newArray(int size) {
            return new Plant[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(date);
        dest.writeString(description);
        dest.writeString(family);
        dest.writeString(imageUrl);
        dest.writeString(name); // Escribir nombre científico
        dest.writeInt(quantity); // Escribir cantidad
    }
}
