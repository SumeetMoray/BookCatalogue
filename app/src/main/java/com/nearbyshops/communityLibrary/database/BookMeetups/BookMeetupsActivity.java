package com.nearbyshops.communityLibrary.database.BookMeetups;

import android.Manifest;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nearbyshops.communityLibrary.database.BookMeetups.Interfaces.NotifyDataset;
import com.nearbyshops.communityLibrary.database.BookMeetups.Interfaces.NotifyFabState;
import com.nearbyshops.communityLibrary.database.BookMeetups.Interfaces.NotifySort;
import com.nearbyshops.communityLibrary.database.Model.BookMeetup;
import com.nearbyshops.communityLibrary.database.R;
import com.nearbyshops.communityLibrary.database.Utility.UtilityGeneral;
import com.wunderlist.slidinglayer.SlidingLayer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import icepick.Icepick;
import icepick.State;

public class BookMeetupsActivity extends AppCompatActivity implements OnMapReadyCallback , NotifyDataset, NotifyFabState, BookMeetupAdapterHorizontal.NotifyFromMeetupAdapter{


    Unbinder unbinder;

    ArrayList<BookMeetup> dataset;

    @BindView(R.id.fragment_container)
    FrameLayout fragmentContainer;

    SupportMapFragment mapFragment;
    BookMeetupsFragment meetupsFragment;

    @State
    boolean isMapView = false;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.horizontal_list)
    RecyclerView reviewsList;

    LinearLayoutManager linearLayoutManager;
    BookMeetupAdapterHorizontal adapter;

    @BindView(R.id.slidingLayer)
    SlidingLayer slidingLayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_book_meetups);

        unbinder = ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        meetupsFragment = new BookMeetupsFragment();

        if(savedInstanceState==null)
        {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container,meetupsFragment)
                    .commit();

            reviewsList.setVisibility(View.GONE);

//            isMapView = false;
        }





        setupSlidingLayer();
    }



    void setupSlidingLayer()
    {

        ////slidingLayer.setShadowDrawable(R.drawable.sidebar_shadow);
        //slidingLayer.setShadowSizeRes(R.dimen.shadow_size);

        if(slidingLayer!=null)
        {
            slidingLayer.setChangeStateOnTap(true);
            slidingLayer.setSlidingEnabled(true);
            slidingLayer.setPreviewOffsetDistance(15);
            slidingLayer.setOffsetDistance(10);
            slidingLayer.setStickTo(SlidingLayer.STICK_TO_RIGHT);

            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);

            //RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(250, ViewGroup.LayoutParams.MATCH_PARENT);

            //slidingContents.setLayoutParams(layoutParams);

            //slidingContents.setMinimumWidth(metrics.widthPixels-50);

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_book_meetups, menu);

        if(isMapView)
        {
            menu.findItem(R.id.action_switch_map).setIcon(R.drawable.ic_view_list_black_24px);
        }


        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

//        searchView.setQuery(getIntent().getStringExtra(SearchManager.QUERY),false);



        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_switch_map) {


            if (isMapView) {

                item.setIcon(R.drawable.ic_map_black_24px);

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container,meetupsFragment)
                        .commit();

                reviewsList.setVisibility(View.GONE);
                isMapView = false;
                slidingLayer.setVisibility(View.VISIBLE);
                showFab();

            }
            else
            {
                item.setIcon(R.drawable.ic_view_list_black_24px);
                setupRecyclerView();
                mapFragment = new SupportMapFragment();


                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container,mapFragment)
                        .commit();

                mapFragment.getMapAsync(this);

                reviewsList.setVisibility(View.VISIBLE);
                isMapView = true;

                slidingLayer.setVisibility(View.GONE);
                hideFab();

            }

            return true;
        }
        else if(id == R.id.action_sort)
        {

            slidingLayer.openLayer(true);
        }

        return super.onOptionsItemSelected(item);
    }




    void setupRecyclerView()
    {

        adapter = new BookMeetupAdapterHorizontal(dataset,this,this);
        reviewsList.setAdapter(adapter);

        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        reviewsList.setLayoutManager(linearLayoutManager);


        reviewsList.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(dataset!=null && dataset.size()>0)
                {
                    int position = linearLayoutManager.findFirstVisibleItemPosition();

                    if(position!=RecyclerView.NO_POSITION)
                    {
                        notifyListItemClick(position);
                    }

                }

            }
        });

    }




    @OnClick(R.id.fab)
    void fabClick(View view)
    {

//        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show();

        Intent intent = new Intent(this,AddBookMeetup.class);
        startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(unbinder!=null)
        {
            unbinder.unbind();
        }
    }


    private GoogleMap mMap;
    List<Marker> markerList;

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.


            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},2);

            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
