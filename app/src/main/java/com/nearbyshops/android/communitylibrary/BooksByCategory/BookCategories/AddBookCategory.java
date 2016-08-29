package com.nearbyshops.android.communitylibrary.BooksByCategory.BookCategories;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.nearbyshops.android.communitylibrary.Model.BookCategory;
import com.nearbyshops.android.communitylibrary.Model.Image;
import com.nearbyshops.android.communitylibrary.MyApplication;
import com.nearbyshops.android.communitylibrary.R;
import com.nearbyshops.android.communitylibrary.RetrofitRestContract.BookCategoryService;
import com.nearbyshops.android.communitylibrary.Utility.ImageCalls;
import com.nearbyshops.android.communitylibrary.Utility.ImageCropUtility;
import com.nearbyshops.android.communitylibrary.Utility.UtilityGeneral;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;


import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class AddBookCategory extends AppCompatActivity implements Callback<Image>, View.OnClickListener {

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.uploadImage) ImageView resultView;

    // Upload the image after picked up
    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 56;
    Image image = null;
    boolean isImageAdded = false;
    private int PICK_IMAGE_REQUEST = 21;


    @BindView(R.id.itemCategoryName) EditText itemCategoryName;
//    @BindView(R.id.itemCategoryDescription) EditText itemCategoryDescription;
    @BindView(R.id.addItemCategory) Button addItemCategory;



    final String IMAGES_END_POINT_URL = "/api/Images";


    public static final String ADD_ITEM_CATEGORY_INTENT_KEY = "add_category_intent_key";


    BookCategory parentCategory;


    BookCategory itemCategory = new BookCategory();



    Unbinder unbinder ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_item_category);
        unbinder = ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        parentCategory = getIntent().getParcelableExtra(ADD_ITEM_CATEGORY_INTENT_KEY);

    }





    @Override
    public void onClick(View view) {

        switch (view.getId())
        {

        }

    }




    void loadImage(String imagePath) {

        Picasso.with(this).load(UtilityGeneral.getServiceURL(null) + IMAGES_END_POINT_URL + imagePath).into(resultView);
    }


    void getDatafromEditText()
    {
        itemCategory.setParentCategoryID(parentCategory.getBookCategoryID());

        itemCategory.setBookCategoryName(itemCategoryName.getText().toString());
//        itemCategory.setCategoryDescription(itemCategoryDescription.getText().toString());
//        itemCategory.setIsLeafNode(isLeafNode.isChecked());


//        itemCategory.setAbstractNode(isAbstractNode.isChecked());
//        itemCategory.setDescriptionShort(descriptionShort.getText().toString());

    }


    void makeRetrofitRequest()
    {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UtilityGeneral.getServiceURL(MyApplication.getAppContext()))
                .addConverterFactory(GsonConverterFactory.create())
                .build();



        BookCategoryService itemCategoryService = retrofit.create(BookCategoryService.class);

        getDatafromEditText();

        Call<BookCategory> itemCategoryCall = itemCategoryService.insertBookCategory(itemCategory);

        itemCategoryCall.enqueue(new Callback<BookCategory>() {

            @Override
            public void onResponse(Call<BookCategory> call, Response<BookCategory> response) {

                BookCategory responseCategory = response.body();

//                displayResult(responseCategory);

                if(response.code() == 201)
                {

                    showToastMessage(getString(R.string.book_category_create_successful));
                }

            }

            @Override
            public void onFailure(Call<BookCategory> call, Throwable t) {

                showToastMessage(getString(R.string.network_not_available));
            }
        });

    }


    void showToastMessage(String message)
    {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }


//    void displayResult(ItemCategory itemCategory)
//    {
//        if(itemCategory!=null)
//        {
//            result.setText("Result : " + "\n"
//                    + itemCategory.getItemCategoryID() + "\n"
//                    + itemCategory.getCategoryName() + "\n"
//                    + itemCategory.getCategoryDescription() + "\n"
//                    + itemCategory.getImagePath() + "\n"
//                    + itemCategory.getParentCategoryID() + "\n");
//        }
//    }






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

        File file = new File(getCacheDir().getPath() + "/" + getString(R.string.cache_file_name));
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


    }


    /*

    // Code for Uploading Image

     */



    @OnClick(R.id.addItemCategory)
    public void addItemCategory()
    {

        if(isImageAdded)
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

            itemCategory.setImageURL(null);
            makeRetrofitRequest();

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
            itemCategory.setImageURL(response.body().getPath());

        }else
        {
            itemCategory.setImageURL(null);
        }

        makeRetrofitRequest();
    }


    @Override
    public void onFailure(Call<Image> call, Throwable t) {

        itemCategory.setImageURL(null);
        makeRetrofitRequest();
    }


}
