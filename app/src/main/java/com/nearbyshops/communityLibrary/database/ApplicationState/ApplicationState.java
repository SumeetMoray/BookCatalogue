package com.nearbyshops.communityLibrary.database.ApplicationState;


import com.nearbyshops.communityLibrary.database.MyApplication;

/**
 * Created by sumeet on 15/3/16.
 */
public class ApplicationState {

    static ApplicationState instance = null;


    MyApplication myApplication;


    private ApplicationState() {
    }


    public static ApplicationState getInstance()
    {

        if(instance == null)
        {
            instance = new ApplicationState();

            return instance;
        }

        return instance;
    }


    public MyApplication getMyApplication() {
        return myApplication;
    }

    public void setMyApplication(MyApplication myApplication) {
        this.myApplication = myApplication;
    }
}
