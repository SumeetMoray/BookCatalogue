package com.nearbyshops.android.communitylibrary.Login;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.mobsandgeeks.saripaar.annotation.Password;
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
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUp extends AppCompatActivity implements Callback<Image>, Validator.ValidationListener {


    boolean isFinalValidation = false;

    @Inject
    MemberService memberService;

    boolean isImageAdded = false;

    @BindView(R.id.text_input_member_name)
    TextInputLayout textInputName;

    @BindView(R.id.text_input_profile_city)
    TextInputLayout textInputCity;

    @BindView(R.id.text_input_username)
    TextInputLayout textInputUsername;

    @BindView(R.id.text_input_password)
    TextInputLayout textInputPassword;

    @BindView(R.id.text_input_confirm_password)
    TextInputLayout textInputConfirmPassword;

    @BindView(R.id.member_name) @NotEmpty @Order(1)
    EditText name;

    @BindView(R.id.profile_city)
    EditText city;

    @BindView(R.id.username) @NotEmpty @Order(2)
    EditText username;

    @BindView(R.id.password) @Password(min = 4, scheme = Password.Scheme.ANY) @Order(3)
    EditText password;

    @BindView(R.id.confirm_password) @ConfirmPassword @Order(4)
    EditText confirm_password;

    @BindView(R.id.sign_up_button)
    Button sign_up;

    Member member;


    Validator validator;



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


        validator = new Validator(this);
        validator.setValidationListener(this);

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

        memberForEdit.setPassword(password.getText().toString());

/*
        if(password.getText().toString().equals(confirm_password.getText().toString()))
        {

        }else
        {

//            textInputConfirmPassword.setError(getString(R.string.validate_error_confirm_password_not_matching));

            showMessageSnackBar(getString(R.string.validate_error_confirm_password_not_matching));
            return;
        }


        if(username.getText().toString().equals("") || password.getText().toString().equals(""))
        {


            showMessageSnackBar(getString(R.string.validation_error_username_password_empty));
            return;
        }*/


        // Make a network call

        Call<Member> call = memberService.insertMember(memberForEdit);

        call.enqueue(new Callback<Member>() {
            @Override
            public void onResponse(Call<Member> call, Response<Member> response) {

                if (response.code() == 201) {

                    showToastMessage(getString(R.string.toast_sign_up_successful));
                }

            }

            @Override
            public void onFailure(Call<Member> call, Throwable t) {

                showToastMessage(getString(R.string.network_not_available));
            }
        });

    }




    @OnClick(R.id.sign_up_button)
    void addItem() {

        isFinalValidation = true;
        validator.setValidationMode(Validator.Mode.BURST);
        validator.validate(true);


    }


    void signUpAfterValidate()
    {


        if (isImageAdded) {

            ImageCalls.getInstance().uploadPickedImage(this, REQUEST_CODE_READ_EXTERNAL_STORAGE, this);

        } else {

//            showMessageSnackBar("Profile Image Not Added !");

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Profile Image Not Added !")
                    .setMessage("You have not added profile Image. Do you want to continue ?")
                    .setPositiveButton(R.string.alert_dialog_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            addNewProfile(null);
                        }
                    })
                    .setNegativeButton(R.string.alert_dialog_no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .show();
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

            showToastMessage(getString(R.string.api_response_image_upload_failed));
        }

    }

    @Override
    public void onFailure(Call<Image> call, Throwable t) {


        showToastMessage(getString(R.string.api_response_image_upload_failed));

        addNewProfile(null);
    }


    void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }






    @OnTextChanged(R.id.password)
    void textChanedPassword()
    {
        validator.setValidationMode(Validator.Mode.IMMEDIATE);
        validator.validateTill(password);
    }

    @OnTextChanged(R.id.username)
    void textChangedUsername()
    {
        validator.setValidationMode(Validator.Mode.IMMEDIATE);
        validator.validateTill(username);
    }

    @OnTextChanged(R.id.confirm_password)
    void textChangedConfirmPassword()
    {
        validator.setValidationMode(Validator.Mode.IMMEDIATE);
        validator.validateTill(confirm_password);
    }


    void clearErrors()
    {
//        textInputConfirmPassword.setError(null);
//        textInputUsername.setError(null);
//        textInputPassword.setError(null);

        textInputConfirmPassword.setErrorEnabled(false);
        textInputUsername.setErrorEnabled(false);
        textInputPassword.setErrorEnabled(false);
    }


    @Override
    public void onValidationSucceeded() {

//        showMessageSnackBar("Validation Success !");

        clearErrors();

        if(isFinalValidation)
        {
            signUpAfterValidate();
        }
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {

        // reset the flag
        isFinalValidation = false;

        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof EditText) {

                if(view.getId()==R.id.confirm_password)
                {
                    textInputConfirmPassword.setError(message);

                }else if(view.getId()==R.id.username)
                {
                    textInputUsername.setError(message);

                }else if(view.getId()==R.id.password)
                {
                    textInputPassword.setError(message);
                }else
                {
                    ((EditText) view).setError(message);
                }


            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }

    }
}
