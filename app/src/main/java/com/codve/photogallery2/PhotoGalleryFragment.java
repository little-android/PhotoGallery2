package com.codve.photogallery2;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;

public class PhotoGalleryFragment extends Fragment {

    private static final String TAG = "PhotoGalleryFragment";

    private RecyclerView mPhotoRecyclerView;

    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchItemsTask().execute();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        mPhotoRecyclerView = (RecyclerView) view.findViewById(R.id.photo_recycler_view);
        // 使用网格布局, 每行 3  个
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        return view;
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                String result = new FlickrFetcher()
                        .getUrlString("https://www.baidu.com");
                Log.i(TAG, result);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
            }
            return null;
        }
    }
}
