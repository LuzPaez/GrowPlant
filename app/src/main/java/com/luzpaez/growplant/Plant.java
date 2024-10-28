package com.luzpaez.growplant;

import com.google.firebase.database.PropertyName;

public class Plant {
    private String date;
    private String description;
    private String family;
    private String imageUrl; // Usa este nombre

    public Plant() {}

    public Plant(String date, String description, String family, String imageUrl) {
        this.date = date;
        this.description = description;
        this.family = family;
        this.imageUrl = imageUrl;
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

    @PropertyName("image_url")
    public String getImageUrl() {
        return imageUrl;
    }

    @PropertyName("image_url")
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
