package com.thoughtworks.workshop.book;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.thoughtworks.workshop.book.imageloader.ImageLoader;
import com.thoughtworks.workshop.book.model.Book;

import java.util.List;

public class BookListAdapter extends ArrayAdapter<Book> {
    
    public BookListAdapter(Context context, List<Book> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_book, parent, false);

            holder = new ViewHolder();
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

        holder.populate(getItem(position));

        return convertView;
    }

    private class ViewHolder{
        TextView title;
        TextView information;
        TextView summary;
        ImageView image;
        RatingBar ratingBar;
        TextView ratingVal;

        public void populate(Book book) {
            title.setText(book.getTitle());
            summary.setText(book.getSummary());
            information.setText(book.getInformation());
            ratingBar.setRating((float) (book.getRating() / 2));
            ratingVal.setText(String.valueOf(book.getRating()));
//            image.setImageResource(R.drawable.ic_default_cover);

            ImageLoader.getInstance().loadImage(image, book.getImage());

        }
    }
}
