package com.nearbyshops.communityLibrary.database.FavouriteBooks;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.nearbyshops.communityLibrary.database.DaggerComponentBuilder;
import com.nearbyshops.communityLibrary.database.Model.Book;
import com.nearbyshops.communityLibrary.database.ModelEndpoint.BookEndpoint;
import com.nearbyshops.communityLibrary.database.R;
import com.nearbyshops.communityLibrary.database.RetrofitRestContract.BookService;
import com.nearbyshops.communityLibrary.database.Utility.UtilityGeneral;

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

public class FavouriteBooks extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    ArrayList<Book> dataset = new ArrayList<>();

    @BindView(R.id.recyclerView)
    RecyclerView reviewsList;

    FavouriteBookAdapter adapter;

    GridLayoutManager layoutManager;

    @Inject
    BookService bookService;

    // scroll variables
    private int limit = 30;
    @State int offset = 0;
    @State int item_count = 0;

    Unbinder unbinder;

    public FavouriteBooks() {

        // Inject the dependencies using Dependency Injection
        DaggerComponentBuilder.getInstance()
                .getNetComponent().Inject(this);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_book);

        unbinder = ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



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

        adapter = new FavouriteBookAdapter(dataset,this);
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


//        reviewsList.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL_LIST));

        final DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        layoutManager.setSpanCount(metrics.widthPixels/200);
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

        Log.d("applog","refreshed");
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


        if(UtilityGeneral.getUser(this)==null)
        {

            swipeContainer.setRefreshing(false);
            showToastMessage(getString(R.string.login_message_user_not_logged_in));
            return;

        }

        Call<BookEndpoint> call = bookService.getBooks(null, UtilityGeneral.getUser(this).getMemberID(),null,
                limit,offset,null);


        call.enqueue(new Callback<BookEndpoint>() {
            @Override
            public void onResponse(Call<BookEndpoint> call, Response<BookEndpoint> response) {

                if(response.body().getResults()!=null)
                {
                    dataset.addAll(response.body().getResults());
                    adapter.notifyDataSetChanged();
                    item_count = response.body().getItemCount();
                }


                swipeContainer.setRefreshing(false);

            }

            @Override
            public void onFailure(Call<BookEndpoint> call, Throwable t) {

                showToastMessage(getString(R.string.network_not_available));

                swipeContainer.setRefreshing(false);

            }
        });
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
            ArrayList<Book> tempCat = savedInstanceState.getParcelableArrayList("dataset");
            dataset.clear();
            dataset.addAll(tempCat);
            adapter.notifyDataSetChanged();
        }
    }
}
