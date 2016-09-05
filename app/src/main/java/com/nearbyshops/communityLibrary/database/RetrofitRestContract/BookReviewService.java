package com.nearbyshops.communityLibrary.database.RetrofitRestContract;


import com.nearbyshops.communityLibrary.database.Model.BookReview;
import com.nearbyshops.communityLibrary.database.ModelEndpoint.BookReviewEndpoint;

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

public interface BookReviewService {

    @GET("/api/v1/BookReview")
    Call<BookReviewEndpoint> getReviews(
            @Query("BookID")Integer bookID,
            @Query("MemberID")Integer memberID,
            @Query("GetMember")Boolean getMember,
            @Query("SortBy") String sortBy,
            @Query("Limit") Integer limit, @Query("Offset") Integer offset,
            @Query("metadata_only") Boolean metaonly
    );


    @GET("/api/v1/BookReview/{id}")
    Call<BookReview> getBookReview(@Path("id") int bookReviewID);

    @POST("/api/v1/BookReview")
    Call<BookReview> insertBookReview(@Body BookReview book);

    @PUT("/api/v1/BookReview/{id}")
    Call<ResponseBody> updateBookReview(@Body BookReview bookReview, @Path("id") int id);

    @PUT("/api/v1/BookReview/")
    Call<ResponseBody> updateBookReviewBulk(@Body List<BookReview> bookReviewsList);

    @DELETE("/api/v1/BookReview/{id}")
    Call<ResponseBody> deleteBookReview(@Path("id") int id);

}
