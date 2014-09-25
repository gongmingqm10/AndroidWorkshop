package com.thoughtworks.workshop.book;

import android.os.Parcel;
import android.os.Parcelable;

import static android.text.TextUtils.join;

/**
 * Created by wxie on 9/18/14.
 */
public class Book implements Parcelable {

    private String title;
    private String image;
    private String author;
    private String publisher;
    private String publishDate;
    private String summary;
    private double rating;

    public Book() {
    }

    public Book(String title, String image, String author, String publisher, String publishDate, String summary, double rating) {
        this.title = title;
        this.image = image;
        this.author = author;
        this.publisher = publisher;
        this.publishDate = publishDate;
        this.summary = summary;
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getSummary() {
        return summary;
    }

    public String getInformation() {
        return join(" / ", new String[]{getAuthor(), getPublisher(), getPublishDate()});
    }

    public String getAuthor() {
        return author;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public double getRating() {
        return rating;
    }

    public static final Creator CREATOR = new Creator() {
        @Override
        public Book createFromParcel(Parcel source) {
            Book book = new Book();
            book.title = source.readString();
            book.image = source.readString();
            book.author = source.readString();
            book.publisher = source.readString();
            book.publishDate = source.readString();
            book.summary = source.readString();
            book.rating = source.readDouble();
            return book;
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(image);
        dest.writeString(author);
        dest.writeString(publisher);
        dest.writeString(publishDate);
        dest.writeString(summary);
        dest.writeDouble(rating);
    }
}
