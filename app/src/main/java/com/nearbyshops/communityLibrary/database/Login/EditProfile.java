package com.nearbyshops.communityLibrary.database.Login;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nearbyshops.communityLibrary.database.DaggerComponentBuilder;

import com.nearbyshops.communityLibrary.database.Model.Image;
import com.nearbyshops.communityLibrary.database.Model.Member;
import com.nearbyshops.communityLibrary.database.R;

import com.nearbyshops.communityLibrary.database.RetrofitRestContract.MemberService;
import com.nearbyshops.communityLibrary.database.Utility.ImageCalls;
import com.nearbyshops.communityLibrary.database.Utility.ImageCropUtility;
import com.nearbyshops.communityLibrary.database.Utility.UtilityGeneral;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EditProfile extends AppCompatActivity implements Callback<Image> {


    @Inject
    MemberService memberService;

    // flag for knowing whether the image is changed or not
    boolean isImageChanged = false;
    boolean isImageRemoved = false;


    @BindView(R.id.member_name) EditText name;
    @BindView(R.id.profile_city) EditText city;
    @BindView(R.id.username) EditText username;

    Member memberForEdit;


    public static final String EDIT_MEMBER_INTENT_KEY = "EDIT_MEMBER_INTENT_KEY";


    public EditProfile() {

        DaggerComponentBuilder.getInstance()
                .getNetComponent().Inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ButterKnife.bind(this);

//        memberForEdit = getIntent().getParcelableExtra(EDIT_MEMBER_INTENT_KEY);

        memberForEdit = UtilityGeneral.getUser(this);


        if(memberForEdit!=null) {

            bindDataToEditText();

            loadImage(memberForEdit.getProfileImageURL());
        }



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }



    void loadImage(String imagePath) {

        Picasso.with(this).load(UtilityGeneral.getImageEndpointURL(this) + imagePath).into(resultView);
    }




    @OnClick(R.id.sign_up_button)
    public void UpdateButtonClick()
    {

        if(isImageChanged)
        {


            // delete previous Image from the Server
            ImageCalls.getInstance()
                    .deleteImage(
                            memberForEdit.getProfileImageURL(),
                            new DeleteImageCallback()
                    );


            if(isImageRemoved)
            {

                memberForEdit.setProfileImageURL("");

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
        if(memberForEdit!=null) {

            city.setText(memberForEdit.getCity());
            name.setText(memberForEdit.getMemberName());
            username.setText(memberForEdit.getUserName());
        }
    }


    boolean getDataFromEditText(Member member)
    {
        if(member!=null)
        {
            member.setCity(city.getText().toString());
            member.setMemberName(name.getText().toString());
            member.setUserName(username.getText().toString());


            if(username.getText().toString().equals(""))
            {
                showMessageSnackBar("Username / Password Cannot be empty !");
                return false;
            }


            return true;
        }

        return false;
    }



    public void retrofitPUTRequest()
    {



        if(!getDataFromEditText(memberForEdit)){

            return;
        }



        Call<ResponseBody> call = memberService.updateMember(
                                    memberForEdit,
                                    memberForEdit.getMemberID());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.code() == 200)
                {
                    Toast.makeText(EditProfile.this,getString(R.string.udate_successful_api_response),Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                showToastMessage(getString(R.string.network_not_available));
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

                showToastMessage(getString(R.string.api_response_image_upload_failed));

        }


        memberForEdit.setProfileImageURL(null);

        if(memberForEdit!=null)
        {
            memberForEdit.setProfileImageURL(image.getPath());
        }

        retrofitPUTRequest();
    }





    @Override
    public void onFailure(Call<Image> call, Throwable t) {


        showToastMessage(getString(R.string.api_response_image_upload_failed));

        memberForEdit.setProfileImageURL("");

        retrofitPUTRequest();

    }


    private class DeleteImageCallback implements Callback<ResponseBody> {


        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            if(response.code()==200)
            {
                showToastMessage(getString(R.string.api_response_previous_image_removed));
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {

            showToastMessage(getString(R.string.api_response_image_remove_failed));

        }
    }




    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    void showMessageSnackBar(String message) {

        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
    }

}
