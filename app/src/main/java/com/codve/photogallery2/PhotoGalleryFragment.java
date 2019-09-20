package com.codve.photogallery2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PhotoGalleryFragment extends Fragment {

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
        setHasOptionsMenu(true);
        setRetainInstance(true);
        updateItems();

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

        Intent intent = PollService.newIntent(getActivity());
        getActivity().startService(intent);

        PollService.setServiceAlarm(getActivity(), true);
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
        private String mQuery;

        public FetchItemsTask(String query) {
            mQuery = query;
        }

        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
            UnsplashFetcher fetcher = new UnsplashFetcher();
            List<GalleryItem> items;
            if (mQuery == null) {
                items = fetcher.fetchRandomPhotos();
            } else {
                items = fetcher.searchPhotos(mQuery);
            }
            for (GalleryItem item : items) {
                String url = item.getUrl();
                if (url == null || url.length() == 0) {
                    items.remove(item);
                }
            }
            return items;
        }

        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            mItems = items; // 内部类可以访问到外部类的私有域
            setupAdapter(); // 获取数据, 绑定 adapter
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener{
        private ImageView mItemImageView;

        public PhotoHolder(@NonNull View itemView) {
            super(itemView);
            mItemImageView = (ImageView) itemView.findViewById(R.id.item_image_view);
            itemView.setOnClickListener(this);
        }

        // 设置缩略图
        public void bindDrawable(Drawable drawable) {
            mItemImageView.setImageDrawable(drawable);
        }

        @Override
        public void onClick(View view) {
            Intent intent = PhotoPageActivity
                    .newIntent(getActivity(), Uri.parse("https://www.unsplash.com"));
            startActivity(intent);
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
            Drawable placeHolder = getResources().getDrawable(R.drawable.photo_placeholder);
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
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_photo_gallery, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                QueryPrefer.setStoredQuery(getActivity(), query);
                updateItems();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_clear:
                QueryPrefer.setStoredQuery(getActivity(), null);
                updateItems();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateItems() {
        String query = QueryPrefer.getStoredQuery(getActivity());
        new FetchItemsTask(query).execute();
    }

}
