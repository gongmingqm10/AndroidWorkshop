package com.thoughtworks.workshop.book.imageloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.ImageView;

import java.io.IOException;
import java.net.URL;

public class ImageLoader {

    private static final int IMAGE_LOAD_MESSAGE = 100;
    private ImageView currentImageView;


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

        if (TextUtils.isEmpty(urlString)) return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlString);
                    Bitmap imageBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());


                    currentImageView = imageView;

                    Message message = new Message();
                    message.what = IMAGE_LOAD_MESSAGE;
                    message.obj = imageBitmap;
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
            if (msg.what == IMAGE_LOAD_MESSAGE && msg.obj != null) {
                currentImageView.setImageBitmap((Bitmap)msg.obj);
            }
        }
    };

}
