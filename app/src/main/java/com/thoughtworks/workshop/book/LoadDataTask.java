package com.thoughtworks.workshop.book;

import android.os.AsyncTask;

import static com.thoughtworks.workshop.book.Data.from;
import static com.thoughtworks.workshop.book.DataLoader.loadJSONData;

/**
 * Created by wxie on 9/24/14.
 */
public class LoadDataTask extends AsyncTask<String, Void, Data> {
    @Override
    protected Data doInBackground(String... params) {
        final String url = params[0];
        return from(loadJSONData(url));
    }
}
