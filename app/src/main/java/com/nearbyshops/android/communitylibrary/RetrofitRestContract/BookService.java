package com.nearbyshops.android.communitylibrary.RetrofitRestContract;


import com.nearbyshops.android.communitylibrary.Model.Book;
import com.nearbyshops.android.communitylibrary.ModelEndpoint.BookEndpoint;

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

public interface BookService {

    @GET("/api/v1/Book")
    Call<BookEndpoint> getBooks(
            @Query("BookCategoryID") Integer bookCategoryID,
            @Query("SortBy") String sortBy,
            @Query("Limit") Integer limit, @Query("Offset") Integer offset,
            @Query("metadata_only") Boolean metaonly
    );


    @GET("/api/v1/Book/{id}")
    Call<Book> getBook(@Path("id") int bookID);

    @POST("/api/v1/Book")
    Call<Book> insertBook(@Body Book book);

    @PUT("/api/v1/Book/{id}")
    Call<ResponseBody> updateBook(@Body Book book, @Path("id") int id);

    @PUT("/api/v1/Book/")
    Call<ResponseBody> updateBookBulk(@Body List<Book> bookList);

    @DELETE("/api/v1/Book/{id}")
    Call<ResponseBody> deleteBook(@Path("id") int id);

}
