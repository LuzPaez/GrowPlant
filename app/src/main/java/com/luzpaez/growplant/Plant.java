package com.luzpaez.growplant;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.database.PropertyName;

public class Plant implements Parcelable {
    private String plantId; // ID de la planta
    private String date;
    private String description; //agregar nombre comun
    private String family;
    private String imageUrl;
    private String name; // Agregar nombre científico
    private int quantity; // Agregar cantidad registrada

    // Constructor vacío requerido por Firebase
    public Plant() {}

    // Constructor con parámetros
    public Plant(String plantId, String date, String description, String family, String imageUrl, String name, int quantity) {
        this.plantId = plantId; // Para asignar ID de planta
        this.date = date;
        this.description = description; //Para asignar nombre común
        this.family = family;
        this.imageUrl = imageUrl;
        this.name = name; // Para asignar  nombre cientifico
        this.quantity = quantity; // Para asignar  cantidad
    }

    // Getters
    public String getPlantId() {
        return plantId; // Método getter para plantId
    }

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
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    @PropertyName("image_url")
    public String getImageUrl() {
        return imageUrl;
    }


    // Implementación de Parcelable
    protected Plant(Parcel in) {
        plantId = in.readString(); // Leer ID de planta
        date = in.readString();
        description = in.readString(); //leer nombre común
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
        dest.writeString(plantId); //  ID de planta
        dest.writeString(date);
        dest.writeString(description); //nombre comun
        dest.writeString(family);
        dest.writeString(imageUrl);
        dest.writeString(name); //  nombre científico
        dest.writeInt(quantity); //  cantidad
    }
}
