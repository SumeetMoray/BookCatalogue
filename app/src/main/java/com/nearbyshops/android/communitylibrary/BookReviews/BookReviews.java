package com.nearbyshops.android.communitylibrary.BookReviews;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.nearbyshops.android.communitylibrary.BooksByCategory.Books.BookAdapter;
import com.nearbyshops.android.communitylibrary.DaggerComponentBuilder;
import com.nearbyshops.android.communitylibrary.Model.Book;
import com.nearbyshops.android.communitylibrary.Model.BookReview;
import com.nearbyshops.android.communitylibrary.ModelEndpoint.BookReviewEndpoint;
import com.nearbyshops.android.communitylibrary.R;
import com.nearbyshops.android.communitylibrary.RetrofitRestContract.BookReviewService;
import com.nearbyshops.android.communitylibrary.Utility.DividerItemDecoration;
import com.nearbyshops.android.communitylibrary.Utility.UtilityGeneral;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import icepick.Icepick;
import icepick.State;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookReviews extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    ArrayList<BookReview> dataset = new ArrayList<>();

    @BindView(R.id.recyclerView)
    RecyclerView reviewsList;

    BookReviewAdapter adapter;

    GridLayoutManager layoutManager;

    @Inject
    BookReviewService bookReviewService;

    @State
    Book book;



    public static final String BOOK_INTENT_KEY = "book_intent_key";


    // scroll variables
    private int limit = 30;
    @State int offset = 0;
    @State int item_count = 0;





    Unbinder unbinder;

    public BookReviews() {

        // Inject the dependencies using Dependency Injection
        DaggerComponentBuilder.getInstance()
                .getNetComponent().Inject(this);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_reviews);

        unbinder = ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        book = getIntent().getParcelableExtra(BOOK_INTENT_KEY);

        if(book!=null)
        {
            getSupportActionBar().setTitle(book.getBookName());
        }


        setupRecyclerView();
        setupSwipeContainer();


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


        if(savedInstanceState==null)
        {

            swipeContainer.post(new Runnable() {
                @Override
                public void run() {
                    swipeContainer.setRefreshing(true);

                    dataset.clear();
                    makeNetworkCall();
                }
            });

        }


    }



    void setupRecyclerView()
    {

        adapter = new BookReviewAdapter(dataset,this);
        reviewsList.setAdapter(adapter);
        layoutManager = new GridLayoutManager(this,1);
        reviewsList.setLayoutManager(layoutManager);

        reviewsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(layoutManager.findLastVisibleItemPosition() == dataset.size()-1)
                {
                    // trigger fetch next page

                    if((offset+limit)<=item_count)
                    {
                        offset = offset + limit;
                        makeNetworkCall();
                    }

                }

            }
        });


        reviewsList.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL_LIST));
//        reviewsList.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.HORIZONTAL_LIST));

        final DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        layoutManager.setSpanCount(metrics.widthPixels/350);
    }



    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    void setupSwipeContainer()
    {

        if(swipeContainer!=null) {

            swipeContainer.setOnRefreshListener(this);
            swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);
        }

    }






    private void showToastMessage(String message)
    {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
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
    public void onRefresh() {

        dataset.clear();
        offset = 0 ; // reset the offset
        makeNetworkCall();

//        Log.d("applog","refreshed");
    }



    void onRefreshSwipeIndicator()
    {

        swipeContainer.post(new Runnable() {
            @Override
            public void run() {
                swipeContainer.setRefreshing(true);

                onRefresh();
            }
        });
    }

    private void makeNetworkCall() {

        Call<BookReviewEndpoint> call = bookReviewService.getReviews(book.getBookID(),null,
                true,null,limit,offset,false);


        call.enqueue(new Callback<BookReviewEndpoint>() {

            @Override
            public void onResponse(Call<BookReviewEndpoint> call, Response<BookReviewEndpoint> response) {

                if(response.body()!=null && response.body().getResults()!=null)
                {
                    dataset.addAll(response.body().getResults());
                    adapter.notifyDataSetChanged();
                    item_count = response.body().getItemCount();
                }

                stopRefreshing();

            }

            @Override
            public void onFailure(Call<BookReviewEndpoint> call, Throwable t) {

                showToastMessage(getString(R.string.network_not_available));

                stopRefreshing();

            }
        });




    }


    void stopRefreshing()
    {
        if(swipeContainer!=null)
        {
            swipeContainer.setRefreshing(false);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Icepick.saveInstanceState(this,outState);

        outState.putParcelableArrayList("dataset",dataset);


    }



    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Icepick.restoreInstanceState(this,savedInstanceState);

        if(savedInstanceState!=null)
        {
            ArrayList<BookReview> tempCat = savedInstanceState.getParcelableArrayList("dataset");
            dataset.clear();
            dataset.addAll(tempCat);
            adapter.notifyDataSetChanged();
        }
    }
}
