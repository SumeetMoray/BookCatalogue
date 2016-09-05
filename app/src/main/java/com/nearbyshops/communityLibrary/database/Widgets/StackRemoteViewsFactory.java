package com.nearbyshops.communityLibrary.database.Widgets;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.nearbyshops.communityLibrary.database.Data.LibraryDBContract;
import com.nearbyshops.communityLibrary.database.Model.Book;
import com.nearbyshops.communityLibrary.database.R;
import com.nearbyshops.communityLibrary.database.Utility.UtilityGeneral;
import com.squareup.picasso.Picasso;

/**
 * Created by sumeet on 28/7/16.
 */



class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final int mCount = 10;
//    private List<WidgetItem> mWidgetItems = new ArrayList<WidgetItem>();
    private Context mContext;
    private int mAppWidgetId;

    Cursor dataset;


    /*
        cursor = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                new String[] { "Distinct " + QuoteColumns.SYMBOL }, null,
                null, null);
    */


    public StackRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);


        String sort_string = "";

        sort_string = " RT_RATING_AVG desc ";


        String limit_offset = " limit " + String.valueOf(10) + " offset " + String.valueOf(0);


        dataset = mContext.getContentResolver().query(LibraryDBContract.BookContract.CONTENT_URI,
                LibraryDBContract.BookContract.PROJECTION_ALL,null,null,sort_string + limit_offset);




        //

    }


    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {


        return dataset.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        // Construct a remote views item based on the app widget item XML file,
        // and set the text based on the position.
        final RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.list_item_book_widget);

        String message = "Position : " + String.valueOf(position);


        dataset.moveToPosition(position);


//        Log.d("widget", dataset.getString(dataset.getColumnIndex(Book.BOOK_NAME)));

        int rating_count = dataset.getInt(dataset.getColumnIndex(Book.RT_RATING_COUNT));

        rv.setTextViewText(R.id.book_title,dataset.getString(dataset.getColumnIndex(Book.BOOK_NAME)));

        if(rating_count == 0)
        {
//            holder.bookRating.setText("Not Yet Rated");
            rv.setTextViewText(R.id.book_rating,mContext.getString(R.string.rating_not_available));

        }else
        {
            //String.valueOf(dataset.get(position).getRt_rating_avg()

            rv.setTextViewText(R.id.book_rating,"Rating : " + String.format("%.1f",dataset.getFloat(dataset.getColumnIndex(Book.RT_RATING_AVG))) + " ("
                    + String.valueOf(rating_count) + " ratings)");
        }


        String imagePath = UtilityGeneral.getImageEndpointURL(mContext)
                + dataset.getString(dataset.getColumnIndex(Book.BOOK_COVER_IMAGE_URL));


        try
        {
            rv.setImageViewBitmap(R.id.book_image,Picasso.with(mContext).load(imagePath).get());
        }
        catch (Exception ex)
        {

        }




        // Return the remote views object.
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {

        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }


}
