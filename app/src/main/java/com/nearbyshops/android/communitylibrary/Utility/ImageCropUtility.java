package com.nearbyshops.android.communitylibrary.Utility;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.nearbyshops.android.communitylibrary.R;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;



import java.io.File;

/**
 * Created by sumeet on 5/5/16.
 */
public class ImageCropUtility{



    public static int PICK_IMAGE_REQUEST = 21;


    public static void showFileChooser(AppCompatActivity context) {


        /*


        if(savedInstanceState==null) {
            // delete previous file in the cache - This will prevent accidently uploading the previous image
            File file = new File(getCacheDir().getPath() + "/" + "SampleCropImage.jpeg");
            file.delete();
        }

         */


        // An example of how the clear the cache in the activities
        /*
        if(savedInstanceState==null) {

            // delete previous file in the cache - This will prevent accidently uploading the previous image
            File file = new File(getCacheDir().getPath() + "/" + "SampleCropImage.jpeg");

        }
        */

        // code for checking the Read External Storage Permission and granting it.
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {


            /// / TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.


            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    2);

            return;

        }




        clearCache(context);

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        context.startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }


    // upload image after being picked up
    public static void startCropActivity(Uri sourceUri, AppCompatActivity activityContext) {


        final String SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage.jpeg";

        Uri destinationUri = Uri.fromFile(new File(activityContext.getCacheDir(), SAMPLE_CROPPED_IMAGE_NAME));

        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
//        options.setCompressionQuality(100);

        options.setToolbarColor(activityContext.getResources().getColor(R.color.cyan900));
        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ALL, UCropActivity.SCALE);
        options.setFreeStyleCropEnabled(true);


        // this function takes the file from the source URI and saves in into the destination URI location.
        UCrop.of(sourceUri, destinationUri)
                .withOptions(options)
                .withMaxResultSize(400,300)
                .start(activityContext);

        //.withMaxResultSize(500, 400)
        //.withAspectRatio(16, 9)
    }

    public static void clearCache(Context context)
    {
        File file = new File(context.getCacheDir().getPath() + "/" + "SampleCropImage.jpeg");
        file.delete();
    }

}
