package com.nearbyshops.communityLibrary.database.RetrofitRestContract;


import com.nearbyshops.communityLibrary.database.Model.Member;
import com.nearbyshops.communityLibrary.database.ModelEndpoint.MemberEndpoint;

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

public interface MemberService {


    @GET("/api/v1/Member/Validate")
    Call<Member> validateMember(
            @Query("Password") String password,
            @Query("Username") String username
    );




    @GET("/api/v1/Member")
    Call<MemberEndpoint> getMember(
            @Query("SortBy") String sortBy,
            @Query("Limit") Integer limit, @Query("Offset") Integer offset,
            @Query("metadata_only") Boolean metaonly
    );


    @GET("/api/v1/Member/{id}")
    Call<Member> getMember(@Path("id") int memberID);

    @POST("/api/v1/Member")
    Call<Member> insertMember(@Body Member member);

    @PUT("/api/v1/Member/{id}")
    Call<ResponseBody> updateMember(@Body Member member, @Path("id") int id);

    @DELETE("/api/v1/Member/{id}")
    Call<ResponseBody> deleteMember(@Path("id") int id);

}
