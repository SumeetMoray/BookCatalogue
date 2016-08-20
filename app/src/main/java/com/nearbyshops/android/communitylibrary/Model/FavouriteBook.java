package com.nearbyshops.android.communitylibrary.Model;

/**
 * Created by sumeet on 8/8/16.
 */
public class FavouriteBook {

    // Table Name
    public static final String TABLE_NAME = "FAVOURITE_BOOK";

    // column Names
    public static final String MEMBER_ID = "MEMBER_ID"; // foreign Key
    public static final String BOOK_ID = "BOOK_ID"; // foreign Key
    public static final String IS_FAVOURITE = "IS_FAVOURITE";


    // Create Table Statement
    public static final String createTableFavouriteBookPostgres = "CREATE TABLE IF NOT EXISTS "
            + FavouriteBook.TABLE_NAME + "("

            + " " + FavouriteBook.MEMBER_ID + " INT,"
            + " " + FavouriteBook.BOOK_ID + " INT,"
            + " " + FavouriteBook.IS_FAVOURITE + " boolean,"

            + " FOREIGN KEY(" + FavouriteBook.MEMBER_ID +") REFERENCES " + Member.TABLE_NAME + "(" + Member.MEMBER_ID + "),"
            + " FOREIGN KEY(" + FavouriteBook.BOOK_ID +") REFERENCES " + Book.TABLE_NAME + "(" + Book.BOOK_ID + "),"
            + " PRIMARY KEY (" + FavouriteBook.MEMBER_ID + ", " + FavouriteBook.BOOK_ID + ")"
            + ")";


    // instance Variables

    Integer memberID;
    Integer bookID;

    // Getter and Setter


    public Integer getMemberID() {
        return memberID;
    }

    public void setMemberID(Integer memberID) {
        this.memberID = memberID;
    }

    public Integer getBookID() {
        return bookID;
    }

    public void setBookID(Integer bookID) {
        this.bookID = bookID;
    }
}
