package com.nearbyshops.android.communitylibrary.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Timestamp;

/**
 * Created by sumeet on 8/8/16.
 */
public class Book implements Parcelable{


    // Table Name
    public static final String TABLE_NAME = "BOOK";

    // column Names
    public static final String BOOK_ID = "BOOK_ID";
    public static final String BOOK_CATEGORY_ID = "BOOK_CATEGORY_ID"; // foreign Key
    public static final String BOOK_NAME = "BOOK_NAME";
    public static final String BOOK_COVER_IMAGE_URL = "BOOK_COVER_IMAGE_URL";
    public static final String BACKDROP_IMAGE_URL = "BACKDROP_IMAGE_URL";
    public static final String AUTHOR_NAME = "AUTHOR_NAME";
    public static final String BOOK_DESCRIPTION = "BOOK_DESCRIPTION";
    public static final String TIMESTAMP_CREATED = "TIMESTAMP_CREATED";
    public static final String TIMESTAMP_UPDATED = "TIMESTAMP_UPDATED";


    // CreateTableStatement

    public static final String createTableBookPostgres = "CREATE TABLE IF NOT EXISTS "
            + Book.TABLE_NAME + "("

            + " " + Book.BOOK_ID + " SERIAL PRIMARY KEY,"
            + " " + Book.BOOK_CATEGORY_ID + " INT,"
            + " " + Book.BOOK_NAME + " VARCHAR(100),"
            + " " + Book.BOOK_COVER_IMAGE_URL + " VARCHAR(100),"
            + " " + Book.BACKDROP_IMAGE_URL + " VARCHAR(100),"
            + " " + Book.AUTHOR_NAME + " VARCHAR(100),"
            + " " + Book.BOOK_DESCRIPTION + " VARCHAR(10000),"
            + " " + Book.TIMESTAMP_CREATED + "  timestamp with time zone NOT NULL DEFAULT now(),"
            + " " + Book.TIMESTAMP_UPDATED + " timestamp with time zone,"

            + " FOREIGN KEY(" + Book.BOOK_CATEGORY_ID +") REFERENCES "
            + BookCategory.TABLE_NAME + "(" + BookCategory.BOOK_CATEGORY_ID + ")"
            + ")";



    //Instance Variables

    private int bookID;
    private int bookCategoryID;
    private String bookName;
    private String bookCoverImageURL;
    private String backdropImageURL;
    private String authorName;
    private String bookDescription;
    private Timestamp timestampCreated;
    private Timestamp timeStampUpdated;


    private float rt_rating_avg;
    private float rt_rating_count;

    protected Book(Parcel in) {
        bookID = in.readInt();
        bookCategoryID = in.readInt();
        bookName = in.readString();
        bookCoverImageURL = in.readString();
        backdropImageURL = in.readString();
        authorName = in.readString();
        bookDescription = in.readString();
        rt_rating_avg = in.readFloat();
        rt_rating_count = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(bookID);
        dest.writeInt(bookCategoryID);
        dest.writeString(bookName);
        dest.writeString(bookCoverImageURL);
        dest.writeString(backdropImageURL);
        dest.writeString(authorName);
        dest.writeString(bookDescription);
        dest.writeFloat(rt_rating_avg);
        dest.writeFloat(rt_rating_count);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    public float getRt_rating_avg() {
        return rt_rating_avg;
    }

    public void setRt_rating_avg(float rt_rating_avg) {
        this.rt_rating_avg = rt_rating_avg;
    }

    public float getRt_rating_count() {
        return rt_rating_count;
    }

    public void setRt_rating_count(float rt_rating_count) {
        this.rt_rating_count = rt_rating_count;
    }

    public Book() {
    }



    // Getter and Setters




    public Integer getBookID() {
        return bookID;
    }

    public void setBookID(Integer bookID) {
        this.bookID = bookID;
    }

    public Integer getBookCategoryID() {
        return bookCategoryID;
    }

    public void setBookCategoryID(Integer bookCategoryID) {
        this.bookCategoryID = bookCategoryID;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookCoverImageURL() {
        return bookCoverImageURL;
    }

    public void setBookCoverImageURL(String bookCoverImageURL) {
        this.bookCoverImageURL = bookCoverImageURL;
    }

    public String getBackdropImageURL() {
        return backdropImageURL;
    }

    public void setBackdropImageURL(String backdropImageURL) {
        this.backdropImageURL = backdropImageURL;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getBookDescription() {
        return bookDescription;
    }

    public void setBookDescription(String bookDescription) {
        this.bookDescription = bookDescription;
    }

    public Timestamp getTimestampCreated() {
        return timestampCreated;
    }

    public void setTimestampCreated(Timestamp timestampCreated) {
        this.timestampCreated = timestampCreated;
    }

    public Timestamp getTimeStampUpdated() {
        return timeStampUpdated;
    }

    public void setTimeStampUpdated(Timestamp timeStampUpdated) {
        this.timeStampUpdated = timeStampUpdated;
    }
}
