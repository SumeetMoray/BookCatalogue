package com.nearbyshops.android.communitylibrary.AllBooks;

import android.content.Context;
import android.content.Intent;
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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sumeet on 19/12/15.
 */


public class AllBookAdapter extends RecyclerView.Adapter<AllBookAdapter.ViewHolder>{

    List<Book> dataset;
    Context context;

    public AllBookAdapter(List<Book> dataset, Context context) {

        this.dataset = dataset;
        this.context = context;

    }

    @Override
    public AllBookAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_all_books,parent,false);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(AllBookAdapter.ViewHolder holder, final int position) {

        int rating_count = (int)dataset.get(position).getRt_rating_count();


        if(!dataset.get(position).getBookName().equals("null"))
        {
            holder.bookTitle.setText(dataset.get(position).getBookName());
        }else
        {
            holder.bookTitle.setText("Title Book");

        }


        if(rating_count == 0)
        {
            holder.bookRating.setText("Not Yet Rated");

        }else
        {
            //String.valueOf(dataset.get(position).getRt_rating_avg()

            holder.bookRating.setText("Rating : " + String.format("%.1f",dataset.get(position).getRt_rating_avg()) + " ("
                    + String.valueOf(rating_count) + " ratings)");
        }


        String imagePath = UtilityGeneral.getImageEndpointURL(context)
                + dataset.get(position).getBookCoverImageURL();

        Picasso.with(context).load(imagePath)
                .placeholder(R.drawable.book_placeholder_image)
                .into(holder.bookImage);

    }


    @Override
    public int getItemCount() {

        return dataset.size();
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
            Intent intent = new Intent(context, BookDetail.class);
            intent.putExtra(BookDetail.BOOK_DETAIL_INTENT_KEY,dataset.get(getLayoutPosition()));
            context.startActivity(intent);
        }




    }// ViewHolder Class declaration ends




    void showToastMessage(String message)
    {
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }


}