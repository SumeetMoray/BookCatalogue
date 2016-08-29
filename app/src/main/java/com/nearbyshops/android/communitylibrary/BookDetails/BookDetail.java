package com.nearbyshops.android.communitylibrary.BookDetails;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nearbyshops.android.communitylibrary.BookReviews.BookReviews;
import com.nearbyshops.android.communitylibrary.DaggerComponentBuilder;
import com.nearbyshops.android.communitylibrary.Model.Book;
import com.nearbyshops.android.communitylibrary.Model.BookReview;
import com.nearbyshops.android.communitylibrary.Model.FavouriteBook;
import com.nearbyshops.android.communitylibrary.Model.Member;
import com.nearbyshops.android.communitylibrary.ModelEndpoint.BookReviewEndpoint;
import com.nearbyshops.android.communitylibrary.ModelEndpoint.FavouriteBookEndpoint;
import com.nearbyshops.android.communitylibrary.R;
import com.nearbyshops.android.communitylibrary.RetrofitRestContract.BookReviewService;
import com.nearbyshops.android.communitylibrary.RetrofitRestContract.FavouriteBookService;
import com.nearbyshops.android.communitylibrary.Utility.UtilityGeneral;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookDetail extends AppCompatActivity implements Target, RatingBar.OnRatingBarChangeListener ,NotifyReviewUpdate {

    public static String BOOK_DETAIL_INTENT_KEY;

    @Inject
    BookReviewService bookReviewService;

    @Inject
    FavouriteBookService favouriteBookService;

    Book book;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.book_title)
    TextView bookTitle;

    @BindView(R.id.author_name)
    TextView authorName;

    @BindView(R.id.date_of_publish)
    TextView publishDate;


    @BindView(R.id.publisher_name)
    TextView publisherName;

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

    @BindView(R.id.member_profile_image)
    ImageView member_profile_image;

    @BindView(R.id.member_name)
    TextView member_name;

    @BindView(R.id.member_rating)
    RatingBar member_rating_indicator;


    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;


    Unbinder unbinder;


    public BookDetail() {

        DaggerComponentBuilder.getInstance()
                .getNetComponent()
                .Inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        unbinder = ButterKnife.bind(this);

        ratingBar_rate.setOnRatingBarChangeListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        book = getIntent().getParcelableExtra(BOOK_DETAIL_INTENT_KEY);
        bindViews(book);

        if (book != null) {
            getSupportActionBar().setTitle(book.getBookName());
            getSupportActionBar().setSubtitle(book.getAuthorName());
        }


        if (book != null) {
            checkUserReview();
        }



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


        checkFavourite();
    }


    void bindViews(Book book) {

        if (book != null) {


            if (book.getBookName().equals("null")) {
                bookTitle.setText("Book Title");
            } else {
                bookTitle.setText(book.getBookName());
            }

            authorName.setText(book.getAuthorName());

            publisherName.setText("Published By : " + book.getNameOfPublisher());

            /*if(book.getDateOfPublish()!=null)
            {
                Log.d("date","Date of Publish binding " + book.getDateOfPublish().toString());

                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM ''yyyy");

                //"EEE, MMM d, ''yy"
                //"yyyy-MM-dd"

                publishDate.setText(dateFormat.format(book.getDateOfPublish()));
            }*/

            if(book.getDateOfPublishInMillis()==0)
            {
                publishDate.setText(R.string.book_date_of_publish_not_available);

            }else
            {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(book.getDateOfPublishInMillis());
                SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.date_format_simple));

                //"MMMM d ''yyyy"
                publishDate.setText(getString(R.string.date_of_publish) + dateFormat.format(calendar.getTime()));
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

                ratingText.setText(R.string.rating_not_available);
                ratingsCount.setText((getString(R.string.not_yet_rated)));
                ratingsBar.setVisibility(View.GONE);

            } else {
                ratingText.setText("Rating : " + String.format("%.1f", book.getRt_rating_avg()));
                ratingsCount.setText((int) book.getRt_rating_count() + " Ratings");
                ratingsBar.setRating(book.getRt_rating_avg());
            }


            if (book.getBookDescription()!=null && !book.getBookDescription().equals("null") && !book.getBookDescription().equals("")) {
                bookDescription.setText(book.getBookDescription());
            }
