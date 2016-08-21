package com.nearbyshops.android.communitylibrary.AllBooks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.nearbyshops.android.communitylibrary.DaggerComponentBuilder;
import com.nearbyshops.android.communitylibrary.Dialogs.SortFIlterBookDialog;
import com.nearbyshops.android.communitylibrary.Model.Book;
import com.nearbyshops.android.communitylibrary.ModelEndpoint.BookEndpoint;
import com.nearbyshops.android.communitylibrary.R;
import com.nearbyshops.android.communitylibrary.RetrofitRestContract.BookService;
import com.nearbyshops.android.communitylibrary.Utility.UtilityGeneral;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import icepick.Icepick;
import icepick.State;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BooksActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, SortFIlterBookDialog.NotifySort{

    ArrayList<Book> dataset = new ArrayList<>();

    @BindView(R.id.recyclerView)
    RecyclerView reviewsList;

    AllBookAdapter adapter;

    GridLayoutManager layoutManager;

    @Inject
    BookService bookService;

    // scroll variables
    private int limit = 30;
    @State int offset = 0;
    @State int item_count = 0;

    Unbinder unbinder;


    // Default
    @State int current_sort_by;
    @State boolean current_whether_descending;


    public BooksActivity() {

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


        // set default sort options
        setDefaultSort();

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


    void setDefaultSort()
    {
        current_sort_by = UtilityGeneral.getBookSortOptions(this).getSort_by();
        current_whether_descending = UtilityGeneral.getBookSortOptions(this).isWhether_descending();
    }



    void setupRecyclerView()
    {

        adapter = new AllBookAdapter(dataset,this);
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


        String sort_string = "";

        if(current_sort_by == SortFIlterBookDialog.SORT_BY_RATING)
        {
            sort_string = "avg_rating";
        }
        else if(current_sort_by == SortFIlterBookDialog.SORT_BY_RELEASE_DATE)
        {
            sort_string = "DATE_OF_PUBLISH";
        }
        else if(current_sort_by == SortFIlterBookDialog.SORT_BY_TITLE)
        {
            sort_string = "BOOK_NAME";
        }


        if(current_whether_descending)
        {
            sort_string = sort_string + " " + "desc NULLS LAST";
        }



        Call<BookEndpoint> call = bookService.getBooks(null,null,sort_string,
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

                stopRefresh();


            }

            @Override
            public void onFailure(Call<BookEndpoint> call, Throwable t) {

                showToastMessage("Network Request failed !");

                stopRefresh();

            }
        });
    }


    void stopRefresh()
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
            ArrayList<Book> tempCat = savedInstanceState.getParcelableArrayList("dataset");
            dataset.clear();
            dataset.addAll(tempCat);
            adapter.notifyDataSetChanged();
        }
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_book_all, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if(id == R.id.action_sort)
        {

//            showToastMessage("Sort !");

            action_sort();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    void action_sort()
    {
        current_sort_by = UtilityGeneral.getBookSortOptions(this).getSort_by();
        current_whether_descending = UtilityGeneral.getBookSortOptions(this).isWhether_descending();

        FragmentManager fm = getSupportFragmentManager();
        SortFIlterBookDialog sortDialog = new SortFIlterBookDialog();
        sortDialog.setCurrentSort(current_sort_by,current_whether_descending);
        sortDialog.show(fm,"sort");
    }




    @Override
    public void applySort(int sortBy, boolean whetherDescendingLocal) {

//        showToastMessage("Applied Fragment !");
        current_sort_by = sortBy;
        current_whether_descending = whetherDescendingLocal;

        UtilityGeneral.saveSortBooks(sortBy,whetherDescendingLocal);
        onRefreshSwipeIndicator();
    }



}
