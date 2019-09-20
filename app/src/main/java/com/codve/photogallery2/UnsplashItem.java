package com.codve.photogallery2;

import com.google.gson.annotations.SerializedName;

public class UnsplashItem {

    @SerializedName("id")
    private String mId;

    @SerializedName("alt_description")
    private String mTitle;

    @SerializedName("urls")
    private UnsplashUrls mUrls;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public UnsplashUrls getUrls() {
        return mUrls;
    }

    public void setUrls(UnsplashUrls urls) {
        mUrls = urls;
    }
}