//                bookDescription.setText("Book description Not Available.");


        }


    }


    @BindView(R.id.edit_review_block)
    RelativeLayout edit_review_block;

    @BindView(R.id.review_title)
    TextView review_title;

    @BindView(R.id.review_description)
    TextView review_description;

    @BindView(R.id.review_date)
    TextView review_date;

    BookReview reviewForUpdate;


    // method to check whether the user has written the review or not if the user is currently logged in.
    void checkUserReview() {

        if (UtilityGeneral.getUser(this) == null) {

            user_review_ratings_block.setVisibility(View.GONE);

        } else {

            // Unhide review dialog


            if (book.getRt_rating_count() == 0) {

                user_review_ratings_block.setVisibility(View.VISIBLE);
                edit_review_block.setVisibility(View.GONE);

                edit_review_text.setText(R.string.book_review_be_the_first_to_review);
            } else if (book.getRt_rating_count() > 0) {


                Call<BookReviewEndpoint> call = bookReviewService.getReviews(book.getBookID(),
                        UtilityGeneral.getUser(this).getMemberID(), true, "REVIEW_DATE", null, null, null);

//                Log.d("review_check",String.valueOf(UtilityGeneral.getUserID(this)) + " : " + String.valueOf(book.getBookID()));

                call.enqueue(new Callback<BookReviewEndpoint>() {
                    @Override
                    public void onResponse(Call<BookReviewEndpoint> call, Response<BookReviewEndpoint> response) {


                        if (response.body() != null) {
                            if (response.body().getItemCount() > 0) {

//                                edit_review_text.setText("Edit your review and Rating !");


                                if(edit_review_block==null)
                                {
                                    // If the views are not bound then return. This can happen in delayed response. When this call is executed
                                    // after the activity have gone out of scope.
                                    return;
                                }

                                edit_review_block.setVisibility(View.VISIBLE);
                                user_review_ratings_block.setVisibility(View.GONE);

                                reviewForUpdate = response.body().getResults().get(0);

                                review_title.setText(response.body().getResults().get(0).getReviewTitle());
                                review_description.setText(response.body().getResults().get(0).getReviewText());

                                review_date.setText(response.body().getResults().get(0).getReviewDate().toLocaleString());

                                member_rating_indicator.setRating(response.body().getResults().get(0).getRating());


//                                user_review.setText(response.body().getResults().get(0).getReviewText());
//                                ratingBar_rate.setRating(response.body().getResults().get(0).getRating());

                                Member member = response.body().getResults().get(0).getRt_member_profile();
                                member_name.setText(member.getMemberName());

                                String imagePath = UtilityGeneral.getImageEndpointURL(BookDetail.this)
                                        + member.getProfileImageURL();

                                Picasso.with(BookDetail.this).load(imagePath)
                                        .placeholder(R.drawable.book_placeholder_image)
                                        .into(member_profile_image);


                            } else if (response.body().getItemCount() == 0) {
                                edit_review_text.setText("Rate this book !");
                                edit_review_block.setVisibility(View.GONE);
                                user_review_ratings_block.setVisibility(View.VISIBLE);

                            }
                        }


                    }

                    @Override
                    public void onFailure(Call<BookReviewEndpoint> call, Throwable t) {


//                        showToastMessage("Network Request Failed. Check your internet connection !");

                    }
                });


            }

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


    void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (unbinder != null) {
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

        bookTitle.setBackgroundColor(vibrant);
        authorName.setBackgroundColor(vibrant);


        if (fab != null && vibrantDark != 0) {

            fab.setBackgroundTintList(ColorStateList.valueOf(vibrantDark));

        }//fab.setBackgroundColor(vibrantDark);

        //originalTitle.setBackgroundColor(vibrantDark);


        if (collapsingToolbarLayout != null) {

            collapsingToolbarLayout.setContentScrimColor(vibrant);

        }

    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }


    @Override
    public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

        write_review_click();
    }


    @OnClick({R.id.edit_icon, R.id.edit_review_label})
    void edit_review_Click() {

        if (reviewForUpdate != null) {
            FragmentManager fm = getSupportFragmentManager();
            RateReviewDialog dialog = new RateReviewDialog();
            dialog.show(fm, "rate");
            dialog.setMode(reviewForUpdate, true, reviewForUpdate.getBookID());
        }

    }


    @OnClick({R.id.edit_review_text,R.id.ratingBar_rate})
    void write_review_click() {

        FragmentManager fm = getSupportFragmentManager();
        RateReviewDialog dialog = new RateReviewDialog();
        dialog.show(fm, "rate");

        if (book != null) {
            dialog.setMode(null, false, book.getBookID());
        }
    }


    @Override
    public void notifyReviewUpdated() {

        checkUserReview();
    }

    @Override
    public void notifyReviewDeleted() {

        book.setRt_rating_count(book.getRt_rating_count() - 1);
        checkUserReview();
    }

    @Override
    public void notifyReviewSubmitted() {

        book.setRt_rating_count(book.getRt_rating_count() + 1);
        checkUserReview();
    }


    @OnClick(R.id.read_all_reviews_button)
    void readAllReviewsButton() {
        Intent intent = new Intent(this, BookReviews.class);
        intent.putExtra(BookReviews.BOOK_INTENT_KEY, book);
        startActivity(intent);
    }



    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;


    void showMessageSnackBar(String message) {

        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
    }



    @OnClick(R.id.fab)
    void fabClick()
    {

        if(UtilityGeneral.getUser(this)==null)
        {
            // User Not logged In.
            showMessageSnackBar(getString(R.string.favourite_book_message));

        }else
        {
            toggleFavourite();
        }
    }



    void toggleFavourite(){

        if(book!=null && UtilityGeneral.getUser(this)!=null)
        {

            Call<FavouriteBookEndpoint> call = favouriteBookService.getFavouriteBooks(book.getBookID(),UtilityGeneral.getUser(this).getMemberID()
                    ,null,null,null,null);


            call.enqueue(new Callback<FavouriteBookEndpoint>() {
                @Override
                public void onResponse(Call<FavouriteBookEndpoint> call, Response<FavouriteBookEndpoint> response) {


                    if(response.body()!=null)
                    {
                        if(response.body().getItemCount()>=1)
                        {
                            deleteFavourite();

                        }
                        else if(response.body().getItemCount()==0)
                        {
                            insertFavourite();
                        }
                    }

                }

                @Override
                public void onFailure(Call<FavouriteBookEndpoint> call, Throwable t) {

//                    showToastMessage("Network Request failed. Check Network Connection !");
                }
            });
        }
    }


    void insertFavourite()
    {


        if(book!=null && UtilityGeneral.getUser(this)!=null)
        {

            FavouriteBook favouriteBook = new FavouriteBook();
            favouriteBook.setBookID(book.getBookID());
            favouriteBook.setMemberID(UtilityGeneral.getUser(this).getMemberID());

            Call<FavouriteBook> call = favouriteBookService.insertFavouriteBook(favouriteBook);

            call.enqueue(new Callback<FavouriteBook>() {
                @Override
                public void onResponse(Call<FavouriteBook> call, Response<FavouriteBook> response) {

                    if(response.code() == 201)
                    {
                        // created successfully

                        setFavouriteIcon(true);
                    }
                }

                @Override
                public void onFailure(Call<FavouriteBook> call, Throwable t) {

//                    showToastMessage("Network Request failed !");

                }
            });
        }


    }

    void deleteFavourite()
    {

        if(book!=null && UtilityGeneral.getUser(this)!=null)
        {
            Call<ResponseBody> call = favouriteBookService.deleteFavouriteBook(book.getBookID(),
                    UtilityGeneral.getUser(this).getMemberID());


            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if(response.code()==200)
                    {
                        setFavouriteIcon(false);
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

//                    showToastMessage("Network Request Failed !");
                }
            });
        }

    }




    void setFavouriteIcon(boolean isFavourite)
    {

        if(fab==null)
        {
            return;
        }

        if(isFavourite)
        {
            Drawable drawable = VectorDrawableCompat.create(getResources(), R.drawable.ic_favorite_white_24px, getTheme());
            fab.setImageDrawable(drawable);
        }
        else
        {
            Drawable drawable2 = VectorDrawableCompat.create(getResources(), R.drawable.ic_favorite_border_white_24px, getTheme());
            fab.setImageDrawable(drawable2);
        }


    }


    void checkFavourite()
    {

        // make a network call to check the favourite

        if(book!=null && UtilityGeneral.getUser(this)!=null)
        {

            Call<FavouriteBookEndpoint> call = favouriteBookService.getFavouriteBooks(book.getBookID(),UtilityGeneral.getUser(this).getMemberID()
                    ,null,null,null,null);


            call.enqueue(new Callback<FavouriteBookEndpoint>() {
                @Override
                public void onResponse(Call<FavouriteBookEndpoint> call, Response<FavouriteBookEndpoint> response) {


                    if(response.body()!=null)
                    {
                        if(response.body().getItemCount()>=1)
                        {
                            setFavouriteIcon(true);

                        }
                        else if(response.body().getItemCount()==0)
                        {
                            setFavouriteIcon(false);
                        }
                    }

                }

                @Override
                public void onFailure(Call<FavouriteBookEndpoint> call, Throwable t) {

//                    showToastMessage("Network Request failed. Check Network Connection !");
                }
            });


        }

    }


}
