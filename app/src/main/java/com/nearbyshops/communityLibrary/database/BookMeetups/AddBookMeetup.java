package com.nearbyshops.communityLibrary.database.BookMeetups;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nearbyshops.communityLibrary.database.DaggerComponentBuilder;
import com.nearbyshops.communityLibrary.database.Dialogs.DateDialogMeetup;
import com.nearbyshops.communityLibrary.database.Dialogs.TimePickerFragment;
import com.nearbyshops.communityLibrary.database.Model.BookMeetup;
import com.nearbyshops.communityLibrary.database.Model.Image;
import com.nearbyshops.communityLibrary.database.R;
import com.nearbyshops.communityLibrary.database.RetrofitRestContract.BookMeetupService;
import com.nearbyshops.communityLibrary.database.Utility.ImageCalls;
import com.nearbyshops.communityLibrary.database.Utility.ImageCropUtility;
import com.nearbyshops.communityLibrary.database.Utility.UtilityGeneral;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.sql.Timestamp;
import java.util.Calendar;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddBookMeetup extends AppCompatActivity implements Callback<Image>, DateDialogMeetup.NotifyDate,TimePickerFragment.NotifyTime{

    @BindView(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.uploadImage) ImageView resultView;

    
    // Upload the image after picked up
    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 56;
    Image image = null;
    boolean isImageAdded = false;
    private int PICK_IMAGE_REQUEST = 21;
    private int REQUEST_CODE_PICK_LAT_LON = 23;



    @BindView(R.id.meetupName) EditText meetupName;
    @BindView(R.id.purpose) EditText meetupPurpose;
    @BindView(R.id.venue) EditText venue;
    @BindView(R.id.latitude) EditText latitude;
    @BindView(R.id.longitude) EditText longitude;
    @BindView(R.id.addButton) Button addMeetupButton;



    final String IMAGES_END_POINT_URL = "/api/Images";
//    public static final String ADD_ITEM_CATEGORY_INTENT_KEY = "add_category_intent_key";

    @Inject
    BookMeetupService bookMeetupService;


    BookMeetup meetup = new BookMeetup();
    Unbinder unbinder ;


    public AddBookMeetup() {

        DaggerComponentBuilder.getInstance()
                .getNetComponent().Inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_book_meetup);
        unbinder = ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setDateTimeLabel();

    }



    void loadImage(String imagePath) {

        Picasso.with(this).load(UtilityGeneral.getServiceURL(null) + IMAGES_END_POINT_URL + imagePath).into(resultView);
    }


    void getDatafromEditText()
    {

        if(!latitude.getText().toString().equals(""))
        {
            meetup.setLatitude(Double.parseDouble(latitude.getText().toString()));
        }

        if(!longitude.getText().toString().equals(""))
        {
            meetup.setLongitude(Double.parseDouble(longitude.getText().toString()));
        }

        if(date!=null)
        {
            meetup.setDateAndTime(date);
        }

        meetup.setVenue(venue.getText().toString());
        meetup.setMeetupPurpose(meetupPurpose.getText().toString());
        meetup.setMeetupName(meetupName.getText().toString());

        meetup.setDateAndTime(new Timestamp(calendar.getTimeInMillis()));

    }





    void makeNetworkCall()
    {

        getDatafromEditText();


        Call<BookMeetup> call = bookMeetupService.insertBookMeetup(meetup);

        call.enqueue(new Callback<BookMeetup>() {
            @Override
            public void onResponse(Call<BookMeetup> call, Response<BookMeetup> response) {

                if(response.code() == 201)
                {
                    showToastMessage(getString(R.string.book_meetup_create_successful));
                }

            }

            @Override
            public void onFailure(Call<BookMeetup> call, Throwable t) {

                showToastMessage(getString(R.string.network_not_available));
            }
        });

    }


    void showToastMessage(String message)
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





    /*
        Utility Methods
     */




    void showMessageSnackBar(String message) {

        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();

    }




    /*

        // Code for Changeing / picking image and saving it in the cache folder


     */


    // code for changing / picking image and saving it in the cache folder


    @OnClick(R.id.removePicture)
    void removeImage()
    {

        File file = new File(getCacheDir().getPath() + "/" + "SampleCropImage.jpeg");
        file.delete();

        resultView.setImageDrawable(null);
        isImageAdded = false;
    }




    private static final String SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage.jpeg";
    private Uri mDestinationUri;

    @BindView(R.id.textChangePicture)
    TextView changePicture;


    @OnClick(R.id.textChangePicture)
    void pickShopImage() {

        resultView.setImageDrawable(null);

        ImageCropUtility.showFileChooser(this);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {

        super.onActivityResult(requestCode, resultCode, result);


        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && result != null
                && result.getData() != null) {

            Uri filePath = result.getData();

            if (filePath != null) {
                ImageCropUtility.startCropActivity(result.getData(),this);
            }

        }


        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {

            resultView.setImageURI(UCrop.getOutput(result));

            isImageAdded = true;


        } else if (resultCode == UCrop.RESULT_ERROR) {

            final Throwable cropError = UCrop.getError(result);
        }


        if(resultCode == RESULT_OK && requestCode == REQUEST_CODE_PICK_LAT_LON)
        {
            latitude.setText(String.valueOf(result.getDoubleExtra("latitude",0)));
            longitude.setText(String.valueOf(result.getDoubleExtra("longitude",0)));
        }
    }


    /*

    // Code for Uploading Image

     */



    // function for validating inputs
    boolean validate()
    {

        boolean isValid = true;

        if(meetupName.getText().toString().equals(""))
        {
            meetupName.setError(getString(R.string.validate_error_book_meetup_name));
            isValid = false;
        }

        if(meetupPurpose.getText().toString().equals(""))
        {
            meetupPurpose.setError(getString(R.string.validate_error_book_meetup_purpose));

            isValid = false;
        }

        if(latitude.getText().toString().equals(""))
        {
            latitude.setError(getString(R.string.validate_error_book_meetup_latitude));

            isValid = false;
        }
        else
        {
            double latitudeValue = Double.parseDouble(latitude.getText().toString());

            if(latitudeValue < -90 || latitudeValue > 90)
            {
                latitude.setError(getString(R.string.validate_error_book_meetup_latitude_not_valid));

                isValid = false;
            }
        }



        if(longitude.getText().toString().equals(""))
        {
            longitude.setError(getString(R.string.validate_error_longitude_not_set));

            isValid = false;
        }
        else
        {
            double longitudeValue = Double.parseDouble(longitude.getText().toString());

            if(longitudeValue < -180 || longitudeValue > 180)
            {
                longitude.setError(getString(R.string.validate_error_longitude_not_valid));

                isValid = false;
            }
        }



        if(calendar.getTimeInMillis()<=System.currentTimeMillis())
        {
            isValid = false;

            showMessageSnackBar(getString(R.string.validate_error_book_meetup_time));
        }



        return  isValid;
    }



    @OnClick(R.id.addButton)
    public void addItemCategory()
    {

        if(!validate())
        {
            return;
        }


        if(isImageAdded && isDateSet)
        {

            ImageCalls.getInstance()
                    .uploadPickedImage(
                            this,
                            REQUEST_CODE_READ_EXTERNAL_STORAGE,
                            this
                    );

        }
        else
        {

            String message = "";

                    if(!isImageAdded)
                    {
                        message = getString(R.string.Validate_error_add_meetup_poster);
                    }

                    if(!isDateSet)
                    {
                        message = message + getString(R.string.validate_error_add_date_and_time);
                    }

                    showMessageSnackBar(message);

//            meetup.setPosterURL(null);
//            makeNetworkCall();

        }
    }







    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case REQUEST_CODE_READ_EXTERNAL_STORAGE:

                addItemCategory();

                break;
        }
    }


    @Override
    public void onResponse(Call<Image> call, Response<Image> response) {

        // image upload successful

        if(response.code()==201)
        {
            meetup.setPosterURL(response.body().getPath());

        }else
        {
            meetup.setPosterURL(null);
        }

        makeNetworkCall();
    }


    @Override
    public void onFailure(Call<Image> call, Throwable t) {

        meetup.setPosterURL(null);
        makeNetworkCall();
    }




    Timestamp date;

    @BindView(R.id.set_date)
    TextView dateText;


    int year, month, day = -1;
    int hourOfDay, minutes = -1;

    Calendar calendar = Calendar.getInstance();

    @BindView(R.id.date_time_label)
    TextView labelDateTime;

    boolean isDateSet = false;


    @OnClick(R.id.set_date)
    void setDateClick()
    {
        DialogFragment newFragment = new DateDialogMeetup();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }




    @Override
    public void onDateNotified(int year, int month, int day) {


        this.year = year;
        this.month = month;
        this.day = day;

        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.DATE,day);

//        dateText.setText("Date of Meetup :\n" + calendar.getTime().toString());

        isDateSet = true;

        setDateTimeLabel();
    }



    void setDateTimeLabel()
    {
        labelDateTime.setText(calendar.getTime().toString());
    }




    @OnClick(R.id.set_time)
    void showTimePicker()
    {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }



    @Override
    public void onTimeNotified(int hourOfDay, int minute) {

        this.hourOfDay = hourOfDay;
        this.minutes = minute;
        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
        calendar.set(Calendar.MINUTE,minute);

        setDateTimeLabel();
    }




    @OnClick(R.id.pick_location_button)
    void pickLocationClick(){

        Intent intent = new Intent(this,PickLocationActivity.class);
        startActivityForResult(intent,REQUEST_CODE_PICK_LAT_LON);
    }

}
