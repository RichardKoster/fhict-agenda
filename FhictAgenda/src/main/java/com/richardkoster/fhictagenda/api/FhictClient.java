package com.richardkoster.fhictagenda.api;


import com.richardkoster.fhictagenda.api.objects.LoginResult;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public class FhictClient {
    private static final String API_URL = "http://fhictapi.marijnvdwerf.nl";

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
    }
}