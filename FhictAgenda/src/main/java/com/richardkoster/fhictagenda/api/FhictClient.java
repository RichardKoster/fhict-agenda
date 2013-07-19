package com.richardkoster.fhictagenda.api;


import com.richardkoster.fhictagenda.api.objects.LoginResult;
import com.richardkoster.fhictagenda.api.objects.User;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

public class FhictClient {
    private static final String API_URL = "http://waffle.marijnvdwerf.nl/api";

    public static Fhict getApi() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setServer(API_URL)
                .build();
        return restAdapter.create(Fhict.class);
    }

    public interface Fhict {
        @FormUrlEncoded
        @POST("/authenticate")
        void login(@Field("pcn") String pcn, @Field("password") String password, @Field("client_id") String clientId, Callback<LoginResult> cb);

        @GET("/me")
        void getUser(@Query("token") String accessToken, Callback<User> cb);
    }

}