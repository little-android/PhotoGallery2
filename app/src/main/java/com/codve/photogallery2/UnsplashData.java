package com.codve.photogallery2;

import com.google.gson.annotations.SerializedName;

public class UnsplashData {

    @SerializedName("photos")
    private UnsplashPhotos photos;

    public UnsplashPhotos getPhotos() {
        return photos;
    }

    public void setPhotos(UnsplashPhotos photos) {
        this.photos = photos;
    }
}
