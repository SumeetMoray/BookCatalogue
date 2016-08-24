package com.nearbyshops.android.communitylibrary.BookMeetups;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nearbyshops.android.communitylibrary.BookMeetups.Interfaces.NotifyDataset;
import com.nearbyshops.android.communitylibrary.BookMeetups.Interfaces.NotifyFabState;
import com.nearbyshops.android.communitylibrary.Model.BookMeetup;
import com.nearbyshops.android.communitylibrary.R;
import com.nearbyshops.android.communitylibrary.Utility.UtilityGeneral;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class BookMeetupsActivity extends AppCompatActivity implements OnMapReadyCallback , NotifyDataset, NotifyFabState, BookMeetupAdapterHorizontal.NotifyFromMeetupAdapter{


    Unbinder unbinder;

    List<BookMeetup> dataset;

    @BindView(R.id.fragment_container)
    FrameLayout fragmentContainer;

    SupportMapFragment mapFragment;
    BookMeetupsFragment meetupsFragment;

    boolean isMapView = false;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.horizontal_list)
    RecyclerView reviewsList;

    LinearLayoutManager linearLayoutManager;
    BookMeetupAdapterHorizontal adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_book_meetups);

        unbinder = ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        meetupsFragment = new BookMeetupsFragment();
        mapFragment = new SupportMapFragment();


        if(savedInstanceState==null)
        {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container,meetupsFragment)
                    .commit();

        }


        reviewsList.setVisibility(View.GONE);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_book_meetups, menu);
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
                showFab();

            }
            else
            {
                item.setIcon(R.drawable.ic_view_list_black_24px);

//                mapFragment = new SupportMapFragment();

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container,mapFragment)
                        .commit();

                mapFragment.getMapAsync(this);

                reviewsList.setVisibility(View.VISIBLE);
                isMapView = true;
                hideFab();

            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    void setupRecyclerView()
    {

        adapter = new BookMeetupAdapterHorizontal(dataset,this,this);
        reviewsList.setAdapter(adapter);

        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        reviewsList.setLayoutManager(linearLayoutManager);

    }




    @OnClick(R.id.fab)
    void fabClick(View view)
    {

        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

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

        Location location = UtilityGeneral.getCurrentLocation(this);


        if(dataset!=null)
        {
            BookMeetup meetup = dataset.get(0);
            LatLng latLng = new LatLng(meetup.getLatitude(),meetup.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));
            Log.d("dataset","Dataset Size Map Ready : " + String.valueOf(dataset.size()));

        }else
        {
            if(location !=null)
            {
                LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,13));
            }

            Log.d("dataset","Dataset null : " + String.valueOf(dataset.size()));
        }


        markerList = new ArrayList<>();


        for(BookMeetup meetup: dataset)
        {
            LatLng latLng = new LatLng(meetup.getLatitude(),meetup.getLongitude());

//            Drawable drawable = VectorDrawableCompat.create(getResources(), R.drawable.ic_low_priority_black_24px, getTheme());

            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .snippet(meetup.getVenue())
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_group_black_24dp))
                    .title(meetup.getMeetupName()));

//            marker.showInfoWindow();

            markerList.add(marker);
        }
    }




    @Override
    public void setDataset(List<BookMeetup> dataset) {
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

        Log.d("dataset","Dataset Size : " + String.valueOf(dataset.size()));
    }

    @Override
    public void hideFab() {

        fab.animate().translationY(120);

        Log.d("dataset","Dataset Size : " + String.valueOf(dataset.size()));
    }


    @Override
    public void notifyListItemClick(int position) {

        BookMeetup meetup = dataset.get(position);
        LatLng latLng = new LatLng(meetup.getLatitude(),meetup.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));
        markerList.get(position).showInfoWindow();
    }

}
