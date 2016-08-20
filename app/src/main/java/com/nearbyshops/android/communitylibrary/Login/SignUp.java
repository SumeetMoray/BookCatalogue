package com.nearbyshops.android.communitylibrary.Login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nearbyshops.android.communitylibrary.DaggerComponentBuilder;
import com.nearbyshops.android.communitylibrary.Model.Image;
import com.nearbyshops.android.communitylibrary.Model.Member;
import com.nearbyshops.android.communitylibrary.R;
import com.nearbyshops.android.communitylibrary.RetrofitRestContract.MemberService;
import com.nearbyshops.android.communitylibrary.Utility.ImageCalls;
import com.nearbyshops.android.communitylibrary.Utility.ImageCropUtility;
import com.nearbyshops.android.communitylibrary.Utility.UtilityGeneral;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUp extends AppCompatActivity implements Callback<Image> {


    @Inject
    MemberService memberService;

    boolean isImageAdded = false;

    @BindView(R.id.member_name)
    EditText name;

    @BindView(R.id.profile_city)
    EditText city;

    @BindView(R.id.username)
    EditText username;

    @BindView(R.id.password)
    EditText password;

    @BindView(R.id.confirm_password)
    EditText confirm_password;

    @BindView(R.id.sign_up_button)
    Button sign_up;

    Member member;


    public SignUp() {

        DaggerComponentBuilder.getInstance()
                .getNetComponent()
                .Inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if (savedInstanceState == null) {
            // delete previous file in the cache - This will prevent accidently uploading the previous image
            File file = new File(getCacheDir().getPath() + "/" + "SampleCropImage.jpeg");
            file.delete();

            //showMessageSnackBar("File delete Status : " + String.valueOf(file.delete()));

        }
    }


    void addNewProfile(String imagePath) {


        Member memberForEdit = new Member();

        memberForEdit.setProfileImageURL(imagePath);


        memberForEdit.setCity(city.getText().toString());
        memberForEdit.setMemberName(name.getText().toString());
        memberForEdit.setUserName(username.getText().toString());

        if(password.getText().toString().equals(confirm_password.getText().toString()))
        {
            memberForEdit.setPassword(password.getText().toString());
        }else
        {

            showMessageSnackBar("Confirm Password do not match !");
            return;
        }


        if(username.getText().toString().equals("") || password.getText().toString().equals(""))
        {
            showMessageSnackBar("Username / Password Cannot be empty !");
            return;
        }


        // Make a network call

        Call<Member> call = memberService.insertMember(memberForEdit);

        call.enqueue(new Callback<Member>() {
            @Override
            public void onResponse(Call<Member> call, Response<Member> response) {

                if (response.code() == 201) {

                    showToastMessage("Signed Up Successfully !");
                }

            }

            @Override
            public void onFailure(Call<Member> call, Throwable t) {

                showToastMessage("Network request failed ! ");
            }
        });

    }




    @OnClick(R.id.sign_up_button)
    void addItem() {

        if (isImageAdded) {

            ImageCalls.getInstance().uploadPickedImage(this, REQUEST_CODE_READ_EXTERNAL_STORAGE, this);

        } else {
            addNewProfile(null);
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }







    /*
        Utility Methods
     */


    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.uploadImage)
    ImageView resultView;



    void showMessageSnackBar(String message) {

        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
    }


    void loadImage(String imagePath) {

        Picasso.with(this).load(UtilityGeneral.getImageEndpointURL(this) + imagePath).into(resultView);

        //getServiceURL() + IMAGES_END_POINT_URL +
    }





    /*
        // Code for Changeing / picking image and saving it in the cache folder
    */


    // code for changing / picking image and saving it in the cache folder


    @OnClick(R.id.removePicture)
    void removeImage() {

        File file = new File(getCacheDir().getPath() + "/" + "SampleCropImage.jpeg");
        file.delete();

        resultView.setImageDrawable(null);

        // reset the flag to reflect the status of image addition
        isImageAdded = false;
    }


    //private static final String SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage.jpeg";
    //private Uri mDestinationUri;

    @BindView(R.id.textChangePicture)
    TextView changePicture;


    @OnClick(R.id.textChangePicture)
    void pickShopImage() {

        //  mDestinationUri = Uri.fromFile(new File(getCacheDir(), SAMPLE_CROPPED_IMAGE_NAME));

        //Log.d("applog", "Cache Dir Path : " + getCacheDir().getPath());

        resultView.setImageDrawable(null);
        //Crop.pickImage(this);

        ImageCropUtility.showFileChooser(this);

        //showFileChooser();
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

                ImageCropUtility.startCropActivity(result.getData(), this);

                //startCropActivity(result.getData());
            }
        }


        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {

            resultView.setImageURI(UCrop.getOutput(result));

            isImageAdded = true;


        } else if (resultCode == UCrop.RESULT_ERROR) {

            final Throwable cropError = UCrop.getError(result);

        }// request crop
    }



    /*

    // Code for Uploading Image

     */


    // Upload the image after picked up
    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 56;

    Image image = null;



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case REQUEST_CODE_READ_EXTERNAL_STORAGE:

                //uploadPickedImage();

                addItem();

                break;
        }
    }


    @Override
    public void onResponse(Call<Image> call, Response<Image> response) {

        image = response.body();

        loadImage(image.getPath());

        if (image != null) {

            addNewProfile(image.getPath());

        } else {

            addNewProfile(null);

            showToastMessage("Image upload failed !");
        }

    }

    @Override
    public void onFailure(Call<Image> call, Throwable t) {


        showToastMessage("Image upload failed !");

        addNewProfile(null);
    }


    void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


}
