package com.nearbyshops.communityLibrary.database.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.nearbyshops.communityLibrary.database.Model.Book;
import com.nearbyshops.communityLibrary.database.Model.BookCategory;

/**
 * Created by sumeet on 7/3/16.
 */
public class BooksDBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "booksDb";
    public static final int DB_VERSION = 1;


    public BooksDBHelper(Context context) {
        super(context,DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        // CreateTableStatement
        String createBookCategory = "CREATE TABLE IF NOT EXISTS "
                + BookCategory.TABLE_NAME + "("

                + " " + BookCategory.BOOK_CATEGORY_ID + " INTEGER PRIMARY KEY,"
                + " " + BookCategory.BOOK_CATEGORY_NAME + " text,"
                + " " + BookCategory.IMAGE_URL + " text,"
                + " " + BookCategory.BACKDROP_IMAGE_URL + " text,"
                + " " + BookCategory.PARENT_CATEGORY_ID + " INTEGER "
                + ")";



        String createTableBook = "CREATE TABLE IF NOT EXISTS "
                + Book.TABLE_NAME + "("

                + " " + Book.BOOK_ID + " INTEGER PRIMARY KEY,"
                + " " + Book.BOOK_CATEGORY_ID + " INTEGER,"
                + " " + Book.BOOK_NAME + " text,"

                + " " + Book.BOOK_COVER_IMAGE_URL + " text,"
                + " " + Book.BACKDROP_IMAGE_URL + " text,"
                + " " + Book.AUTHOR_NAME + " text,"

                + " " + Book.BOOK_DESCRIPTION + " text,"
                + " " + Book.TIMESTAMP_CREATED + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + " " + Book.TIMESTAMP_UPDATED + " timestamp,"

                + " " + Book.DATE_OF_PUBLISH + " timestamp,"
                + " " + Book.DATE_OF_PUBLISH_LONG + " long,"
                + " " + Book.PUBLISHER_NAME + " text,"
                + " " + Book.PAGES_TOTAL + " INTEGER,"

                + " " + Book.RT_RATING_COUNT + " INTEGER,"
                + " " + Book.RT_RATING_AVG + " REAL"
                + ")";


        db.execSQL(createBookCategory);
        db.execSQL(createTableBook);

    }




    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + Book.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + BookCategory.TABLE_NAME);

        onCreate(db);
    }

}
