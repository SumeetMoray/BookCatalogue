package com.nearbyshops.android.communitylibrary.RetrofitRestContract;


import com.nearbyshops.android.communitylibrary.Model.BookCategory;
import com.nearbyshops.android.communitylibrary.ModelEndpoint.BookCategoryEndpoint;

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

public interface BookCategoryService {

    @GET("/api/v1/BookCategory")
    Call<BookCategoryEndpoint> getBookCategories(
            @Query("ParentCategoryID") Integer parentID,
            @Query("SortBy") String sortBy,
            @Query("Limit") Integer limit, @Query("Offset") Integer offset,
            @Query("metadata_only") Boolean metaonly
    );


    @GET("/api/v1/BookCategory/{id}")
    Call<BookCategory> getBookCategory(@Path("id") int BookCategoryID);

    @POST("/api/v1/BookCategory")
    Call<BookCategory> insertBookCategory(@Body BookCategory bookCategory);

    @PUT("/api/v1/BookCategory/{id}")
    Call<ResponseBody> updateBookCategory(@Body BookCategory bookCategory, @Path("id") int id);

    @PUT("/api/v1/BookCategory/")
    Call<ResponseBody> updateBookCategoryBulk(@Body List<BookCategory> bookCategoryList);

    @DELETE("/api/v1/BookCategory/{id}")
    Call<ResponseBody> deleteItemCategory(@Path("id") int id);

}
