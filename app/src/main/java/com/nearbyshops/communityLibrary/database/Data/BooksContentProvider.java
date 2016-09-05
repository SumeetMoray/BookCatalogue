package com.nearbyshops.communityLibrary.database.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.nearbyshops.communityLibrary.database.Model.Book;

/**
 * Created by sumeet on 7/3/16.
 */
public class BooksContentProvider extends ContentProvider{

    BooksDBHelper booksDbHelper;


    public static final int BOOK_ITEM_LIST = 1;
    public static final int BOOK_ITEM_ID = 2;

    private static final UriMatcher URI_MATCHER;

    static{

        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(LibraryDBContract.AUTHORITY,Book.TABLE_NAME,BOOK_ITEM_LIST);
//        URI_MATCHER.addURI(MoviesContract.AUTHORITY, Book.TABLE_NAME +"/#",BOOK_ITEM_ID);


    }



    @Nullable
    @Override
    public String getType(Uri uri) {


        switch (URI_MATCHER.match(uri))
        {
            case BOOK_ITEM_LIST:

                return LibraryDBContract.BookContract.CONTENT_TYPE;

            case BOOK_ITEM_ID:

                return LibraryDBContract.BookContract.CONTENT_ITEM_TYPE;


            default:

                return null;
        }



    }



    @Override
    public boolean onCreate() {

        booksDbHelper = new BooksDBHelper(getContext());

        return true;
    }




    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = booksDbHelper.getWritableDatabase();

        Cursor cursor = null;

        switch (URI_MATCHER.match(uri))
        {
            case BOOK_ITEM_LIST:

                cursor =  db.query(Book.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);

                break;

            case BOOK_ITEM_ID:

                cursor = db.query(Book.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);

                break;


            default:
                break;
        }


        return cursor;
    }




    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase db = booksDbHelper.getWritableDatabase();

        long id = 0;

        if(URI_MATCHER.match(uri) == BOOK_ITEM_LIST)
        {

//            id = db.insert(Book.TABLE_NAME,null,values);
        }



        id = db.insertWithOnConflict(Book.TABLE_NAME,null,values,SQLiteDatabase.CONFLICT_REPLACE);

        getContext().getContentResolver().notifyChange(uri,null);

        return ContentUris.withAppendedId(uri,id);
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase db = booksDbHelper.getWritableDatabase();

        int delCount = db.delete(Book.TABLE_NAME,selection,selectionArgs);

        switch (URI_MATCHER.match(uri))
        {

            case BOOK_ITEM_LIST:

                break;

            case BOOK_ITEM_ID:

                break;

        }

        getContext().getContentResolver().notifyChange(uri,null);


        return delCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase db = booksDbHelper.getWritableDatabase();

        int rowsUpdated = db.update(Book.TABLE_NAME,values,selection,selectionArgs);


        getContext().getContentResolver().notifyChange(uri,null);

        return rowsUpdated;
    }

}
