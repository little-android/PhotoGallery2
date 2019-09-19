package com.codve.photogallery2;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PhotoGalleryFragment extends Fragment {

    private static final String TAG = "PhotoGalleryFragment";

    private RecyclerView mPhotoRecyclerView;

    private List<GalleryItem> mItems = new ArrayList<>();

    private CropDownloader<PhotoHolder> mCropDownloader;

    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        StrictMode.enableDefaults();

        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchItemsTask().execute();

        // 后台下载线程启动
        Handler responseHandler = new Handler();
        mCropDownloader = new CropDownloader<>(responseHandler);
        mCropDownloader.setCropDownloadListener(
                new CropDownloader.CropDownloadListener<PhotoHolder>() {
                    @Override
                    public void onCropDownloaded(PhotoHolder photoHolder, Bitmap bitmap) {
                        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                        photoHolder.bindDrawable(drawable);
                    }
                }
        );


        mCropDownloader.start();
        mCropDownloader.getLooper();
        Log.i(TAG, "Background thread started");
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

    private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>> {
        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
            return new FlickrFetcher().fetchItems();
        }

        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            mItems = items; // 内部类可以访问到外部类的私有域
            setupAdapter(); // 获取数据, 绑定 adapter
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder {
        private ImageView mItemImageView;

        public PhotoHolder(@NonNull View itemView) {
            super(itemView);
            mItemImageView = (ImageView) itemView.findViewById(R.id.item_image_view);
        }

        // 设置缩略图
        public void bindDrawable(Drawable drawable) {
            mItemImageView.setImageDrawable(drawable);
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

        private List<GalleryItem> mGalleryItems;

        public PhotoAdapter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }

        @NonNull
        @Override
        public PhotoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            // 实例化布局
            View view = inflater.inflate(R.layout.list_item_gallery, parent, false);
            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PhotoHolder photoHolder, int position) {
            GalleryItem galleryItem = mGalleryItems.get(position);
            // 设置占位符
            Drawable placeHolder = getResources().getDrawable(R.drawable.bill_up_close);
            photoHolder.bindDrawable(placeHolder);
            // 下载缩略图并渲染视图
            mCropDownloader.queueCrop(photoHolder, galleryItem.getUrl());
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }

    private void setupAdapter() {
        if (isAdded()) {
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mCropDownloader.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 视图关闭后, 一定要结束后台线程. 否则后台进程就会成为僵尸
        mCropDownloader.quit();
        Log.i(TAG, "Background thread destroyed");
    }
}
