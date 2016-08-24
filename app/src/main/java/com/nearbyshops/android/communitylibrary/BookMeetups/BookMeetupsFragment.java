package com.nearbyshops.android.communitylibrary.BookMeetups;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nearbyshops.android.communitylibrary.BookMeetups.Interfaces.NotifyDataset;
import com.nearbyshops.android.communitylibrary.BookMeetups.Interfaces.NotifyFabState;
import com.nearbyshops.android.communitylibrary.DaggerComponentBuilder;
import com.nearbyshops.android.communitylibrary.Model.Book;
import com.nearbyshops.android.communitylibrary.Model.BookMeetup;
import com.nearbyshops.android.communitylibrary.ModelEndpoint.BookMeetupEndpoint;
import com.nearbyshops.android.communitylibrary.R;
import com.nearbyshops.android.communitylibrary.RetrofitRestContract.BookMeetupService;
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

public class BookMeetupsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    ArrayList<BookMeetup> dataset = new ArrayList<>();

    @BindView(R.id.recyclerView)
    RecyclerView reviewsList;

    BookMeetupAdapter adapter;

    GridLayoutManager layoutManager;

    @Inject
    BookMeetupService bookMeetupService;

    @State
    Book book;

    @State boolean show = true;


    public static final String BOOK_INTENT_KEY = "book_intent_key";

    // scroll variables
    private int limit = 30;
    @State int offset = 0;
    @State int item_count = 0;


    Unbinder unbinder;

    NotifyFabState notifyFabState;



    public BookMeetupsFragment() {

        // Inject the dependencies using Dependency Injection
        DaggerComponentBuilder.getInstance()
                .getNetComponent().Inject(this);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_book_meetups,container,false);

        unbinder = ButterKnife.bind(this,view);

        setupRecyclerView();
        setupSwipeContainer();

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


        if(getActivity() instanceof NotifyFabState)
        {
            notifyFabState = (NotifyFabState) getActivity();
        }

        if(getActivity() instanceof NotifyDataset)
        {
            ((NotifyDataset)getActivity()).setDataset(dataset);
        }


        return view;
    }



    void setupRecyclerView()
    {

        adapter = new BookMeetupAdapter(dataset,getActivity());
        reviewsList.setAdapter(adapter);

        layoutManager = new GridLayoutManager(getActivity(),1);
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

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(dy < -20)
                {

                    boolean previous = show;

                    show = true ;

                    if(show!=previous)
                    {
                        // changed
//                        Log.d("scrolllog","show");

                        if(notifyFabState!=null)
                        {
                            notifyFabState.showFab();
                        }
                    }

                }else if(dy > 20)
                {

                    boolean previous = show;

                    show = false;



                    if(show!=previous)
                    {
                        // changed

                        if(notifyFabState!=null)
                        {
                            notifyFabState.hideFab();
                        }
                    }
                }

            }
        });


//        reviewsList.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL_LIST));
//        reviewsList.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.HORIZONTAL_LIST));

        final DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
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
        if(getActivity()!=null)
        {
            Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

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

        Double latitude = null;
        Double longitude = null;

        if(UtilityGeneral.getCurrentLocation(getActivity())!=null)
        {
            Location location = UtilityGeneral.getCurrentLocation(getContext());

            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }


        Call<BookMeetupEndpoint> call = bookMeetupService.getMeetups(longitude,latitude,null,"distance",limit,offset,null);

        call.enqueue(new Callback<BookMeetupEndpoint>() {
            @Override
            public void onResponse(Call<BookMeetupEndpoint> call, Response<BookMeetupEndpoint> response) {

                if(response.body().getResults()!=null)
                {
                    dataset.addAll(response.body().getResults());
                    adapter.notifyDataSetChanged();

                    if(getActivity() instanceof NotifyDataset)
                    {
                        ((NotifyDataset)getActivity()).setDataset(dataset);
                    }

                    item_count = response.body().getItemCount();
                }

                stopRefreshing();

            }

            @Override
            public void onFailure(Call<BookMeetupEndpoint> call, Throwable t) {

                showToastMessage("Network Request failed !");

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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Icepick.saveInstanceState(this,outState);

        outState.putParcelableArrayList("dataset",dataset);


    }


    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        Icepick.restoreInstanceState(this,savedInstanceState);

        if(savedInstanceState!=null)
        {
            ArrayList<BookMeetup> tempCat = savedInstanceState.getParcelableArrayList("dataset");
            dataset.clear();
            dataset.addAll(tempCat);
            adapter.notifyDataSetChanged();
        }
    }



}
