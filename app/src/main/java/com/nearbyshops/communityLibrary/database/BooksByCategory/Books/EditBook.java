package com.nearbyshops.communityLibrary.database.BooksByCategory.Books;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nearbyshops.communityLibrary.database.DaggerComponentBuilder;
import com.nearbyshops.communityLibrary.database.Dialogs.DateDialog;
import com.nearbyshops.communityLibrary.database.Model.Book;
import com.nearbyshops.communityLibrary.database.Model.Image;
import com.nearbyshops.communityLibrary.database.R;
import com.nearbyshops.communityLibrary.database.RetrofitRestContract.BookService;
import com.nearbyshops.communityLibrary.database.Utility.ImageCalls;
import com.nearbyshops.communityLibrary.database.Utility.ImageCropUtility;
import com.nearbyshops.communityLibrary.database.Utility.UtilityGeneral;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EditBook extends AppCompatActivity implements Callback<Image> , DateDialog.NotifyDate{


    @Inject
    BookService bookService;

    // flag for knowing whether the image is changed or not
    boolean isImageChanged = false;
    boolean isImageRemoved = false;


    @BindView(R.id.bookName) EditText bookName;
    @BindView(R.id.authorName) EditText authorName;
    @BindView(R.id.bookDescription) EditText bookDescription;
    @BindView(R.id.publisher_name) EditText publisherName;
    @BindView(R.id.pages_total) EditText pagesTotal;
//    @BindView(R.id.set_date) TextView dateText;

    @BindView(R.id.itemID) EditText itemID;
    @BindView(R.id.saveButton) Button buttonUpdateItem;


//    public static final String ITEM_CATEGORY_INTENT_KEY = "itemCategoryIntentKey";
    public static final String ITEM_INTENT_KEY = "itemIntentKey";

    Book itemForEdit;
    //ItemCategory itemCategory;




    public EditBook() {

        DaggerComponentBuilder.getInstance()
                .getNetComponent().Inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        ButterKnife.bind(this);

        itemForEdit = getIntent().getParcelableExtra(ITEM_INTENT_KEY);
//        itemCategory = getIntent().getParcelableExtra(ITEM_CATEGORY_INTENT_KEY);



        if(itemForEdit!=null) {

            bindDataToEditText();

            loadImage(itemForEdit.getBookCoverImageURL());
        }



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }



    void loadImage(String imagePath) {

        Picasso.with(this).load(UtilityGeneral.getImageEndpointURL(this) + imagePath).into(resultView);
    }




    @OnClick(R.id.saveButton)
    public void UpdateButtonClick()
    {

        if(isImageChanged)
        {


            // delete previous Image from the Server
            ImageCalls.getInstance()
                    .deleteImage(
                            itemForEdit.getBookCoverImageURL(),
                            new DeleteImageCallback()
                    );


            if(isImageRemoved)
            {

                itemForEdit.setBookCoverImageURL("");

                retrofitPUTRequest();

            }else
            {



                ImageCalls
                        .getInstance()
                        .uploadPickedImage(
                                this,
                                REQUEST_CODE_READ_EXTERNAL_STORAGE,
                                this
                        );

            }


            // resetting the flag in order to ensure that future updates do not upload the same image again to the server
            isImageChanged = false;
            isImageRemoved = false;

        }else {

            retrofitPUTRequest();
        }
    }




    void bindDataToEditText()
    {
        if(itemForEdit!=null) {

            itemID.setText(String.valueOf(itemForEdit.getBookID()));
            bookName.setText(itemForEdit.getBookName());
            authorName.setText(itemForEdit.getAuthorName());

            bookDescription.setText(itemForEdit.getBookDescription());
            publisherName.setText(itemForEdit.getNameOfPublisher());

            pagesTotal.setText(String.valueOf(itemForEdit.getPagesTotal()));


            calendar.setTimeInMillis(itemForEdit.getDateOfPublishInMillis());
            setDateTimeLabel();






/*
            if(itemForEdit.getDateOfPublish()!=null)
            {
                dateText.setText(dateText.getText() + " : " + itemForEdit.getDateOfPublish());
            }*/


        }
    }












    public void onDateNotified(Calendar calendar) {

        date = new Timestamp(calendar.getTimeInMillis());

        dateText.setText("Set Date : " + date.toString());


        Log.d("date",date.toGMTString());
    }



    void getDataFromEditText(Book book)
    {
        if(book!=null)
        {

/*
            if(date!=null)
            {
                itemForEdit.setDateOfPublish(date);
                Log.d("date",itemForEdit.getDateOfPublish().toString());
            }*/

            itemForEdit.setBookName(bookName.getText().toString());
            itemForEdit.setAuthorName(authorName.getText().toString());
            itemForEdit.setBookDescription(bookDescription.getText().toString());
//        itemForEdit.setDateOfPublish(dateOfPublish.getText().toString());

//        itemForEdit.setDateOfPublish();


            itemForEdit.setDateOfPublish(new Timestamp(calendar.getTimeInMillis()));

            itemForEdit.setNameOfPublisher(publisherName.getText().toString());

            if(!pagesTotal.getText().toString().equals(""))
            {
                itemForEdit.setPagesTotal(Integer.parseInt(pagesTotal.getText().toString()));
            }

        }

    }



    public void retrofitPUTRequest()
    {



        getDataFromEditText(itemForEdit);


        Call<ResponseBody> itemCall = bookService.updateBook(
                                        itemForEdit,
                                        itemForEdit.getBookID());



        itemCall.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.code() == 200)
                {
                    Toast.makeText(EditBook.this,"Update Successful !",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {


                showToastMessage("Network request failed !");

            }
        });
    }





    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    /*
        Utility Methods
     */


    @BindView(R.id.uploadImage)
    ImageView resultView;



    void showToastMessage(String message)
    {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }




    @BindView(R.id.textChangePicture)
    TextView changePicture;


    @OnClick(R.id.removePicture)
    void removeImage()
    {

        File file = new File(getCacheDir().getPath() + "/" + "SampleCropImage.jpeg");
        file.delete();

        resultView.setImageDrawable(null);

        isImageChanged = true;
        isImageRemoved = true;
    }


    @OnClick(R.id.textChangePicture)
    void pickShopImage() {

        ImageCropUtility.showFileChooser(this);

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {

        super.onActivityResult(requestCode, resultCode, result);



        if (requestCode == ImageCropUtility.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && result != null
                && result.getData() != null) {


            Uri filePath = result.getData();

            //imageUri = filePath;

            if (filePath != null) {
                ImageCropUtility.startCropActivity(result.getData(),this);
            }

        }


        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {

            resultView.setImageURI(UCrop.getOutput(result));

            isImageChanged = true;
            isImageRemoved = false;


        } else if (resultCode == UCrop.RESULT_ERROR) {

            final Throwable cropError = UCrop.getError(result);

        }


    }





    /*

    // Code for Uploading Image

     */



    // Upload the image after picked up
    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 56;






    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case REQUEST_CODE_READ_EXTERNAL_STORAGE:

                UpdateButtonClick();

                break;
        }
    }

    @Override
    public void onResponse(Call<Image> call, Response<Image> response) {


        Image image = null;


        image = response.body();


        //// TODO: 31/3/16
        // check whether load image call is required. or Not

        loadImage(image.getPath());


        if (response.code() != 201) {

                showToastMessage("Image Upload error at the server !");

        }


        itemForEdit.setBookCoverImageURL(null);

        if(itemForEdit!=null)
        {
            itemForEdit.setBookCoverImageURL(image.getPath());
        }

        retrofitPUTRequest();
    }





    @Override
    public void onFailure(Call<Image> call, Throwable t) {


        showToastMessage("Image Upload failed !");

        itemForEdit.setBookCoverImageURL("");

        retrofitPUTRequest();

    }



    private class DeleteImageCallback implements Callback<ResponseBody> {


        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            if(response.code()==200)
            {
                showToastMessage("Previous Image removed !");
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {

            showToastMessage("Image remove failed !");

        }
    }




    // Code for Setting and Editing Date of Publish


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
        DialogFragment newFragment = new DateDialog();
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

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d ''yyyy");
        labelDateTime.setText("Date of Publish : " + dateFormat.format(calendar.getTime()));
//        labelDateTime.setText(calendar.getTime().toString());
    }

    // Code for Setting and Editing Date of Publish : Ends
}
