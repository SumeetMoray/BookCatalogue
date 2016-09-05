package com.nearbyshops.communityLibrary.database.Model;

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

    // ! ---- To Be Introduced

    public static final String DATE_OF_PUBLISH = "DATE_OF_PUBLISH";
    public static final String DATE_OF_PUBLISH_LONG = "DATE_LONG";

    public static final String PUBLISHER_NAME = "PUBLISHER_NAME";
    public static final String PAGES_TOTAL = "PAGES_TOTAL";
    public static final String ISBN = "ISBN";
    public static final String ISO_LANGUAGE_CODE = "ISO_LANGUAGE_CODE";

    // publisher, pages, isbn, language, category, Title

    // Sort Options : Sort By
    // 1. Date of Publish (Newer First) : 2. Date of Publish (Older First)
    // 3. Book Rating 4. Title

    // Filter Options : Filter By
    // 1. Language 2. Publisher 3. Date of Publish [Start - End]



    // CreateTableStatement

    public static final String createTableBookPostgres = "CREATE TABLE IF NOT EXISTS "
            + Book.TABLE_NAME + "("

            + " " + Book.BOOK_ID + " SERIAL PRIMARY KEY,"
            + " " + Book.BOOK_CATEGORY_ID + " INT,"
            + " " + Book.BOOK_NAME + " VARCHAR(500),"

            + " " + Book.BOOK_COVER_IMAGE_URL + " VARCHAR(100),"
            + " " + Book.BACKDROP_IMAGE_URL + " VARCHAR(100),"
            + " " + Book.AUTHOR_NAME + " VARCHAR(500),"

            + " " + Book.BOOK_DESCRIPTION + " VARCHAR(10000),"
            + " " + Book.TIMESTAMP_CREATED + "  timestamp with time zone NOT NULL DEFAULT now(),"
            + " " + Book.TIMESTAMP_UPDATED + " timestamp with time zone,"

            + " " + Book.DATE_OF_PUBLISH + " timestamp with time zone,"
            + " " + Book.PUBLISHER_NAME + " VARCHAR(500),"
            + " " + Book.PAGES_TOTAL + " INT,"

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

    private Timestamp dateOfPublish;
    private long dateOfPublishInMillis;


    private String nameOfPublisher;
    private int pagesTotal;

    private float rt_rating_avg;
    private float rt_rating_count;

    public static final String RT_RATING_AVG = "RT_RATING_AVG";
    public static final String RT_RATING_COUNT = "RT_RATING_COUNT";

    public Book() {
    }

    protected Book(Parcel in) {

        if(dateOfPublish!=null)
        {
            dateOfPublishInMillis = dateOfPublish.getTime();
        }

        bookID = in.readInt();
        bookCategoryID = in.readInt();
        bookName = in.readString();
        bookCoverImageURL = in.readString();
        backdropImageURL = in.readString();
        authorName = in.readString();
        bookDescription = in.readString();
        dateOfPublishInMillis = in.readLong();
        nameOfPublisher = in.readString();
        pagesTotal = in.readInt();
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
        dest.writeLong(dateOfPublishInMillis);
        dest.writeString(nameOfPublisher);
        dest.writeInt(pagesTotal);
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

    public Timestamp getDateOfPublish() {
        return dateOfPublish;
    }

    public void setDateOfPublish(Timestamp dateOfPublish) {
        this.dateOfPublish = dateOfPublish;
    }

// Getter and Setters


    public long getDateOfPublishInMillis() {
        return dateOfPublishInMillis;
    }

    public void setDateOfPublishInMillis(long dateOfPublishInMillis) {
        this.dateOfPublishInMillis = dateOfPublishInMillis;
    }

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

    public int getBookID() {
        return bookID;
    }

    public void setBookID(int bookID) {
        this.bookID = bookID;
    }

    public int getBookCategoryID() {
        return bookCategoryID;
    }

    public void setBookCategoryID(int bookCategoryID) {
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


    public String getNameOfPublisher() {
        return nameOfPublisher;
    }

    public void setNameOfPublisher(String nameOfPublisher) {
        this.nameOfPublisher = nameOfPublisher;
    }

    public int getPagesTotal() {
        return pagesTotal;
    }

    public void setPagesTotal(int pagesTotal) {
        this.pagesTotal = pagesTotal;
    }
}
