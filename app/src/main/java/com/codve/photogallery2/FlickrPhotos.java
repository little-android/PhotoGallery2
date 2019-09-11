package com.codve.photogallery2;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FlickrPhotos {

    @SerializedName("photo")
    private List<GalleryItem> photos;

    public FlickrPhotos(List<GalleryItem> photo) {
        this.photos = photo;
    }

    public List<GalleryItem> getPhotos() {
        return photos;
    }
}