//        mMap.getUiSettings().setZoomControlsEnabled(true);

        Location location = UtilityGeneral.getCurrentLocation(this);


        if(dataset!=null && dataset.size()>0)
        {
            BookMeetup meetup = dataset.get(0);
            LatLng latLng = new LatLng(meetup.getLatitude(),meetup.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,12));
//            Log.d("dataset","Dataset Size Map Ready : " + String.valueOf(dataset.size()));

        }else
        {
            if(location !=null)
            {
                LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,12));
            }

            Log.d("dataset","Dataset null : " + String.valueOf(dataset.size()));
        }


        markerList = new ArrayList<>();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                reviewsList.scrollToPosition(markerList.indexOf(marker));

                marker.showInfoWindow();

                return true;
            }
        });


        for(BookMeetup meetup: dataset)
        {
            LatLng latLng = new LatLng(meetup.getLatitude(),meetup.getLongitude());

//            Drawable drawable = VectorDrawableCompat.create(getResources(), R.drawable.ic_low_priority_black_24px, getTheme());

            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .snippet(meetup.getVenue())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                    .title(meetup.getMeetupName()));

            //                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_group_black_24dp))
//            marker.showInfoWindow();

            markerList.add(marker);


        }
    }




    @Override
    public void setDataset(ArrayList<BookMeetup> dataset) {

        this.dataset = dataset;

        Log.d("dataset","Dataset Size : " + String.valueOf(dataset.size()));

        if(adapter!=null)
        {
            adapter.notifyDataSetChanged();
        }

        setupRecyclerView();
    }



    @Override
    public void showFab() {

        fab.animate().translationY(0);

//        Log.d("dataset","Dataset Size : " + String.valueOf(dataset.size()));
    }

    @Override
    public void hideFab() {

        fab.animate().translationY(120);

//        Log.d("dataset","Dataset Size : " + String.valueOf(dataset.size()));
    }


    @Override
    public void notifyListItemClick(int position) {

        BookMeetup meetup = dataset.get(position);
        LatLng latLng = new LatLng(meetup.getLatitude(),meetup.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

        if(markerList!=null){
            markerList.get(position).showInfoWindow();
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
            ArrayList<BookMeetup> tempCat = savedInstanceState.getParcelableArrayList("dataset");
            dataset = new ArrayList<>();
            dataset.addAll(tempCat);
//            adapter.notifyDataSetChanged();

        }


        if(isMapView)
        {
            reviewsList.setVisibility(View.VISIBLE);
            hideFab();
            setupRecyclerView();

            mapFragment = new SupportMapFragment();

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container,mapFragment)
                    .commit();

            mapFragment.getMapAsync(this);

        }else
        {
            reviewsList.setVisibility(View.GONE);
        }
    }




    // apply sort code


    @BindView(R.id.sort_by_title)
    TextView sortByTitle;

    @BindView(R.id.sort_by_distance)
    TextView sortByDistance;

    @BindView(R.id.sort_by_meetup_time)
    TextView sortByMeetupTime;

    @BindView(R.id.sort_descending)
    TextView sortDescending;

    @BindView(R.id.sort_ascending)
    TextView sortAscending;



    public final static int SORT_BY_TITLE = 1;
    public final static int SORT_BY_DISTANCE = 2;
    public final static int SORT_BY_MEETUP_DATE_TIME = 3;

    @State int sort_by = SORT_BY_DISTANCE;
    @State boolean whetherDescending = false;



    NotifySort notifySort;



    @OnClick(R.id.sort_by_title)
    void sortByTitle_click()
    {

        sort_by = SORT_BY_TITLE;

        clearSortOptions();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            sortByTitle.setTextColor(getResources().getColor(R.color.white,null));
            sortByTitle.setBackgroundColor(getResources().getColor(R.color.Gray88Alpha,null));

        }else
        {
            sortByTitle.setTextColor(getResources().getColor(R.color.white));
            sortByTitle.setBackgroundColor(getResources().getColor(R.color.Gray88Alpha));
        }

        applySort();

    }

    @OnClick(R.id.sort_by_distance)
    void sortByRating_click()
    {
        sort_by = SORT_BY_DISTANCE;

        clearSortOptions();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            sortByDistance.setTextColor(getResources().getColor(R.color.white,null));
            sortByDistance.setBackgroundColor(getResources().getColor(R.color.Gray88Alpha,null));

        }else
        {
            sortByDistance.setTextColor(getResources().getColor(R.color.white));
            sortByDistance.setBackgroundColor(getResources().getColor(R.color.Gray88Alpha));
        }

        applySort();
    }



    @OnClick(R.id.sort_by_meetup_time)
    void sortByReleaseDate_click()
    {

        sort_by = SORT_BY_MEETUP_DATE_TIME;

        clearSortOptions();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            sortByMeetupTime.setTextColor(getResources().getColor(R.color.white,null));
            sortByMeetupTime.setBackgroundColor(getResources().getColor(R.color.Gray88Alpha,null));

        }else
        {
            sortByMeetupTime.setTextColor(getResources().getColor(R.color.white));
            sortByMeetupTime.setBackgroundColor(getResources().getColor(R.color.Gray88Alpha));
        }

        applySort();
    }


    @OnClick(R.id.sort_ascending)
    void sortByAscending_Click()
    {

        whetherDescending = false;

        clearAscending();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            sortAscending.setTextColor(getResources().getColor(R.color.white,null));
            sortAscending.setBackgroundColor(getResources().getColor(R.color.Gray88Alpha,null));

        }else
        {
            sortAscending.setTextColor(getResources().getColor(R.color.white));
            sortAscending.setBackgroundColor(getResources().getColor(R.color.Gray88Alpha));
        }

        applySort();
    }



    @OnClick(R.id.sort_descending)
    void sortDescending_Click()
    {

        whetherDescending = true;

        clearAscending();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            sortDescending.setTextColor(getResources().getColor(R.color.white,null));
            sortDescending.setBackgroundColor(getResources().getColor(R.color.Gray88Alpha,null));

        }else
        {
            sortDescending.setTextColor(getResources().getColor(R.color.white));
            sortDescending.setBackgroundColor(getResources().getColor(R.color.Gray88Alpha));
        }

        applySort();
    }




    void clearSortOptions()
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            sortByTitle.setTextColor(getResources().getColor(R.color.blueGrey800,null));
            sortByDistance.setTextColor(getResources().getColor(R.color.blueGrey800,null));
            sortByMeetupTime.setTextColor(getResources().getColor(R.color.blueGrey800,null));

            sortByDistance.setBackgroundColor(getResources().getColor(R.color.light_grey_sort_option,null));
            sortByTitle.setBackgroundColor(getResources().getColor(R.color.light_grey_sort_option,null));
            sortByMeetupTime.setBackgroundColor(getResources().getColor(R.color.light_grey_sort_option,null));

        }else
        {
            sortByTitle.setTextColor(getResources().getColor(R.color.blueGrey800));
            sortByDistance.setTextColor(getResources().getColor(R.color.blueGrey800));
            sortByMeetupTime.setTextColor(getResources().getColor(R.color.blueGrey800));

            sortByDistance.setBackgroundColor(getResources().getColor(R.color.light_grey_sort_option));
            sortByTitle.setBackgroundColor(getResources().getColor(R.color.light_grey_sort_option));
            sortByMeetupTime.setBackgroundColor(getResources().getColor(R.color.light_grey_sort_option));
        }
    }


    void clearAscending()
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            sortAscending.setTextColor(getResources().getColor(R.color.blueGrey800,null));
            sortDescending.setTextColor(getResources().getColor(R.color.blueGrey800,null));

            sortAscending.setBackgroundColor(getResources().getColor(R.color.light_grey_sort_option,null));
            sortDescending.setBackgroundColor(getResources().getColor(R.color.light_grey_sort_option,null));

        }else
        {
            sortAscending.setTextColor(getResources().getColor(R.color.blueGrey800));
            sortDescending.setTextColor(getResources().getColor(R.color.blueGrey800));

            sortAscending.setBackgroundColor(getResources().getColor(R.color.light_grey_sort_option));
            sortDescending.setBackgroundColor(getResources().getColor(R.color.light_grey_sort_option));
        }
    }



    void applySort()
    {
        if(notifySort!=null)
        {
            if(!isMapView)
            {
                notifySort.applySort(sort_by,whetherDescending);
            }
        }
    }


    // apply sort code ends

}
