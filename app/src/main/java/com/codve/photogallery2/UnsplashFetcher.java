package com.codve.photogallery2;

import android.net.Uri;
import android.util.Log;

import com.codve.photogallery2.gson.NullStringToEmptyAdapterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class UnsplashFetcher extends Fetcher {
    private static final String TAG = "UnsplashFetcher";
    private static final String END_POINT = "https://unsplash.com/napi/";
    private static final String SEARCH_METHOD = "search";
    private static final String RANDOM_METHOD = "photos/random";

    public List<GalleryItem> fetchRandomPhotos() {
        String url = END_POINT + RANDOM_METHOD;
        Uri uri = Uri.parse(url).buildUpon()
                .appendQueryParameter("count", "30")
                .build();
        url = uri.toString();
        String jsonStr = "";

        try {
            jsonStr = getUrlString(url);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory())
                .create();

        Type type = new TypeToken<ArrayList<UnsplashItem>>(){}.getType();
        List<UnsplashItem> items = gson.fromJson(jsonStr, type);

        return convertUnsplash(items);
    }

    public List<GalleryItem> searchPhotos(String query) {
        String url = END_POINT + SEARCH_METHOD;
        Uri uri = Uri.parse(url).buildUpon()
                .appendQueryParameter("query", query)
                .appendQueryParameter("page", "1")
                .appendQueryParameter("per_page", "30")
                .build();
        url = uri.toString();
        String jsonStr = "";
        try {
            jsonStr = getUrlString(url);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory())
                .create();
        UnsplashData data = gson.fromJson(jsonStr, UnsplashData.class);
        UnsplashPhotos photos = data.getPhotos();
        List<UnsplashItem> items =  photos.getItems();
        return convertUnsplash(items);
    }

    private List<GalleryItem> convertUnsplash(List<UnsplashItem> items) {
        List<GalleryItem> galleryItems = new ArrayList<>();
        for (UnsplashItem item : items) {
            String url = item.getUrls().getThumb();
            if (url.length() != 0) { // 过滤 url 为空的图片
                GalleryItem tmp = new GalleryItem();
                tmp.setCaption(item.getTitle());
                tmp.setId(item.getId());
                tmp.setUrl(url);
                galleryItems.add(tmp);
            }
        }
        return galleryItems;
    }




}
