package com.nearbyshops.android.communitylibrary.BookReviews;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nearbyshops.android.communitylibrary.BookDetails.BookDetail;
import com.nearbyshops.android.communitylibrary.BooksByCategory.Books.BookFragment;
import com.nearbyshops.android.communitylibrary.BooksByCategory.Books.EditBook;
import com.nearbyshops.android.communitylibrary.DaggerComponentBuilder;
import com.nearbyshops.android.communitylibrary.Model.Book;
import com.nearbyshops.android.communitylibrary.Model.BookReview;
import com.nearbyshops.android.communitylibrary.Model.Member;
import com.nearbyshops.android.communitylibrary.R;
import com.nearbyshops.android.communitylibrary.RetrofitRestContract.BookService;
import com.nearbyshops.android.communitylibrary.SelectParent.BookCategoriesParent;
import com.nearbyshops.android.communitylibrary.Utility.UtilityGeneral;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sumeet on 19/12/15.
 */


public class BookReviewAdapter extends RecyclerView.Adapter<BookReviewAdapter.ViewHolder>{



    List<BookReview> dataset;
    Context context;

    public BookReviewAdapter(List<BookReview> dataset, Context context) {

        this.dataset = dataset;
        this.context = context;

    }

    @Override
    public BookReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_book_review,parent,false);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(BookReviewAdapter.ViewHolder holder, final int position) {

        Member member = dataset.get(position).getRt_member_profile();

        holder.member_name.setText(member.getMemberName());
        holder.rating.setRating(dataset.get(position).getRating());


        holder.review_date.setText(dataset.get(position).getReviewDate().toString());


        holder.review_title.setText(dataset.get(position).getReviewTitle());
        holder.review_text.setText(dataset.get(position).getReviewText());

        String imagePath = UtilityGeneral.getImageEndpointURL(context)
                + dataset.get(position).getRt_member_profile().getProfileImageURL();


            Picasso.with(context).load(imagePath)
                    .placeholder(R.drawable.book_placeholder_image)
                    .into(holder.profile_image);

    }


    @Override
    public int getItemCount() {

        return dataset.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{


        @BindView(R.id.profile_image)
        ImageView profile_image;

        @BindView(R.id.member_name)
        TextView member_name;

        @BindView(R.id.rating)
        RatingBar rating;

        @BindView(R.id.review_date)
        TextView review_date;

        @BindView(R.id.review_title)
        TextView review_title;

        @BindView(R.id.review_text)
        TextView review_text;



        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this,itemView);
        }


    }// ViewHolder Class declaration ends




    void showToastMessage(String message)
    {
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }


}