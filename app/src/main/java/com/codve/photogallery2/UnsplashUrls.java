package com.codve.photogallery2;

import com.google.gson.annotations.SerializedName;

public class UnsplashUrls {

    @SerializedName("thumb")
    private String mThumb;

    public String getThumb() {
        return mThumb;
    }

    public void setThumb(String thumb) {
        mThumb = thumb;
    }
}
