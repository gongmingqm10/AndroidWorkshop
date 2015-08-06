package com.thoughtworks.workshop.book.imageloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.ImageView;

import com.thoughtworks.workshop.book.R;

import java.io.IOException;
import java.net.URL;

public class ImageLoader {

    private static final int IMAGE_LOAD_MESSAGE = 100;
    private ImageView currentImageView;

    private final String KEY_URL = "url";
    private final String KEY_BITMAP = "bitmap";

    private static ImageLoader instance;

    private ImageLoader() {
    }

    public static ImageLoader getInstance() {
        if (instance == null) {
            instance = new ImageLoader();
        }
        return instance;
    }

    public void loadImage(final ImageView imageView, final String urlString) {

        if (TextUtils.isEmpty(urlString) || imageView == null) return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlString);
                    Bitmap imageBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                    if (imageBitmap == null) return;

                    currentImageView = imageView;
                    currentImageView.setTag(urlString);

                    Bundle bundle = new Bundle();
                    bundle.putString(KEY_URL, urlString);
                    bundle.putParcelable(KEY_BITMAP, imageBitmap);

                    Message message = new Message();
                    message.what = IMAGE_LOAD_MESSAGE;
                    message.setData(bundle);
                    handler.sendMessage(message);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            Bundle data = msg.getData();
            if (msg.what == IMAGE_LOAD_MESSAGE && data != null) {
                if (currentImageView.getTag().equals(data.getString(KEY_URL))) {
                    currentImageView.setImageBitmap((Bitmap)data.getParcelable(KEY_BITMAP));
                } else {
                    currentImageView.setImageResource( R.drawable.ic_default_cover);
                }
            }
        }
    };

}
