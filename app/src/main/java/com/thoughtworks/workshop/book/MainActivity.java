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

import java.util.List;

import static com.thoughtworks.workshop.book.DataLoader.loadJSONData;

public class MainActivity extends Activity {
    private static final String TAG = "Main";

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Data data = Data.from(loadJSONData(this));
        mListView = (ListView) findViewById(android.R.id.list);
        mListView.setAdapter(new MyArrayAdapter(this, data.getBookArray()));
    }

    static class MyArrayAdapter extends ArrayAdapter<Book> {

        public MyArrayAdapter(Context context, List<Book> books) {
            super(context, 0, books);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = inflater.inflate(R.layout.list_item_book, parent, false);

            TextView title = (TextView) view.findViewById(R.id.title);
            TextView information = (TextView) view.findViewById(R.id.information);
            TextView summary = (TextView) view.findViewById(R.id.summary);
            ImageView image = (ImageView) view.findViewById(R.id.thumbnail);
            RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rating);
            TextView ratingVal = (TextView) view.findViewById(R.id.ratingValue);

            Book data = getItem(position);

            title.setText(data.getTitle());
            summary.setText(data.getSummary());
            information.setText(data.getInformation());
            ratingBar.setRating((float) (data.getRating() / 2));
            ratingVal.setText(String.valueOf(data.getRating()));
            image.setImageBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_default_cover));

            return view;
        }
    }
}
