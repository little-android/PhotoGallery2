package com.codve.photogallery2;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FlickrFetcher {

    private static final String TAG = "FlickrFetcher";
    private static final String API_KEY = "f21e87e7a6beb65edab0dde2b1888b9e";

    public byte[] getUrlBytes(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        try{
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = conn.getInputStream();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(conn.getResponseMessage());
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            conn.disconnect();
        }
    }

    public String getUrlString(String urlStr) throws IOException {
        return new String(getUrlBytes(urlStr));
    }

// 构造请求 URL: https://api.flickr.com/services/rest/?method=flickr.photos.getRecent&api_key=f21e87e7a6beb65edab0dde2b1888b9e&format=json&nojsoncallback=1&extras=url_s
    public List<GalleryItem> fetchItems() {

        List<GalleryItem> items = new ArrayList<>();

        try {
            String url = Uri.parse("https://api.flickr.com/services/rest/")
                    .buildUpon()
                    .appendQueryParameter("method", "flickr.photos.getRecent")
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("extras", "url_s")
                    .build().toString();
            String jsonString = getUrlString(url);
            Gson gson = new Gson();
            FlickrData data = gson.fromJson(jsonString, FlickrData.class);
            Log.i(TAG, jsonString);
            items = data.getPhotos().getPhotos();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return items;
    }
}
