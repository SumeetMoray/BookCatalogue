package com.nearbyshops.android.communitylibrary.BookDetails;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.nearbyshops.android.communitylibrary.Model.Book;
import com.nearbyshops.android.communitylibrary.R;
import com.nearbyshops.android.communitylibrary.Utility.UtilityGeneral;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class BookDetail extends AppCompatActivity implements Target{

    public static String BOOK_DETAIL_INTENT_KEY;

    Book book;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.book_title)
    TextView bookTitle;

    @BindView(R.id.author_name)
    TextView authorName;

    @BindView(R.id.book_description)
    TextView bookDescription;

    @BindView(R.id.book_cover)
    ImageView bookCover;

    @BindView(R.id.rating_text)
    TextView ratingText;

    @BindView(R.id.ratings_count)
    TextView ratingsCount;

    @BindView(R.id.ratingBar)
    RatingBar ratingsBar;

    @BindView(R.id.user_rating_review)
    LinearLayout user_review_ratings_block;

    @BindView(R.id.edit_review_text)
    TextView edit_review_text;

    @BindView(R.id.ratingBar_rate)
    RatingBar ratingBar_rate;

    @BindView(R.id.read_all_reviews_button)
    TextView read_all_reviews_button;


    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;


    Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        unbinder = ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        book = getIntent().getParcelableExtra(BOOK_DETAIL_INTENT_KEY);
        bindViews(book);


        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    void bindViews(Book book)
    {

        if(book !=null) {


            if(book.getBookName().equals("null"))
            {
                bookTitle.setText("Book Title");
            }
            else
            {
                bookTitle.setText(book.getBookName());
            }



            // set Book Cover Image


            String imagePath = UtilityGeneral.getImageEndpointURL(this)
                    + book.getBookCoverImageURL();

//            if (!book.getBookCoverImageURL().equals("")) {

                Picasso.with(this).load(imagePath)
                        .placeholder(R.drawable.book_placeholder_image)
                        .into(bookCover);

                Picasso.with(this)
                        .load(imagePath)
                        .placeholder(R.drawable.book_placeholder_image)
                        .into(this);

//            }

            if (book.getRt_rating_count() == 0) {

                ratingText.setText("Rating : N/A");
                ratingsCount.setText(("Ratings Not Available"));
                ratingsBar.setVisibility(View.GONE);

            }
            else
            {
                ratingText.setText("Rating : " + String.valueOf(book.getRt_rating_avg()));
                ratingsCount.setText((int) book.getRt_rating_count() + " Ratings");
                ratingsBar.setRating(book.getRt_rating_avg());
            }


            if(!book.getBookDescription().equals("null") && !book.getBookDescription().equals(""))
            {
                bookDescription.setText(book.getBookDescription());
            }
//                bookDescription.setText("Book description Not Available.");



            if(UtilityGeneral.getUserID(this)==-1)
            {
                user_review_ratings_block.setVisibility(View.GONE);
            }else
            {
                if(book.getRt_rating_count()==0)
                {
                    edit_review_text.setText("Be the first to review and rate this book. ");
                }

                user_review_ratings_block.setVisibility(View.VISIBLE);
            }


        }


    }



    @OnClick(R.id.ratingBar_rate)
    void ratingBar_RateClick()
    {

    }


    // method to check whether the user has written the review or not if the user is currently logged in.
    void checkUserReview()
    {

        if(UtilityGeneral.getUserID(this)!=-1)
        {

            // check book ratings count
                // If ratings count is 0 then set message : Be the first to review


            // If ratings count is >0 then
                // check if user has written the review or not
                // if Yes
                    // Write messsage : Edit your review and rating
                // If NO
                    // Write message : Rate and Review this book

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(unbinder!=null)
        {
            unbinder.unbind();
        }
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {


        Palette palette = Palette.from(bitmap).generate();

        int color = getResources().getColor(R.color.colorAccent);
        int vibrant = palette.getVibrantColor(color);
        int vibrantLight = palette.getLightVibrantColor(color);
        int vibrantDark = palette.getDarkVibrantColor(color);
        int muted = palette.getMutedColor(color);
        int mutedLight = palette.getLightMutedColor(color);
        int mutedDark = palette.getDarkMutedColor(color);

        Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();

        //if(vibrantSwatch!=null) {
        //  originalTitle.setTextColor(vibrantSwatch.getTitleTextColor());
        //}



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            this.getWindow().setStatusBarColor(vibrantDark);

        }

        bookTitle.setBackgroundColor(vibrantLight);
        authorName.setBackgroundColor(vibrantLight);


        if(fab!=null && vibrantDark!=0) {

            fab.setBackgroundTintList(ColorStateList.valueOf(vibrant));

        }//fab.setBackgroundColor(vibrantDark);

        //originalTitle.setBackgroundColor(vibrantDark);


        if(collapsingToolbarLayout!=null) {

            collapsingToolbarLayout.setContentScrimColor(vibrantLight);

        }

    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }
}
