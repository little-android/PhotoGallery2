package com.codve.photogallery2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CropDownloader<T> extends HandlerThread {

    // 后台下载线程, 作为 Looper
    private static final String TAG = "CropDownloader";

    private static final int MESSAGE_DOWNLOAD = 0;

    private Boolean mQuitFlag = false;

    private Handler mRequestHandler;

    private ConcurrentMap<T, String> mRequestMap = new ConcurrentHashMap<>();

    private Handler mResponseHandler;

    private CropDownloadListener<T> mCropDownloadListener;

    public CropDownloader(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
    }

    @Override
    public boolean quit() {
        mQuitFlag = true;
        return super.quit();
    }

    // 把消息添加到后台线程的消息队列中去
    public void queueCrop(T target, String url) {
        if (url == null) {
            mRequestMap.remove(target);
        } else {
            Log.i(TAG, url);
            mRequestMap.put(target, url);
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target)
                    .sendToTarget();
        }
    }

    // 定义后台线程得到消息后应执行的任务
    @Override
    protected void onLooperPrepared() {
        mRequestHandler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    T target = (T) msg.obj;
                    handleRequest(target);
                }
            }
        };
    }

    // 下载图片
    // target 实际上是 placeHolder
    private void handleRequest(final T target) {
        try {
            final String url = mRequestMap.get(target);
            if (url == null) {
                return;
            }
            // 下载图片字节数据
            byte[] bitmapBytes = new FlickrFetcher().getUrlBytes(url);
            // 将字节转换为 位图
            final Bitmap bitmap = BitmapFactory
                    .decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            Log.i(TAG, "bit map created");

            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (url != null && mRequestMap.get(target).equals(url) && !mQuitFlag) {
                        mRequestMap.remove(target);
                        mCropDownloadListener.onCropDownloaded(target, bitmap);
                    }
                }
            });
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public interface CropDownloadListener<T> {
        void onCropDownloaded(T target, Bitmap bitmap);
    }

    public void setCropDownloadListener(CropDownloadListener<T> listener) {
        mCropDownloadListener = listener;
    }

    public void clearQueue() {
        mResponseHandler.removeMessages(MESSAGE_DOWNLOAD);
        mRequestMap.clear();
    }
}
