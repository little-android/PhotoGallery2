package com.codve.photogallery2;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UnsplashPhotos {

    @SerializedName("results")
    private List<UnsplashItem> items;

    public List<UnsplashItem> getItems() {
        return items;
    }

    public void setItems(List<UnsplashItem> items) {
        this.items = items;
    }
}
