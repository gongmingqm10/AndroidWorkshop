package com.thoughtworks.workshop.book;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.thoughtworks.workshop.book.model.Book;
import com.thoughtworks.workshop.book.model.BookWrapper;

import java.util.List;

import static com.thoughtworks.workshop.book.util.DataLoader.loadJSONData;

public class MainActivity extends Activity {
    private static final String TAG = "Main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BookWrapper bookWrapper = BookWrapper.from(loadJSONData(this));

        ListView mListView = (ListView) findViewById(android.R.id.list);
        mListView.setAdapter(new BookListAdapter(this, bookWrapper.getBookList()));
    }
}
