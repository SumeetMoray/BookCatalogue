package com.nearbyshops.android.communitylibrary.Utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;
import com.nearbyshops.android.communitylibrary.Dialogs.SortFIlterBookDialog;
import com.nearbyshops.android.communitylibrary.Model.Member;
import com.nearbyshops.android.communitylibrary.ModelUtility.SortOptionBooks;
import com.nearbyshops.android.communitylibrary.MyApplication;
import com.nearbyshops.android.communitylibrary.R;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by sumeet on 5/5/16.
 */
public class UtilityGeneral {


    public static void saveUserID(int user_id)
    {
        Context context = MyApplication.getAppContext();
        // get a handle to shared Preference
        SharedPreferences sharedPref;

        sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_name),
                MODE_PRIVATE);

        // write to the shared preference
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putInt(context.getString(R.string.user_id), user_id);

        editor.apply();
    }


    public static int getUserID(Context context) {

        context = MyApplication.getAppContext();

        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_name), MODE_PRIVATE);
        int user_id = sharedPref.getInt(context.getString(R.string.user_id), -1);

        return user_id;
    }


    public static void saveUser(Member member, Context context)
    {

        if(context == null)
        {
            return;
        }

        //Creating a shared preference

        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_name), MODE_PRIVATE);

        SharedPreferences.Editor prefsEditor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(member);
        prefsEditor.putString("member", json);
        prefsEditor.commit();
    }


    public static Member getUser(Context context)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_name), MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sharedPref.getString("member", "null");

        if(json.equals("null"))
        {

            return null;

        }else
        {
            Member member = gson.fromJson(json, Member.class);

            return member;
        }

    }


    public static void saveSortBooks(int sort_by, boolean whether_descending)
    {

        Context context = MyApplication.getAppContext();
        // get a handle to shared Preference
        SharedPreferences sharedPref;

        sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_name),
                MODE_PRIVATE);

        // write to the shared preference
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putInt(context.getString(R.string.sort_books_by), sort_by);
        editor.putBoolean(context.getString(R.string.whether_descending),whether_descending);

        editor.apply();
    }


    public static SortOptionBooks getBookSortOptions(Context context)
    {
        SortOptionBooks sortOptionBooks = new SortOptionBooks();

        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_name), MODE_PRIVATE);
        sortOptionBooks.setSort_by(sharedPref.getInt(context.getString(R.string.sort_books_by), SortFIlterBookDialog.SORT_BY_RATING));
        sortOptionBooks.setWhether_descending(sharedPref.getBoolean(context.getString(R.string.whether_descending),false));

        return sortOptionBooks;
    }





    public static void saveServiceURL(String service_url)
    {
        Context context = MyApplication.getAppContext();
        // get a handle to shared Preference
        SharedPreferences sharedPref;

        sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_name),
                MODE_PRIVATE);

        // write to the shared preference
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(
                context.getString(R.string.preference_service_url_key),
                service_url);

        editor.apply();
    }




    public static String getServiceURL(Context context) {

        context = MyApplication.getAppContext();

        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_name), MODE_PRIVATE);
        String service_url = sharedPref.getString(context.getString(R.string.preference_service_url_key), "http://192.168.1.34:8080");

        //"http://138.68.62.93:8080"
        //service_url = "http://localareademo-env.ap-southeast-1.elasticbeanstalk.com";

        return service_url;
    }


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public static String getImageEndpointURL(Context context)
    {
        return UtilityGeneral.getServiceURL(context) + "/api/Images";
    }


    public static String getConfigImageEndpointURL(Context context)
    {
        return UtilityGeneral.getServiceURL(context) + "/api/ServiceConfigImages";
    }
}
