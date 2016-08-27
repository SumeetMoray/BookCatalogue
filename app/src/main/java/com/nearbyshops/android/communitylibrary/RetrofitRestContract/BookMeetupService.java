package com.nearbyshops.android.communitylibrary.RetrofitRestContract;


import com.nearbyshops.android.communitylibrary.Model.BookMeetup;
import com.nearbyshops.android.communitylibrary.Model.BookReview;
import com.nearbyshops.android.communitylibrary.ModelEndpoint.BookMeetupEndpoint;
import com.nearbyshops.android.communitylibrary.ModelEndpoint.BookReviewEndpoint;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by sumeet on 2/4/16.
 */

public interface BookMeetupService {

    @GET("/api/v1/BookMeetup")
    Call<BookMeetupEndpoint> getMeetups(
            @Query("MemberLongitude") Double memberLongitude,
            @Query("MemberLatitude") Double memberLatitude,
            @Query("ProximityLimit") Double proximityMaximum,
            @Query("SearchString") String stringString,
            @Query("SortBy") String sortBy,
            @Query("Limit") Integer limit, @Query("Offset") Integer offset,
            @Query("metadata_only") Boolean metaonly
    );


    @GET("/api/v1/BookMeetup/{id}")
    Call<BookMeetup> getBookMeetup(@Path("id") int bookMeetupID);

    @POST("/api/v1/BookMeetup")
    Call<BookMeetup> insertBookMeetup(@Body BookMeetup bookMeetup);

    @PUT("/api/v1/BookMeetup/{id}")
    Call<ResponseBody> updateBookMeetup(@Body BookMeetup bookMeetup, @Path("id") int id);

    @DELETE("/api/v1/BookMeetup/{id}")
    Call<ResponseBody> deleteBookMeetup(@Path("id") int id);

}
