package com.nearbyshops.android.communitylibrary.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Timestamp;

/**
 * Created by sumeet on 8/8/16.
 */
public class BookMeetup implements Parcelable{

    // Table Name

    public static final String TABLE_NAME = "BOOK_MEETUP";

    // column Names


    public static final String BOOK_MEETUP_ID = "BOOK_MEETUP_ID";
    public static final String VENUE = "VENUE";
    public static final String LONGITUDE = "LONGITUDE";
    public static final String LATITUDE = "LATITUDE";
    public static final String MEETUP_NAME = "MEETUP_NAME";
    public static final String MEETUP_PURPOSE = "MEETUP_PURPOSE";
    public static final String POSTER_URL = "POSTER_URL";


    // publisher, pages, isbn, language, category, Title

    // Sort Options : Sort By
    // 1. Date of Publish (Newer First) : 2. Date of Publish (Older First)
    // 3. Book Rating 4. Title

    // Filter Options : Filter By
    // 1. Language 2. Publisher 3. Date of Publish [Start - End]



    // CreateTableStatement

    public static final String createTableBookMeetupPostgres = "CREATE TABLE IF NOT EXISTS "
            + BookMeetup.TABLE_NAME + "("
            + " " + BookMeetup.BOOK_MEETUP_ID + " SERIAL PRIMARY KEY,"
            + " " + BookMeetup.VENUE + " text,"
            + " " + BookMeetup.LATITUDE + " FLOAT,"
            + " " + BookMeetup.LONGITUDE + " FLOAT,"
            + " " + BookMeetup.MEETUP_NAME + " TEXT,"
            + " " + BookMeetup.MEETUP_PURPOSE + " TEXT,"
            + " " + BookMeetup.POSTER_URL + " TEXT"
            + ")";

    public BookMeetup() {
    }


    //Instance Variables


    private int bookMeetupID;
    private String venue;
    private double latitude;
    private double longitude;
    private String meetupName;
    private String meetupPurpose;
    private String posterURL;

    private double rt_distance;

    // Getter and Setters


    protected BookMeetup(Parcel in) {
        bookMeetupID = in.readInt();
        venue = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        meetupName = in.readString();
        meetupPurpose = in.readString();
        posterURL = in.readString();
        rt_distance = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(bookMeetupID);
        dest.writeString(venue);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(meetupName);
        dest.writeString(meetupPurpose);
        dest.writeString(posterURL);
        dest.writeDouble(rt_distance);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BookMeetup> CREATOR = new Creator<BookMeetup>() {
        @Override
        public BookMeetup createFromParcel(Parcel in) {
            return new BookMeetup(in);
        }

        @Override
        public BookMeetup[] newArray(int size) {
            return new BookMeetup[size];
        }
    };

    public String getPosterURL() {
        return posterURL;
    }

    public void setPosterURL(String posterURL) {
        this.posterURL = posterURL;
    }

    public Integer getBookMeetupID() {
        return bookMeetupID;
    }

    public void setBookMeetupID(Integer bookMeetupID) {
        this.bookMeetupID = bookMeetupID;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getMeetupName() {
        return meetupName;
    }

    public void setMeetupName(String meetupName) {
        this.meetupName = meetupName;
    }

    public String getMeetupPurpose() {
        return meetupPurpose;
    }

    public void setMeetupPurpose(String meetupPurpose) {
        this.meetupPurpose = meetupPurpose;
    }

    public Double getRt_distance() {
        return rt_distance;
    }

    public void setRt_distance(Double rt_distance) {
        this.rt_distance = rt_distance;
    }
}
