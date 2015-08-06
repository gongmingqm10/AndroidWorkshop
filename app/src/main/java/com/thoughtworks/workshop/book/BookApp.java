package com.thoughtworks.workshop.book;

import android.app.Application;

import com.thoughtworks.workshop.book.imageloader.ImageLoader;

public class BookApp extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        ImageLoader.init(this);
    }
}
