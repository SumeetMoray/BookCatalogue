package com.nearbyshops.android.communitylibrary.Data;

import android.content.ContentResolver;
import android.net.Uri;

import com.nearbyshops.android.communitylibrary.Model.Book;

/**
 * Created by sumeet on 6/3/16.
 */
public class LibraryDBContract {


    public static final String AUTHORITY =
            "org.nearbyshops.communitylibrary";

    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY);



    public static final class BookContract
    {

        // constants and contract for content provider

        public static final String CONTENT_TYPE= ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/vnd.org.nearbyshops.communitylibrary.book";

        public static final String CONTENT_ITEM_TYPE= ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/vnd.org.nearbyshops.communitylibrary.bookcategory";


        public static final String[] PROJECTION_ALL =
                {
                        Book.BOOK_ID,
                        Book.BOOK_CATEGORY_ID,
                        Book.BOOK_NAME,

                        Book.BOOK_COVER_IMAGE_URL,
                        Book.BACKDROP_IMAGE_URL,
                        Book.AUTHOR_NAME,

                        Book.BOOK_DESCRIPTION,
                        Book.TIMESTAMP_CREATED,
                        Book.TIMESTAMP_UPDATED,

                        Book.DATE_OF_PUBLISH,
                        Book.PUBLISHER_NAME,
                        Book.PAGES_TOTAL,

                        Book.RT_RATING_COUNT,
                        Book.RT_RATING_AVG
                };


        public static final String SORT_ORDER_DEFAULT =
                Book.BOOK_ID + " ASC";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(LibraryDBContract.CONTENT_URI,Book.TABLE_NAME);

    }


}
