package com.nearbyshops.android.communitylibrary;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;
import com.nearbyshops.android.communitylibrary.ApplicationState.ApplicationState;

/**
 * Created by sumeet on 12/5/16.
 */
public class MyApplication extends Application{

    private static Context context;

    public void onCreate() {

        super.onCreate();


        Stetho.initializeWithDefaults(this);

        MyApplication.context = getApplicationContext();

//        LeakCanary.install(this);

        ApplicationState.getInstance().setMyApplication(this);

    }


    public static Context getAppContext() {

        return MyApplication.context;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }


}
