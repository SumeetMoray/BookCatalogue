package com.nearbyshops.android.communitylibrary.DaggerModules;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nearbyshops.android.communitylibrary.Model.BookReview;
import com.nearbyshops.android.communitylibrary.MyApplication;
import com.nearbyshops.android.communitylibrary.RetrofitRestContract.BookCategoryService;
import com.nearbyshops.android.communitylibrary.RetrofitRestContract.BookMeetupService;
import com.nearbyshops.android.communitylibrary.RetrofitRestContract.BookReviewService;
import com.nearbyshops.android.communitylibrary.RetrofitRestContract.BookService;
import com.nearbyshops.android.communitylibrary.RetrofitRestContract.FavouriteBookService;
import com.nearbyshops.android.communitylibrary.RetrofitRestContract.ItemCategoryService;
import com.nearbyshops.android.communitylibrary.RetrofitRestContract.MemberService;
import com.nearbyshops.android.communitylibrary.Utility.UtilityGeneral;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by sumeet on 14/5/16.
 */

        /*
        retrofit = new Retrofit.Builder()
                .baseUrl(UtilityGeneral.getServiceURL(context))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        */

@Module
public class NetModule {

    String serviceURL;

    // Constructor needs one parameter to instantiate.
    public NetModule() {

    }

    // Dagger will only look for methods annotated with @Provides
    @Provides
    @Singleton
    // Application reference must come from AppModule.class
    SharedPreferences providesSharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    @Singleton
    Cache provideOkHttpCache(Application application) {
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(application.getCacheDir(), cacheSize);
        return cache;
    }


    @Provides
    @Singleton
    Gson provideGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        //gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        return gsonBuilder
                .create();

        //.setDateFormat("yyyy-MM-dd hh:mm:ss.S")
        //.setDateFormat("yyyy-MM-dd")
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(Cache cache) {

        OkHttpClient client = new OkHttpClient()
                .newBuilder()
                .cache(cache)
                .build();

        // Cache cache

        // cache is commented out ... you can add cache by putting it back in the builder options
        //.cache(cache)

        return client;
    }


    //    @Singleton

    @Provides
    @Singleton
    Retrofit provideRetrofit(Gson gson, OkHttpClient okHttpClient) {

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(UtilityGeneral.getServiceURL(MyApplication.getAppContext()))
                .build();

        //        .client(okHttpClient)

        Log.d("applog","Retrofit : " + UtilityGeneral.getServiceURL(MyApplication.getAppContext()));


        return retrofit;
    }






    @Provides
    @Singleton
    BookCategoryService provideBookCategoryService(Retrofit retrofit)
    {
        BookCategoryService service = retrofit.create(BookCategoryService.class);

        return service;
    }


    @Provides
    @Singleton
    BookService provideBookService(Retrofit retrofit)
    {
        BookService service = retrofit.create(BookService.class);

        return service;
    }



    @Provides
    @Singleton
    MemberService provideMemberService(Retrofit retrofit)
    {
        MemberService service = retrofit.create(MemberService.class);
        return service;
    }

    @Provides
    @Singleton
    BookReviewService provideReviewService(Retrofit retrofit)
    {
        BookReviewService service = retrofit.create(BookReviewService.class);
        return service;
    }


    @Provides
    @Singleton
    FavouriteBookService provideFavouriteService(Retrofit retrofit)
    {
        FavouriteBookService service = retrofit.create(FavouriteBookService.class);
        return service;
    }




    @Provides
    @Singleton
    BookMeetupService bookMeetupService(Retrofit retrofit)
    {
        BookMeetupService service = retrofit.create(BookMeetupService.class);
        return service;
    }





//    @Provides
//    OrderService provideOrderService(Retrofit retrofit)
//    {
//        OrderService service = retrofit.create(OrderService.class);
//
//        return service;
//    }

}
