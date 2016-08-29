package com.nearbyshops.android.communitylibrary;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.os.ResultReceiver;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.nearbyshops.android.communitylibrary.AllBooks.BooksActivity;
import com.nearbyshops.android.communitylibrary.BookMeetups.BookMeetupsActivity;
import com.nearbyshops.android.communitylibrary.BookMeetups.BookMeetupsFragment;
import com.nearbyshops.android.communitylibrary.BooksByCategory.BookCategoriesTabs;
import com.nearbyshops.android.communitylibrary.FavouriteBooks.FavouriteBooks;
import com.nearbyshops.android.communitylibrary.Login.EditProfile;
import com.nearbyshops.android.communitylibrary.Login.LoginDialog;
import com.nearbyshops.android.communitylibrary.Login.NotifyAboutLogin;
import com.nearbyshops.android.communitylibrary.Model.BookCategory;
import com.nearbyshops.android.communitylibrary.Model.Member;
import com.nearbyshops.android.communitylibrary.Utility.UtilityGeneral;
import com.nearbyshops.android.communitylibrary.UtilityGeocoding.Constants;
import com.nearbyshops.android.communitylibrary.UtilityGeocoding.FetchAddressIntentService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import icepick.Icepick;
import icepick.State;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NotifyAboutLogin , GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {


    private Unbinder unbinder;

    @BindView(R.id.option_all_books)
    RelativeLayout optionAllBooks;

    @BindView(R.id.option_books_by_category)
    RelativeLayout optionBooksByCategory;

    NavigationView navigationView;



    // location variables
    //private FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;

    @BindView(R.id.text_lat_lon)
    TextView text_lat_longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        unbinder = ButterKnife.bind(this);

        // Location Code

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        // Location code ends

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().getItem(1).setVisible(false);

        checkLogin();
    }


    @State boolean instructionsVisible = false;
    @BindView(R.id.show_hide_instructions) TextView showHideInstructions;
    @BindView(R.id.usage_instructions) TextView usageInstructions;

    @OnClick(R.id.show_hide_instructions)
    void clickShowHideInstructions()
    {
        if(instructionsVisible)
        {
            usageInstructions.setVisibility(View.GONE);

            instructionsVisible = false;

        }else
        {
            usageInstructions.setVisibility(View.VISIBLE);

            instructionsVisible = true;
        }

    }


    boolean location_block_visible = false;

    @BindView(R.id.location_settings_block)
    RelativeLayout locationBlock;


    @OnClick(R.id.show_hide_location_settings)
    public void showHideLocationBlock(View view)
    {
        if(location_block_visible)
        {
            locationBlock.setVisibility(View.GONE);
            location_block_visible = false;

        }else
        {
            locationBlock.setVisibility(View.VISIBLE);
            location_block_visible = true;
        }
    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }


    private void showLoginDialog()
    {
        FragmentManager fm = getSupportFragmentManager();
        LoginDialog loginDialog = new LoginDialog();
        loginDialog.show(fm,"login");
    }



    void checkLogin()
    {

        /*if(UtilityGeneral.getUserID(this)==-1)
        {
            // No user Logged in !
            navigationView.getMenu().findItem(R.id.nav_camera).setTitle("Login");
        }else
        {
            // user already logged in !
            navigationView.getMenu().findItem(R.id.nav_camera).setTitle("Logout !");
        }*/

        if(UtilityGeneral.getUser(this)==null)
        {
            // No user Logged in !
            navigationView.getMenu().findItem(R.id.nav_camera).setTitle(R.string.nav_menu_label_login);


        }else
        {
            // user already logged in !
            navigationView.getMenu().findItem(R.id.nav_camera).setTitle(R.string.nav_menu_label_logout);
            navigationView.getMenu().getItem(1).setVisible(true);

        }


        setNavigationHeader();

    }

    @Override
    public void NotifyLogin()
    {
        navigationView.getMenu().findItem(R.id.nav_camera).setTitle(getString(R.string.nav_menu_label_logout));
//        showToastMessage("User ID : " + String.valueOf(UtilityGeneral.getUserID(this)));

//        UtilityGeneral.saveUser(null,this);

        navigationView.getMenu().getItem(1).setVisible(true);

            setNavigationHeader();
    }


    void setNavigationHeader()
    {
        View headerLayout = navigationView.getHeaderView(0);

        ImageView profileImage = (ImageView) headerLayout.findViewById(R.id.profile_image);
        TextView userName = (TextView) headerLayout.findViewById(R.id.member_name);


        Member member = UtilityGeneral.getUser(this);


        if(member!=null)
        {
            String imagePath = UtilityGeneral.getImageEndpointURL(this)
                    + member.getProfileImageURL();

            Picasso.with(this).load(imagePath)
                    .placeholder(R.drawable.book_placeholder_image)
                    .into(profileImage);

            userName.setText(member.getMemberName());
        }else
        {
            profileImage.setImageDrawable(null);
            userName.setText("");
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

//            showToastMessage("Login");


            if(UtilityGeneral.getUser(this)==null)
            {
                showLoginDialog();

            }else
            {
                UtilityGeneral.saveUser(null,this);
                UtilityGeneral.saveUserID(-1);

                showToastMessage(getString(R.string.toast_user_logged_out));
                navigationView.getMenu().getItem(1).setVisible(false);
                item.setTitle(getString(R.string.nav_menu_label_login));
                setNavigationHeader();
            }

            // Handle the camera action
        } else if(id== R.id.nav_favourite_book)
        {

            if(UtilityGeneral.getUser(this)!=null)
            {
                Intent intent = new Intent(this, FavouriteBooks.class);
                startActivity(intent);
            }
            else
            {
                showToastMessage(getString(R.string.favourite_books_not_logged_in));
            }

        }
        else if(id == R.id.nav_edit_profile)
        {
            Intent intent = new Intent(this, EditProfile.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @OnClick(R.id.option_all_books)
    void allBooksClick()
    {
//        showToastMessage("All BooksActivityBackup Click");
        Intent intent = new Intent(this,BooksActivity.class);
        startActivity(intent);
    }


    @OnClick(R.id.option_books_by_category)
    void booksByCategoryClick()
    {
        Intent intent = new Intent(this, BookCategoriesTabs.class);
        startActivity(intent);
    }



    @OnClick(R.id.option_book_meetups)
    void bookMeetupClick()
    {
        Intent intent = new Intent(this, BookMeetupsActivity.class);
        startActivity(intent);
    }



    void showToastMessage(String message)
    {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbinder.unbind();

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Icepick.saveInstanceState(this, outState);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);


        if (savedInstanceState != null) {

            Icepick.restoreInstanceState(this, savedInstanceState);

            ArrayList<BookCategory> tempList = savedInstanceState.getParcelableArrayList("dataset");
        }
    }


    @Override
    protected void onStop() {

        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }

        super.onStop();
    }


    @Override
    protected void onStart() {

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

        super.onStart();
    }


    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);


            return;
        }


        if (mGoogleApiClient == null) {

            return;
        }


        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);


        if (mLastLocation != null) {

            saveLocation(mLastLocation);


        }else
        {

            // if getlastlocation does not work then request the device to get the current location.
            createLocationRequest();


            if(mLocationRequest!=null)
            {
                startLocationUpdates();
            }

        }
    }




    private static final int REQUEST_CHECK_SETTINGS = 3;



    protected void createLocationRequest() {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());


        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {

                final Status status = locationSettingsResult.getStatus();
                final LocationSettingsStates states = locationSettingsResult.getLocationSettingsStates();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    HomeActivity.this,
                                    REQUEST_CHECK_SETTINGS);

                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        // ...
                        break;

                }
            }

        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CHECK_SETTINGS) {

            if (resultCode == RESULT_OK) {


                showToastMessage(getString(R.string.toast_permission_granted));

                onConnected(null);

            } else {


                showToastMessage(getString(R.string.toast_permission_not_granted));
            }
        }
    }




    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.d("applog","Google api client connection failed !");

    }


    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.


            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                    2);

            return;
        }


        if(mGoogleApiClient.isConnected())
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }

    }



    protected void stopLocationUpdates() {


        if(mGoogleApiClient.isConnected())
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }

    }


    @Override
    public void onLocationChanged(Location location) {

        saveLocation(location);

        stopLocationUpdates();
    }



    void saveLocation(Location location)
    {

        text_lat_longitude.setText("Latitude    : " + String.format("%.4f",location.getLatitude())
                + "\nLongitude : " + String.format("%.4f",location.getLongitude()));

        startIntentService(location);
        UtilityGeneral.saveCurrentLocation(this,location);
    }


    @OnClick(R.id.text_update)
    void updateLocationClick(View view)
    {
        // if getlastlocation does not work then request the device to get the current location.
        createLocationRequest();


        if(mLocationRequest!=null)
        {
            startLocationUpdates();
        }

    }


    // location code Ends


    // address resolution code

    @BindView(R.id.text_address)
    TextView text_address;


    private AddressResultReceiver mResultReceiver = new AddressResultReceiver();

    protected void startIntentService(Location location) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        startService(intent);
    }




    @SuppressLint("ParcelCreator")
    class AddressResultReceiver extends ResultReceiver {

        public AddressResultReceiver() {

            super(new Handler());
        }



        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string
            // or an error message sent from the intent service.

            String mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);

            if(mAddressOutput!=null && text_address!=null)
            {
                text_address.setText(mAddressOutput);
            }



            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
//                showToastMessage(getString(R.string.address_found));
            }

        }
    }

    // address resolution code ends






    // handle results for permission request


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case 1:

                if(grantResults.length>0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {

                    onConnected(null);

                }
                else
                {
                    showToastMessage(getString(R.string.toast_permission_denied_location));
                }


                break;


            case 2:

                if(grantResults.length>0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {

                    startLocationUpdates();

                }
                else
                {
                    showToastMessage(getString(R.string.toast_permission_denied_location));
                }


            default:

                break;
        }
    }


}
