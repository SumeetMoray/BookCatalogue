package com.nearbyshops.android.communitylibrary.AllBooks;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nearbyshops.android.communitylibrary.BookDetails.BookDetail;
import com.nearbyshops.android.communitylibrary.Model.Book;
import com.nearbyshops.android.communitylibrary.R;
import com.nearbyshops.android.communitylibrary.Utility.UtilityGeneral;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sumeet on 19/12/15.
 */


public class BookCursorAdapter extends RecyclerView.Adapter<BookCursorAdapter.ViewHolder>{

    Cursor dataset;
    Context context;

    public BookCursorAdapter(Cursor dataset, Context context) {

        this.dataset = dataset;
        this.context = context;

    }

    @Override
    public BookCursorAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_all_books,parent,false);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(BookCursorAdapter.ViewHolder holder, final int position) {

//        int rating_count = (int)dataset.get(position).getRt_rating_count();


        dataset.moveToPosition(position);

        int rating_count = dataset.getInt(dataset.getColumnIndex(Book.RT_RATING_COUNT));

        holder.bookTitle.setText(String.valueOf(dataset.getInt(dataset.getColumnIndex(Book.BOOK_ID))) + " : " + dataset.getString(dataset.getColumnIndex(Book.BOOK_NAME)));





        if(rating_count == 0)
        {
            holder.bookRating.setText("Not Yet Rated");

        }else
        {
            //String.valueOf(dataset.get(position).getRt_rating_avg()

            holder.bookRating.setText("Rating : " + String.format("%.1f",dataset.getFloat(dataset.getColumnIndex(Book.RT_RATING_AVG))) + " ("
                    + String.valueOf(rating_count) + " ratings)");
        }


        String imagePath = UtilityGeneral.getImageEndpointURL(context)
                + dataset.getString(dataset.getColumnIndex(Book.BOOK_COVER_IMAGE_URL));

        Picasso.with(context).load(imagePath)
                .placeholder(R.drawable.book_placeholder_image)
                .into(holder.bookImage);


    }


    @Override
    public int getItemCount() {

        if(dataset==null)
        {
            return 0;
        }else
        {
            return dataset.getCount();
        }
    }


    public void swapCursor(Cursor newCursor)
    {
        dataset = null;
        dataset = newCursor;
        notifyDataSetChanged();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{


        @BindView(R.id.list_item_book)
        CardView bookListItem;

        @BindView(R.id.book_title) TextView bookTitle;


        @BindView(R.id.book_image) ImageView bookImage;

        @BindView(R.id.book_rating) TextView bookRating;


        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this,itemView);
        }




        @OnClick(R.id.list_item_book)
        void listItemClick()
        {

            Book book = rowToObject(dataset,getLayoutPosition());

            Intent intent = new Intent(context, BookDetail.class);
            intent.putExtra(BookDetail.BOOK_DETAIL_INTENT_KEY,book);
            context.startActivity(intent);
        }




    }// ViewHolder Class declaration ends


    Book rowToObject(Cursor cursor, int position)
    {
        cursor.moveToPosition(position);

        Book book = new Book();



        book.setBookID(cursor.getInt(cursor.getColumnIndex(Book.BOOK_ID)));
        book.setBookCategoryID(cursor.getInt(cursor.getColumnIndex(Book.BOOK_CATEGORY_ID)));
        book.setBookName(cursor.getString(cursor.getColumnIndex(Book.BOOK_NAME)));
        book.setBookCoverImageURL(cursor.getString(cursor.getColumnIndex(Book.BOOK_COVER_IMAGE_URL)));
        book.setBackdropImageURL(cursor.getString(cursor.getColumnIndex(Book.BACKDROP_IMAGE_URL)));
        book.setAuthorName(cursor.getString(cursor.getColumnIndex(Book.AUTHOR_NAME)));
        book.setBookDescription(cursor.getString(cursor.getColumnIndex(Book.BOOK_DESCRIPTION)));

        book.setNameOfPublisher(cursor.getString(cursor.getColumnIndex(Book.PUBLISHER_NAME)));
        book.setPagesTotal(cursor.getInt(cursor.getColumnIndex(Book.PAGES_TOTAL)));

        book.setRt_rating_count(cursor.getInt(cursor.getColumnIndex(Book.RT_RATING_COUNT)));
        book.setRt_rating_avg(cursor.getFloat(cursor.getColumnIndex(Book.RT_RATING_AVG)));

        return book;
    }



    void showToastMessage(String message)
    {
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }


}