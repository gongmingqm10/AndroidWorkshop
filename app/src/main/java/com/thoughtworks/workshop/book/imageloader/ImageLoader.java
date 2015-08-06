package com.thoughtworks.workshop.book.imageloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.LruCache;
import android.widget.ImageView;

import com.thoughtworks.workshop.book.R;

import java.io.IOException;
import java.net.URL;

public class ImageLoader {

    private final int IMAGE_LOAD_SUCCESS = 200;
    private final int IMAGE_LOAD_ERROR = 400;

    private static ImageLoader instance;

    private ImageView currentImageView;
    private LruCache<String, Bitmap> memoryCache;

    private ImageLoader() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        memoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public static ImageLoader getInstance() {
        if (instance == null) {
            instance = new ImageLoader();
        }
        return instance;
    }

    public void loadImage(final ImageView imageView, final String urlString) {

        if (TextUtils.isEmpty(urlString) || imageView == null) return;

        Bitmap memoryBitmap = memoryCache.get(urlString);
        if (memoryBitmap == null) {
            loadImageFromURL(imageView, urlString);
        } else {
            imageView.setImageBitmap(memoryBitmap);
        }
    }

    private void loadImageFromURL(final ImageView imageView, final String urlString) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlString);
                    Bitmap imageBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                    if (imageBitmap == null) {
                        throw new IOException();
                    } else {
                        memoryCache.put(urlString, imageBitmap);
                    }

                    currentImageView = imageView;
                    currentImageView.setTag(urlString);

                    Message message = new Message();
                    message.what = IMAGE_LOAD_SUCCESS;
                    message.obj = urlString;
                    handler.sendMessage(message);

                } catch (IOException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(IMAGE_LOAD_ERROR);
                }
            }
        }).start();
    }

    private Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case IMAGE_LOAD_SUCCESS:
                    String key = (String) msg.obj;
                    Bitmap targetBitmap = memoryCache.get(key);
                    if (targetBitmap != null && key.equals(currentImageView.getTag())) {
                        currentImageView.setImageBitmap(targetBitmap);
                    } else {
                        currentImageView.setImageResource(R.drawable.ic_default_cover);
                    }
                    break;
                case IMAGE_LOAD_ERROR:
                    currentImageView.setImageResource(R.drawable.ic_default_cover);
                    break;
            }
        }
    };

}
