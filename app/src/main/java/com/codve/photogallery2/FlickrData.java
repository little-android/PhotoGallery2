package com.codve.photogallery2;

import com.google.gson.annotations.SerializedName;

public class FlickrData {

    @SerializedName("photos")
    private FlickrPhotos mPhotos;

    public FlickrData(FlickrPhotos photos) {
        mPhotos = photos;
    }

    public FlickrPhotos getPhotos() {
        return mPhotos;
    }
}
