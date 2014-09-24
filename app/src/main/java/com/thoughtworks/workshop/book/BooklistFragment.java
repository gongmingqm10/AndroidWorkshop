package com.thoughtworks.workshop.book;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import static com.thoughtworks.workshop.book.Data.from;
import static com.thoughtworks.workshop.book.DataLoader.loadJSONData;

/**
 * Created by wxie on 9/22/14.
 */
public class BooklistFragment extends Fragment {

    private static final String TAG = "BooklistFragment";

    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_list, container, false);

        listView = (ListView) view.findViewById(android.R.id.list);

        new AsyncTask<String, Void, Data>() {

            @Override
            protected Data doInBackground(String... params) {
                final String url = params[0];
                return from(loadJSONData(url));
            }

            @Override
            protected void onPostExecute(Data data) {
                super.onPostExecute(data);
                listView.setAdapter(new MyArrayAdapter(getActivity(), data.getBookArray()));
            }
        }.execute("https://api.douban.com/v2/book/search?tag=%E7%BC%96%E7%A8%8B");

        return view;
    }

    static class MyArrayAdapter extends ArrayAdapter<Book> {
        private LayoutInflater inflater;

        public MyArrayAdapter(Context context, List<Book> books) {
            super(context, 0, books);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.list_item_book, parent, false);

                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.information = (TextView) convertView.findViewById(R.id.information);
                holder.summary = (TextView) convertView.findViewById(R.id.summary);
                holder.image = (ImageView) convertView.findViewById(R.id.thumbnail);
                holder.ratingBar = (RatingBar) convertView.findViewById(R.id.rating);
                holder.ratingVal = (TextView) convertView.findViewById(R.id.ratingValue);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final Book data = getItem(position);

            holder.title.setText(data.getTitle());
            holder.summary.setText(data.getSummary());
            holder.information.setText(data.getInformation());
            holder.ratingBar.setRating((float) (data.getRating() / 2));
            holder.ratingVal.setText(String.valueOf(data.getRating()));
            Picasso.with(getContext()).load(data.getImage()).into(holder.image);
            return convertView;
        }

        static class ViewHolder {
            TextView title;
            TextView information;
            TextView summary;
            ImageView image;
            RatingBar ratingBar;
            TextView ratingVal;
        }
    }
}
